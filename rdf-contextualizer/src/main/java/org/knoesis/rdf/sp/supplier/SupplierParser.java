package org.knoesis.rdf.sp.supplier;

import java.util.function.Supplier;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RiotException;
import org.knoesis.rdf.sp.exception.SPException;
import org.knoesis.rdf.sp.parser.ParserElement;
import org.knoesis.rdf.sp.parser.Reporter;
import org.knoesis.rdf.sp.pipeline.PipedQuadTripleStream;
import org.knoesis.rdf.sp.utils.Constants;

public class SupplierParser implements Supplier<ParserElement>{
	PipedQuadTripleStream processorInputStream;
	Reporter reporter;
	ParserElement element;

    public SupplierParser(PipedQuadTripleStream processorInputStream, ParserElement element, Reporter reporter) {
		super();
		this.processorInputStream = processorInputStream;
		this.reporter = reporter;
		this.element = element;
	}

	@Override
    public ParserElement get() {
		long start = System.currentTimeMillis();
		reporter.reportStartStatus(element, Constants.PROCESSING_STEP_PARSE);		
        
		try{
			
			RDFDataMgr.parse(processorInputStream, element.getFilein(), null);
			
			reporter.reportSystem(start, element, Constants.PROCESSING_STEP_PARSE);
		} catch (RiotException e){
			System.out.println("File " + element.getFilein() + ": encounter exception in RIOT parser");
			e.getCause().printStackTrace();
			throw new SPException("File " + element.getFilein() + ": encounter streaming exception in parser", e);
			
		} finally{

		}
        return element;
    }

}
