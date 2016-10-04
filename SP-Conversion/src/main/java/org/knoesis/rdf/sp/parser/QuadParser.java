package org.knoesis.rdf.sp.parser;


import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedQuadsStream;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.sparql.core.Quad;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.converter.ContextConverterFactory;
import org.knoesis.rdf.sp.converter.ContextualRepresentationConverter;
import org.knoesis.rdf.sp.inference.ContextualInference;
import org.knoesis.rdf.sp.model.SPTriple;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;
import org.knoesis.rdf.sp.utils.SPStats;

import com.romix.scala.collection.concurrent.TrieMap;

public class QuadParser extends SPParser{
	
	final static Logger logger = Logger.getLogger(QuadParser.class);

	protected PipedRDFIterator<Quad> iter;
	protected PipedRDFStream<Quad> inputStream;
	protected Map<String,String> parserTrie = new TrieMap<String,String>();
	
	@Override
	public void parseFile(String in, String ext, String rep, String dirOut) {
		System.out.println("Initialize stream for file " + in);
		
        iter = new PipedRDFIterator<Quad>(Constants.BUFFER_SIZE, true);
        inputStream = new PipedQuadsStream(iter);
        // PipedRDFStream and PipedRDFIterator need to be on different threads
        ExecutorService executor1 = Executors.newSingleThreadExecutor();

        // Create a runnable for our parser thread
        Runnable parser = new Runnable() {

            @Override
            public void run() {
                // Call the parsing process.
        		System.out.println("Started streaming file " + in + " in thread " + Thread.currentThread());
                RDFDataMgr.parse(inputStream, in, Lang.NQUADS);
            }
        };

        // Start the parser on another thread
        executor1.submit(parser);
        AtomicInteger atomicInt = new AtomicInteger(0);

        final boolean isZip = this.isZip();
        final boolean isInfer = this.isInfer();
        final String conRep = rep;
        
        ExecutorService executor2 = Executors.newFixedThreadPool(2);
        Runnable transformer = new Runnable(){
        	@Override
        	public void run(){
        		
        	}
        };
		int num = atomicInt.updateAndGet(n -> n + 1);
		String fileOut = RDFWriteUtils.genFileOutForThread(in, dirOut, num, ext, isZip);
		BufferedWriter writer = RDFWriteUtils.getBufferedWriter(fileOut, isZip);
		long start = System.currentTimeMillis();
		// Write the credentials
		try {
			writer.write(Constants.WRITE_FILE_PREFIX);
			List<SPTriple> triples = new ArrayList<SPTriple>();
			Map<String,String> prefixMapping = new TrieMap<String,String>();
			Map<String,String> trie = new TrieMap<String,String>();
			RDFWriteUtils.loadPrefixesToTrie(trie);

			ContextualInference reasoner = new ContextualInference();
			ContextualRepresentationConverter converter = ContextConverterFactory.createConverter(conRep);
       		while (iter.hasNext()) {
    		    Quad quad = iter.next();
    			if (isInfer){
    				// infer new triples and add them to the list
    				triples.addAll(reasoner.infer(converter.transformQuad(writer, quad, ext)));
    			} else {
        			triples.addAll(converter.transformQuad(writer, quad, ext));
    			}
    			writer.write(RDFWriteUtils.printTriples(triples, prefixMapping, trie, ext));
    			triples.clear();
    		}
       		
       		if (isInfer) {
	       		// Generate the generic property triples
       			triples.addAll(reasoner.generateGenericPropertyTriplesPerFile());
       			writer.write(RDFWriteUtils.printTriples(triples, prefixMapping, trie, ext));
       		}
			writer.close();
			
    		SPStats.reportSystem(start, rep, (isInfer?"infer":"no-infer"), ext, in, fileOut);
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

//		executor2.submit(transformer);
		executor1.shutdown();
//		executor2.shutdown();
	}
}
