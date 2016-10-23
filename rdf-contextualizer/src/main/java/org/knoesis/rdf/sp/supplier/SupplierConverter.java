package org.knoesis.rdf.sp.supplier;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.sparql.core.Quad;
import org.knoesis.rdf.sp.exception.SPException;
import org.knoesis.rdf.sp.model.SPModel;
import org.knoesis.rdf.sp.model.SPNode;
import org.knoesis.rdf.sp.model.SPTriple;
import org.knoesis.rdf.sp.parser.ParserElement;
import org.knoesis.rdf.sp.parser.Reporter;
import org.knoesis.rdf.sp.pipeline.PipedQuadTripleIterator;
import org.knoesis.rdf.sp.pipeline.PipedSPTripleStream;
import org.knoesis.rdf.sp.utils.Constants;

public class SupplierConverter implements Supplier<ParserElement>{

	SPProcessor processor;
    PipedQuadTripleIterator processorIter;
	PipedSPTripleStream converterInputStream;
	Reporter reporter;
	ParserElement element;
	
    public SupplierConverter(PipedQuadTripleIterator processorIter,
			PipedSPTripleStream converterInputStream, ParserElement element, Reporter reporter) {
		super();
		this.processorIter = processorIter;
		this.converterInputStream = converterInputStream;
		this.reporter = reporter;
		this.element = element;
		processor = new SPProcessor(reporter.getRep(), reporter.getUuidInitNum(), reporter.getUuidInitStr());
		processor.setExt(reporter.getExt());
		processor.setIsinfer(reporter.isInfer());
		processor.setOntoDir(reporter.getOntoDir());
		processor.setDsName(reporter.getDsName());
		processor.setShortenURI(reporter.isShortenURI());
	}

	@Override
    public ParserElement get() {
    	long start = System.currentTimeMillis();
    	reporter.reportStartStatus(element, Constants.PROCESSING_STEP_CONVERT);
		SPTriple sptriple = null;
		processor.start();

		converterInputStream.start();
		
		try {
			
			while (processorIter.hasNext()){
				// Put the output to the writerInputStream
				Object obj = processorIter.next();
				if (obj instanceof Quad){
	    			sptriple = processor.process((Quad)obj);
				} else if (obj instanceof Triple){
	    			sptriple = processor.process((Triple)obj);
				}
				if (sptriple != null) ((PipedSPTripleStream)converterInputStream).sptriple(sptriple);
			}
			Iterator<Entry<String, Integer>> it = processor.getReasoner().getGenericPropertyMapPerFile().entrySet().iterator();
			
			while (it.hasNext()) {
				
			    Map.Entry<String,Integer> pair = (Map.Entry<String,Integer>)it.next();
			    ((PipedSPTripleStream)converterInputStream).sptriple(new SPTriple(new SPNode(pair.getKey()), SPModel.rdfType, SPModel.genericPropertyClass));
			}
			
		} catch (RiotException e){
			
			throw new SPException("File " + element.getFilein() + ": encounter streaming exception in converter", e);
			
		} finally {
			converterInputStream.finish();
			processorIter.close();
			reporter.reportSystem(start, element, Constants.PROCESSING_STEP_CONVERT);
		}
		return element;
    }

	public SPProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(SPProcessor processor) {
		this.processor = processor;
	}

	public PipedRDFStream<SPTriple> getConverterInputStream() {
		return converterInputStream;
	}

	public void setConverterInputStream(
			PipedSPTripleStream converterInputStream) {
		this.converterInputStream = converterInputStream;
	}

	public PipedQuadTripleIterator getProcessorIter() {
		return processorIter;
	}

	public void setProcessorIter(PipedQuadTripleIterator processorIter) {
		this.processorIter = processorIter;
	}

	public Reporter getReporter() {
		return reporter;
	}

	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}

}
