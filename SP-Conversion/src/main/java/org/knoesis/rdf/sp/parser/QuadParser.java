package org.knoesis.rdf.sp.parser;


import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedQuadsStream;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.sparql.core.Quad;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.concurrent.PipedNodesIterator;
import org.knoesis.rdf.sp.concurrent.PipedNodesStream;
import org.knoesis.rdf.sp.runnable.SPProcessor;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;

import com.romix.scala.collection.concurrent.TrieMap;

public class QuadParser extends SPParser{
	
	final static Logger logger = Logger.getLogger(QuadParser.class);

	protected Map<String,String> parserTrie = new TrieMap<String,String>();

	public QuadParser() {
		super();
	}

	public QuadParser(long _uuidInitNum, String _uuidInitStr) {
		super(_uuidInitNum, _uuidInitStr);
	}

	
	@Override
	public void parseFile(String in, String extension, String rep, String fileout) {
		
        // PipedRDFStream and PipedRDFIterator need to be on different threads
		
		PipedRDFIterator<Quad> processorIter = new PipedRDFIterator<Quad>(Constants.BUFFER_SIZE, true);
		final PipedRDFStream<Quad> processorInputStream = new PipedQuadsStream(processorIter);
        final boolean isZip = this.isZip();
        final boolean isInfer = this.isInfer();
        final String ext = extension;
        final String conRep = rep;
        final String ontoDir = this.getOntoDir();
        final String ds = this.getDsName();

        Runnable parser = new Runnable() {

            @Override
            public void run() {
                // Call the parsing process.
                RDFDataMgr.parse(processorInputStream, in, Lang.NQUADS);
            }
        };

        // Start the parser on another thread
        producerExecutor.submit(parser);
        
		PipedRDFIterator<String> writerIter = new PipedNodesIterator<String>(Constants.BUFFER_SIZE, true);
		final PipedNodesStream<String> writerInputStream = new PipedNodesStream<String>(writerIter);
		
        Runnable transformer = new Runnable(){
        	@Override
        	public void run(){
        		
          		SPProcessor processor = new SPProcessor(conRep, uuidInitNum, uuidInitStr);
        		processor.setIsinfer(isInfer);
        		processor.setDsName(ds);
        		processor.setOntoDir(ontoDir);
        		processor.setExt(ext);
        		processor.start();
        		
        		while (processorIter.hasNext()){
        			// Put the output to the writerInputStream
					writerInputStream.node(processor.process(processorIter.next()));
        		}
        		
        		processor.finish();
        		
        		processorIter.close();
        		processorInputStream.finish();
        		System.out.println("Done tranforming stream for " + in);
        		
        	}
        };
        producerExecutor.submit(transformer);
        producerExecutor.submit(transformer);
        producerExecutor.submit(transformer);

        Runnable writerRunner = new Runnable(){
        	@Override
        	public void run(){
        		
        		BufferedWriter buffWriter = RDFWriteUtils.getBufferedWriter(fileout, isZip);
        		while (writerIter.hasNext()){
        			try {
            			// Put the output to the writerInputStream
						buffWriter.write(writerIter.next());
					} catch (IOException e) {
						e.printStackTrace();
					} finally{
						try {
							buffWriter.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
        		}
        		
        		writerIter.close();
        		writerInputStream.finish();
        		System.out.println("Done writing stream for " + in);
       		
        	}
        };
        producerExecutor.submit(writerRunner);
	}
}
