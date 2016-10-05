package org.knoesis.rdf.sp.parser;


import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedQuadsStream;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.sparql.core.Quad;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.runnable.SPProcessor;
import org.knoesis.rdf.sp.utils.Constants;

import com.romix.scala.collection.concurrent.TrieMap;

public class QuadParser extends SPParser{
	
	final static Logger logger = Logger.getLogger(QuadParser.class);

	protected Map<String,String> parserTrie = new TrieMap<String,String>();

	public QuadParser() {
		super();
	}


	@Override
	public void parseFile(String in, String extension, String rep, String dirOut) {
		
		
		PipedRDFIterator<Quad> iter = new PipedRDFIterator<Quad>(Constants.BUFFER_SIZE, true);
		final PipedRDFStream<Quad> inputStream = new PipedQuadsStream(iter);
        // PipedRDFStream and PipedRDFIterator need to be on different threads
        ExecutorService executor1 = Executors.newSingleThreadExecutor();

        // Create a runnable for our parser thread
        Runnable parser = new Runnable() {

            @Override
            public void run() {
                // Call the parsing process.
                RDFDataMgr.parse(inputStream, in, Lang.NQUADS);
            }
        };

        // Start the parser on another thread
        executor1.submit(parser);
        AtomicInteger atomicInt = new AtomicInteger(0);

        final boolean isZip = this.isZip();
        final boolean isInfer = this.isInfer();
        final String conRep = rep;
        final String filein = in;
        final String dirout = dirOut;
        final String ext = extension;
        final String ds = this.getDsName();
        
        ExecutorService executor2 = Executors.newWorkStealingPool();
        Runnable transformer = new Runnable(){
        	@Override
        	public void run(){
    		
        		SPProcessor processor = new SPProcessor();
        		processor.setDirout(dirout);
        		processor.setExt(ext);
        		processor.setFilein(filein);
        		processor.setIsinfer(isInfer);
        		processor.setIszip(isZip);
        		processor.setRep(conRep);
        		processor.setThreadnum(atomicInt.updateAndGet(n -> n + 1));
        		processor.setDsName(ds);
        		
        		processor.start();
        		
        		while (iter.hasNext()){
        			Quad quad = iter.next();
        			processor.process(quad);
        		}
        		processor.finish();
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
