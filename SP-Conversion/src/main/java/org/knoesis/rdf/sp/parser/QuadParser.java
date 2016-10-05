package org.knoesis.rdf.sp.parser;


import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedQuadsStream;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.sparql.core.Quad;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.concurrent.PipedSPTripleIterator;
import org.knoesis.rdf.sp.concurrent.PipedStringStream;
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
	public void parseFile(String in, String extension, String rep, String dirOut) {
		
        // PipedRDFStream and PipedRDFIterator need to be on different threads
		
		PipedRDFIterator<Quad> processorIter = new PipedRDFIterator<Quad>(Constants.BUFFER_SIZE, true);
		final PipedRDFStream<Quad> processorInputStream = new PipedQuadsStream(processorIter);
        final boolean isZip = this.isZip();
        final boolean isInfer = this.isInfer();
        final String conRep = rep;
        final String filein = in;
        final String dirout = dirOut;
        final String ext = extension;
        final String ds = this.getDsName();
        final String uuidInitStr = this.getUuidInitStr();
        final long uuidInitNum = this.getUuidInitNum();
    	PipedSPTripleIterator<String> writerIter = new PipedSPTripleIterator<String>(Constants.BUFFER_SIZE, true);
    	final PipedStringStream<String> writerInputStream = new PipedStringStream<String>(writerIter);
		
        Runnable parser = new Runnable() {

            @Override
            public void run() {
                // Call the parsing process.
                RDFDataMgr.parse(processorInputStream, in, Lang.NQUADS);
            }
        };

        // Start the parser on another thread
        producerExecutor.submit(parser);
        
        Runnable transformer = new Runnable(){
        	@Override
        	public void run(){
        		
          		SPProcessor processor = new SPProcessor(conRep, uuidInitNum, uuidInitStr);
        		processor.setDirout(dirout);
        		processor.setExt(ext);
        		processor.setFilein(filein);
        		processor.setIsinfer(isInfer);
        		processor.setIszip(isZip);
        		processor.setDsName(ds);
        		processor.start();
        		
        		writerInputStream.start();
        	
        		while (processorIter.hasNext()){
        			Quad quad = processorIter.next();
        			writerInputStream.string(processor.process(quad));
        			// Put the output to the writerInputStream
        		}
        		
        		processor.finish();
        		processor.close();
        		
        		writerInputStream.finish();
        		
        		processorIter.close();
        		processorInputStream.finish();
		
        	}
        };
		consumerExecutor.submit(transformer);

        Runnable writer = new Runnable(){
        	@Override
        	public void run(){
        		// Read the data from stream to file
        		BufferedWriter bufWriter = RDFWriteUtils.getBufferedWriter(filein, isZip);
        		while (writerIter.hasNext()){
        			try {
						bufWriter.write(writerIter.next());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        	}
        };
        
		ExecutorService writerExecutor = Executors.newSingleThreadExecutor();
		writerExecutor.submit(writer);
		writerExecutor.shutdown();

	}
}
