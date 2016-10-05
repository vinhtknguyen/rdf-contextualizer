package org.knoesis.rdf.sp.parser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.concurrent.PipedNodeIterator;
import org.knoesis.rdf.sp.concurrent.PipedNodeStream;
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
		PipedRDFIterator<org.apache.jena.graph.Triple> iter = new PipedRDFIterator<org.apache.jena.graph.Triple>(Constants.BUFFER_SIZE, true);
		final PipedRDFStream<org.apache.jena.graph.Triple> inputStream = new PipedTriplesStream(iter);

		// Create a runnable for our parser thread
		Runnable parser = new Runnable() {

			@Override
			public void run() {
				// Call the parsing process.
				RDFDataMgr.parse(inputStream, in, null);
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

		Runnable transformer = new Runnable(){
        	@Override
        	public void run(){
        		SPProcessor processor = new SPProcessor(conRep);
        		processor.setExt(ext);
        		processor.setIsinfer(isInfer);
        		processor.setDsName(ds);
        		processor.setOntoDir(ontoDir);
        		
            	
    			BufferedWriter buffWriter = RDFWriteUtils.getBufferedWriter(fileout, isZip);
    			while (iter.hasNext()){
        			try {
            			// Put the output to the writerInputStream
						buffWriter.write(processor.process(iter.next()));
					} catch (IOException e) {
						e.printStackTrace();
					}
        		}
        		
        		processor.finish();
        		
        		iter.close();
        		inputStream.finish();
       	}
		};
		
		consumerExecutor.submit(transformer);
	}

}
