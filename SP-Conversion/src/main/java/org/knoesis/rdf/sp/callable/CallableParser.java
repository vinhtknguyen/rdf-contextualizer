package org.knoesis.rdf.sp.callable;

import java.util.concurrent.Callable;

import org.apache.jena.riot.RDFDataMgr;
import org.knoesis.rdf.sp.concurrent.PipedQuadTripleStream;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.Reporter;

public class CallableParser<T> implements Callable<String>{
	PipedQuadTripleStream processorInputStream;
	Reporter reporter;

    public CallableParser(PipedQuadTripleStream processorInputStream, Reporter reporter) {
		super();
		this.processorInputStream = processorInputStream;
		this.reporter = reporter;
	}

	@Override
    public String call() {
		long start = System.currentTimeMillis();
		reporter.reportStartStatus(Constants.PROCESSING_STEP_PARSE);		
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
