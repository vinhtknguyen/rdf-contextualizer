package org.knoesis.rdf.sp.parser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.converter.ContextConverterFactory;
import org.knoesis.rdf.sp.converter.ContextualRepresentationConverter;
import org.knoesis.rdf.sp.inference.ContextualInference;
import org.knoesis.rdf.sp.model.SPTriple;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;
import org.knoesis.rdf.sp.utils.SPStats;

import com.romix.scala.collection.concurrent.TrieMap;

public class TripleParser extends SPParser {
	
	final static Logger logger = Logger.getLogger(TripleParser.class);

    PipedRDFIterator<org.apache.jena.graph.Triple> iter = null;
    PipedRDFStream<org.apache.jena.graph.Triple> inputStream = null;

	public TripleParser(){
        iter = new PipedRDFIterator<org.apache.jena.graph.Triple>(Constants.BUFFER_SIZE, true);
        inputStream = new PipedTriplesStream(iter);
	}
	
	@Override
	public void parseFile(String in, String ext, String rep, String dirOut) {
		// PipedRDFStream and PipedRDFIterator need to be on different threads
		ExecutorService executor1 = Executors.newSingleThreadExecutor();

		// Create a runnable for our parser thread
		Runnable parser = new Runnable() {

			@Override
			public void run() {
				// Call the parsing process.
				RDFDataMgr.parse(inputStream, in, null);
			}
		};

		// Start the parser on another thread
		executor1.submit(parser);
  
		ExecutorService executor2 = Executors.newWorkStealingPool();
        AtomicInteger atomicInt = new AtomicInteger(0);
        final boolean isZip = this.isZip();
        final boolean isInfer = this.isInfer();
        final String conRep = rep;

		Runnable transformer = new Runnable(){
        	@Override
        	public void run(){
        		int num = atomicInt.updateAndGet(n -> n + 1);
        		String fileOut = RDFWriteUtils.genFileOutForThread(in, dirOut, num, ext, isZip);
        		BufferedWriter writer = RDFWriteUtils.getBufferedWriter(fileOut, isZip);
				// Write the credentials
				String cred = "# This file is generated by SP-Conversion.\n";
				try {
					writer.write(cred);
					List<SPTriple> triples = new ArrayList<SPTriple>();
					Map<String,String> prefixMapping = new TrieMap<String,String>();
					Map<String,String> trie = new TrieMap<String,String>();
					
					ContextualInference reasoner = new ContextualInference();
					ContextualRepresentationConverter converter = ContextConverterFactory.createConverter(conRep);
					
					long start = System.currentTimeMillis();
				
		       		while (iter.hasNext()) {
		       			Triple triple = iter.next();
						if (isInfer){
	        				// infer new triples and add them to the list
	        				triples.addAll(reasoner.infer(converter.transformTriple(writer, triple, ext)));
	        			} else {
		        			triples.addAll(converter.transformTriple(writer, triple, ext));
	        			}
	        			prefixMapping = RDFWriteUtils.extractPrefixesfromTriples(triples, prefixMapping, trie);
	        			writer.write(RDFWriteUtils.printTriples(triples, prefixMapping, trie, ext));
	        			triples.clear();
	        		}
		       		if (isInfer) {
			       		// Generate the generic property triples
		       			triples.addAll(reasoner.generateGenericPropertyTriplesPerFile());
	        			prefixMapping = RDFWriteUtils.extractPrefixesfromTriples(triples, prefixMapping, trie);
		       			writer.write(RDFWriteUtils.printTriples(triples, prefixMapping, trie, ext));
		       		}
					writer.close();
					
	        		SPStats.reportSystem(start, rep, (isInfer?"infer":"no-infer"), ext, in, fileOut);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
		};
       	
		executor2.submit(transformer);

	}

}