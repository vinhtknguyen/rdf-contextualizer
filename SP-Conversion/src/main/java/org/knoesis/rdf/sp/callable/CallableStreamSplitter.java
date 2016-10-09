package org.knoesis.rdf.sp.callable;

import java.util.List;
import java.util.concurrent.Callable;

import org.knoesis.rdf.sp.concurrent.PipedQuadTripleIterator;
import org.knoesis.rdf.sp.concurrent.PipedQuadTripleStream;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.Reporter;

public class CallableStreamSplitter implements Callable<String>{

    PipedQuadTripleIterator inputIter;
	List<PipedQuadTripleStream> outputStreams;
	Reporter reporter;
	
    public CallableStreamSplitter(PipedQuadTripleIterator inputIter,
    		List<PipedQuadTripleStream> outputStreams, Reporter reporter) {
		super();
		this.inputIter = inputIter;
		this.outputStreams = outputStreams;
		
		this.reporter = reporter;
	}

	@Override
    public String call() {
    	long start = System.currentTimeMillis();
    	reporter.reportStartStatus(Constants.PROCESSING_STEP_SPLITTING);
		int index = 0, len = outputStreams.size();
		
		// Start the stream
		for (PipedQuadTripleStream stream: outputStreams){
			stream.start();
		}
		
		while (inputIter.hasNext()){
			outputStreams.get(index).object(inputIter.next());
			if (index==len-1) index = 0;
			else index++;
		}
		
		inputIter.close();
		
		reporter.reportSystem(start, Constants.PROCESSING_STEP_SPLITTING);
		return reporter.getFilename();
	}
}
