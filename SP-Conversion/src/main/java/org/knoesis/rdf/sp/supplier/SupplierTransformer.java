package org.knoesis.rdf.sp.supplier;

import java.util.Map;
import java.util.function.Supplier;

import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.knoesis.rdf.sp.exception.SPException;
import org.knoesis.rdf.sp.model.SPTriple;
import org.knoesis.rdf.sp.parser.ParserElement;
import org.knoesis.rdf.sp.parser.Reporter;
import org.knoesis.rdf.sp.pipeline.PipedNodesStream;
import org.knoesis.rdf.sp.pipeline.PipedSPTripleIterator;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;

import com.romix.scala.collection.concurrent.TrieMap;

public class SupplierTransformer implements Supplier<ParserElement>{
	PipedNodesStream transformerInputStream;
    PipedSPTripleIterator converterIter;
	Reporter reporter;
	ParserElement element;

    public SupplierTransformer(PipedNodesStream transformerInputStream,
    		PipedSPTripleIterator converterIter, ParserElement element, Reporter reporter) {
		super();
		this.transformerInputStream = transformerInputStream;
		this.converterIter = converterIter;
		this.reporter = reporter;
		this.element = element;
	}

	@Override
    public ParserElement get() {
		long start = System.currentTimeMillis();
		reporter.reportStartStatus(element, Constants.PROCESSING_STEP_TRANFORM);
		Map<String,String> prefixMapping = new TrieMap<String,String>();
        Map<String,String> trie = new TrieMap<String,String>();
		
        transformerInputStream.start();
		
        try{
	        String node;
			while (converterIter.hasNext()){
			// Transform the SPTriple to string node
				Object obj = converterIter.next();
				if (obj instanceof SPTriple){
					node = RDFWriteUtils.printTriples((SPTriple)obj, prefixMapping, trie, reporter.getExt(), reporter.isShortenURI());
					if (node != null) ((PipedNodesStream) transformerInputStream).node(node);
				}
			}
			
			reporter.reportSystem(start, element, Constants.PROCESSING_STEP_TRANFORM);
        	transformerInputStream.finish();
        	converterIter.close();
       } catch (RiotException e){
			throw new SPException("File " + element.getFilein() + ": encounter streaming exception in transformer", e);
			
		} 
        return element;
    }

	public PipedRDFStream<String> getTransformerInputStream() {
		return transformerInputStream;
	}

	public void setTransformerInputStream(
			PipedNodesStream transformerInputStream) {
		this.transformerInputStream = transformerInputStream;
	}

	public PipedRDFIterator<SPTriple> getConverterIter() {
		return converterIter;
	}

	public void setConverterIter(PipedSPTripleIterator converterIter) {
		this.converterIter = converterIter;
	}

}
