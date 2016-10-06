package org.knoesis.rdf.sp.parser;

import java.io.BufferedWriter;
import java.io.IOException;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.concurrent.PipedNodesIterator;
import org.knoesis.rdf.sp.concurrent.PipedNodesStream;
import org.knoesis.rdf.sp.runnable.SPProcessor;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;

public class TripleParser extends SPParser {
	
	final static Logger logger = Logger.getLogger(TripleParser.class);

	public TripleParser() {
	}

	public TripleParser(long uuidInitNum, String _uuidInitStr) {
		super(uuidInitNum, _uuidInitStr);
	}

	@Override
	public void parseFile(String in, String extension, String rep, String fileout) {
		// PipedRDFStream and PipedRDFIterator need to be on different threads
		PipedRDFIterator<Triple> processorIter = new PipedRDFIterator<Triple>(Constants.BUFFER_SIZE, true);
		final PipedRDFStream<Triple> processorInputStream = new PipedTriplesStream(processorIter);

		// Create a runnable for our parser thread
		Runnable parser = new Runnable() {

			@Override
			public void run() {
				// Call the parsing process.
				RDFDataMgr.parse(processorInputStream, in, null);
			}
		};

		// Start the parser on another thread
		producerExecutor.submit(parser);
  
        final boolean isZip = this.isZip();
        final boolean isInfer = this.isInfer();
        final String conRep = rep;
        final String ext = extension;
        final String ontoDir = this.getOntoDir();
        final String ds = this.getDsName();

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
        		
		
        	}
        };
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
        		
        	}
        };
        producerExecutor.submit(writerRunner);
	}

}
