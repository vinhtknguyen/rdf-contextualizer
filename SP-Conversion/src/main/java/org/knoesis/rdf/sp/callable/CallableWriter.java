package org.knoesis.rdf.sp.callable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.Callable;

import org.knoesis.rdf.sp.concurrent.PipedNodesIterator;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;
import org.knoesis.rdf.sp.utils.Reporter;

public class CallableWriter implements Callable<String> {

	PipedNodesIterator transformerIter;
	Reporter reporter;

    public CallableWriter(PipedNodesIterator transformerIter, Reporter reporter) {
		super();
		this.transformerIter = transformerIter;
		this.reporter = reporter;
	}

	@Override
    public String call() {
		long start = System.currentTimeMillis();
		reporter.reportStartStatus(Constants.PROCESSING_STEP_WRITE);
       // Call the parsing process.
		
        BufferedWriter buffWriter = RDFWriteUtils.getBufferedWriter(reporter.getFileout(), reporter.isZip(), reporter.getBufferSizeWriter());
		try {
			transformerIter.start();
 			while (transformerIter.hasNext()){
				// Put the output to the writerInputStream
 				Object out = transformerIter.next();
 				if (out instanceof String){
 					if (out != null) buffWriter.write(out.toString());
 				}
			}
 			transformerIter.close();
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
		
		reporter.reportSystem(start, Constants.PROCESSING_STEP_WRITE);

		return reporter.getFilename();
    }

}
