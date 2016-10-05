package org.knoesis.rdf.sp.parser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.apache.jena.sparql.core.Quad;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.concurrent.PipedSPTripleIterator;
import org.knoesis.rdf.sp.concurrent.PipedStringStream;
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
	public void parseFile(String in, String extension, String rep, String dirOut) {
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
        final String filein = in;
        final String dirout = dirOut;
        final String ext = extension;
        final String ds = this.getDsName();
    	PipedSPTripleIterator<String> writerIter = new PipedSPTripleIterator<String>(Constants.BUFFER_SIZE, true);
    	final PipedStringStream<String> writerInputStream = new PipedStringStream<String>(writerIter);

		Runnable transformer = new Runnable(){
        	@Override
        	public void run(){
        		SPProcessor processor = new SPProcessor(conRep);
        		processor.setDirout(dirout);
        		processor.setExt(ext);
        		processor.setFilein(filein);
        		processor.setIsinfer(isInfer);
        		processor.setIszip(isZip);
        		processor.setDsName(ds);
        		
        		writerInputStream.start();
            	
        		while (iter.hasNext()){
        			Triple triple = iter.next();
        			writerInputStream.string(processor.process(triple));
        			// Put the output to the writerInputStream
        		}
        		
        		processor.finish();
        		processor.close();
        		
        		writerInputStream.finish();

        		iter.close();
        		inputStream.finish();
       	}
		};
		
		consumerExecutor.submit(transformer);
		ExecutorService writerExecutor = Executors.newSingleThreadExecutor();
        Runnable writer = new Runnable(){
        	@Override
        	public void run(){
        		// Read the data from stream to file
    			try {
            		BufferedWriter buffWriter = RDFWriteUtils.getBufferedWriter(filein, isZip);
            		while (writerIter.hasNext()){
            			buffWriter.write(writerIter.next());
            		}
            		buffWriter.close();
            	} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        };
        
		writerExecutor.submit(writer);
		writerExecutor.shutdown();
		try {
			writerExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
