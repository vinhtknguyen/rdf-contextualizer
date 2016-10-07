package org.knoesis.rdf.sp.parser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
import org.knoesis.rdf.sp.utils.SPStats;

public class TripleParser extends SPParser {
	
	final static Logger logger = Logger.getLogger(TripleParser.class);

	public TripleParser() {
	}

	public TripleParser(long uuidInitNum, String _uuidInitStr) {
		super(uuidInitNum, _uuidInitStr);
	}

	@Override
	public void parseFile(String filein, String ext, String rep, String fileout) {
		// PipedRDFStream and PipedRDFIterator need to be on different threads
		PipedRDFIterator<Triple> processorIter = new PipedRDFIterator<Triple>(Constants.BUFFER_SIZE, true);
		final PipedRDFStream<Triple> processorInputStream = new PipedTriplesStream(processorIter);

        // PipedRDFStream and PipedRDFIterator need to be on different threads
		final long start = System.currentTimeMillis();

		Callable<Long> parser = new Callable<Long>() {

            @Override
            public Long call() {
                // Call the parsing process.
                System.out.println("Start parsing the file " + filein);
                RDFDataMgr.parse(processorInputStream, filein, null);
    			long end = System.currentTimeMillis();
    			SPStats.reportSystem(start, end, rep, (infer?Constants.OPTIONS_INFER:Constants.OPTIONS_NO_INFER), ext, filein, fileout, dsName, Constants.PROCESSING_STEP_PARSE);
                System.out.println("Done parsing the file in " + (end-start) + " ms.");
    			return end-start;
            }
        };

        // Start the parser on another thread
        
        futureList.add(parserExecutor.submit(parser));
        
        PipedNodesIterator<String> converterIter = new PipedNodesIterator<String>(Constants.BUFFER_SIZE, true);
		final PipedNodesStream<String> converterInputStream = new PipedNodesStream<String>(converterIter);
      
		Callable<Long> converter = new Callable<Long>() {

            @Override
            public Long call() {
                System.out.println("Start converting the file " + filein);
               SPProcessor processor = new SPProcessor(rep, uuidInitNum, uuidInitStr);
        		processor.setExt(ext);
        		processor.setIsinfer(infer);
        		processor.setOntoDir(ontoDir);
        		processor.setDsName(dsName);
        		processor.setShortenURI(shortenURI);
        		
        		processor.start();
        		
        		converterInputStream.start();
        		
        		while (processorIter.hasNext()){
    				// Put the output to the writerInputStream
    				converterInputStream.node(processor.process(processorIter.next()));
    			}
				converterInputStream.node(processor.finish());
    			converterInputStream.finish();
				
    			processorIter.close();
    			long end = System.currentTimeMillis();
    			SPStats.reportSystem(start, end, rep, (infer?Constants.OPTIONS_INFER:Constants.OPTIONS_NO_INFER), ext, filein, fileout, dsName, Constants.PROCESSING_STEP_CONVERT);
                System.out.println("Done converting the file " + filein + " in " + (end-start) + " ms.");
    			return end-start;
            }
        };
        
        futureList.add(converterExecutor.submit(converter));

        Callable<Long> writer = new Callable<Long>() {

            @Override
            public Long call() {
                // Call the parsing process.
                System.out.println("Start writing output to the file " + fileout);
        		BufferedWriter buffWriter = RDFWriteUtils.getBufferedWriter(fileout, zip);
        		try {
    				System.out.println("Writing to file: "+ fileout);
            		buffWriter.write(Constants.WRITE_FILE_PREFIX);
         			while (converterIter.hasNext()){
        				// Put the output to the writerInputStream
        					buffWriter.write(converterIter.next());
        			}
         			// Pay attention to the order, must finish the stream before closing the iterator
        			converterIter.close();
        			
        			buffWriter.close();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} finally{
        			try {
            			buffWriter.close();
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
    			long end = System.currentTimeMillis();
    			SPStats.reportSystem(start, end, rep, (infer?Constants.OPTIONS_INFER:Constants.OPTIONS_NO_INFER), ext, filein, fileout, dsName, Constants.PROCESSING_STEP_WRITE);
                System.out.println("Done converting the file " + filein + " in " + (end-start) + " ms.");
    			return end-start;
            }
        };

        // Start the parser on another thread
        futureList.add(writerExecutor.submit(writer));
	}

}
