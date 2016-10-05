package org.knoesis.rdf.sp.parser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.runnable.SPProcessor;
import org.knoesis.rdf.sp.utils.Constants;

public class TripleParser extends SPParser {
	
	final static Logger logger = Logger.getLogger(TripleParser.class);


	public TripleParser(){
	}
	
	@Override
	public void parseFile(String in, String ext, String rep, String dirOut) {
		// PipedRDFStream and PipedRDFIterator need to be on different threads
		PipedRDFIterator<org.apache.jena.graph.Triple> iter = new PipedRDFIterator<org.apache.jena.graph.Triple>(Constants.BUFFER_SIZE, true);
		final PipedRDFStream<org.apache.jena.graph.Triple> inputStream = new PipedTriplesStream(iter);

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
  
        AtomicInteger atomicInt = new AtomicInteger(0);
        final boolean isZip = this.isZip();
        final boolean isInfer = this.isInfer();
        final String conRep = rep;

		ExecutorService executor2 = Executors.newWorkStealingPool();
		Runnable transformer = new Runnable(){
        	@Override
        	public void run(){
        		SPProcessor processor = new SPProcessor();
        		processor.setDirout(dirOut);
        		processor.setExt(ext);
        		processor.setFilein(in);
        		processor.setIsinfer(isInfer);
        		processor.setIszip(isZip);
        		processor.setRep(conRep);
        		processor.setThreadnum(atomicInt.updateAndGet(n -> n + 1));
        		processor.start();

        		while (iter.hasNext()){
        			Triple triple = iter.next();
        			processor.process(triple);
        		}
        		
        		processor.finish();
        		processor.report();
        		processor.close();
        		iter.close();
        		inputStream.finish();
       	}
		};
		executor2.submit(transformer);
		executor2.shutdown();
		executor1.shutdown();
		try {
			executor2.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
			executor1.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
