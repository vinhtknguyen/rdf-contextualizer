package org.knoesis.rdf.sp.supplier;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.function.Supplier;

import org.apache.jena.riot.RiotException;
import org.knoesis.rdf.sp.exception.SPException;
import org.knoesis.rdf.sp.parser.ParserElement;
import org.knoesis.rdf.sp.parser.Reporter;
import org.knoesis.rdf.sp.pipeline.PipedNodesIterator;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;

public class SupplierWriter implements Supplier<ParserElement> {

	PipedNodesIterator transformerIter;
	Reporter reporter;
	ParserElement element;
	int ind;
	
    public SupplierWriter(PipedNodesIterator transformerIter, ParserElement element, Reporter reporter, int ind) {
		super();
		this.transformerIter = transformerIter;
		this.reporter = reporter;
		this.element = element;
		this.ind = ind;
	}

	@Override
    public ParserElement get() {
		long start = System.currentTimeMillis();
		reporter.reportStartStatus(element, Constants.PROCESSING_STEP_WRITE);
       // Call the parsing process.
		String filename;
		if (ind >= 0){
			filename = RDFWriteUtils.appendIndexToFileName(element.getFileout(), ind);
		} else {
			filename = element.getFileout();
		}
		BufferedWriter buffWriter = RDFWriteUtils.getBufferedWriter(filename, reporter.isZip(), element.getBufferWriter());
		try {
			while (transformerIter.hasNext()){
// 				System.out.println("printing");
				// Put the output to the writerInputStream
 				Object out = transformerIter.next();
 				if (out instanceof String){
 					if (out != null) buffWriter.write(out.toString());
 				}
			}
 			transformerIter.close();
 			buffWriter.close();
			reporter.reportSystem(start, element, Constants.PROCESSING_STEP_WRITE);
		} catch (RiotException e){
			throw new SPException("File " + element.getFilein() + ": encounter streaming exception in writer", e);
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
		return element;
    }

}
