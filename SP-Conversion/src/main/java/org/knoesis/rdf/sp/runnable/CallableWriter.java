package org.knoesis.rdf.sp.runnable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.Callable;

import org.apache.jena.riot.lang.PipedRDFIterator;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;
import org.knoesis.rdf.sp.utils.Reporter;

public class CallableWriter implements Callable<String> {

	PipedRDFIterator<String> transformerIter;
	Reporter reporter;

    public CallableWriter(PipedRDFIterator<String> transformerIter, Reporter reporter) {
		super();
		this.transformerIter = transformerIter;
		this.reporter = reporter;
	}

	@Override
    public String call() {
		long start = System.currentTimeMillis();
        // Call the parsing process.
		
        BufferedWriter buffWriter = RDFWriteUtils.getBufferedWriter(reporter.getFileout(), reporter.isZip(), reporter.getBufferSizeWriter());
		try {
 			while (transformerIter.hasNext()){
				// Put the output to the writerInputStream
 				String out = transformerIter.next();
				if (out != null) buffWriter.write(out.toString());
			}
 			buffWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
 			transformerIter.close();
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
