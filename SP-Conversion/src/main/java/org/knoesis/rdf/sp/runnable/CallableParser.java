package org.knoesis.rdf.sp.runnable;

import java.util.concurrent.Callable;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.Reporter;

public class CallableParser<T> implements Callable<String>{
	PipedRDFStream<T> processorInputStream;
	Reporter reporter;

    public CallableParser(PipedRDFStream<T> processorInputStream, Reporter reporter) {
		super();
		this.processorInputStream = processorInputStream;
		this.reporter = reporter;
	}

	@Override
    public String call() {
		long start = System.currentTimeMillis();
		
		processorInputStream.start();
        
		try{
			RDFDataMgr.parse(processorInputStream, reporter.getFilein(), null);
		} finally{
			processorInputStream.finish();
			reporter.reportSystem(start, Constants.PROCESSING_STEP_PARSE);
		}
        
        
		return reporter.getFilename();
    }

}
