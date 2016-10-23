package org.knoesis.rdf.sp.supplier;

import java.util.List;
import java.util.function.Supplier;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RiotException;
import org.apache.jena.sparql.core.Quad;
import org.knoesis.rdf.sp.exception.SPException;
import org.knoesis.rdf.sp.model.SPTriple;
import org.knoesis.rdf.sp.parser.ParserElement;
import org.knoesis.rdf.sp.parser.Reporter;
import org.knoesis.rdf.sp.pipeline.PipedQuadTripleIterator;
import org.knoesis.rdf.sp.pipeline.PipedQuadTripleStream;
import org.knoesis.rdf.sp.utils.Constants;

public class SupplierStreamSplitter implements Supplier<ParserElement>{

    PipedQuadTripleIterator inputIter;
	List<PipedQuadTripleStream> outputStreams;
	Reporter reporter;
	ParserElement element;
	
    public SupplierStreamSplitter(PipedQuadTripleIterator inputIter,
    		List<PipedQuadTripleStream> outputStreams, ParserElement element, Reporter reporter) {
		super();
		this.inputIter = inputIter;
		this.outputStreams = outputStreams;
		
		this.reporter = reporter;
		this.element = element;
	}

	@Override
    public ParserElement get() {
    	long start = System.currentTimeMillis();
    	reporter.reportStartStatus(element, Constants.PROCESSING_STEP_SPLITTING);
		int index = 0, len = outputStreams.size();
		
		// Start the stream
		for (PipedQuadTripleStream stream: outputStreams){
			stream.start();
		}
		try{
			Object obj;
			while (inputIter.hasNext()){
				obj = inputIter.next();
				if (obj != null){
					if (obj instanceof SPTriple){
						outputStreams.get(index).sptriple((SPTriple) obj);
					}
					if (obj instanceof Quad){
						outputStreams.get(index).quad((Quad) obj);
					}
					if (obj instanceof Triple){
						outputStreams.get(index).triple((Triple) obj);
					}
					if (obj instanceof String){
						outputStreams.get(index).node((String) obj);
					}
				}
				if (index==len-1) index = 0;
				else index++;
			}
			
			for (PipedQuadTripleStream stream: outputStreams){
				stream.finish();
			}
		}  catch (RiotException e){
			throw new SPException("File " + element.getFilein() + ": encounter streaming exception in splitter", e);
			
		} finally{
			inputIter.close();
			reporter.reportSystem(start, element, Constants.PROCESSING_STEP_SPLITTING);
		}
		
		return element;
	}
}
