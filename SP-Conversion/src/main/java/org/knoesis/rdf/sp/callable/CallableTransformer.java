package org.knoesis.rdf.sp.callable;

import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.knoesis.rdf.sp.concurrent.PipedNodesStream;
import org.knoesis.rdf.sp.concurrent.PipedSPTripleIterator;
import org.knoesis.rdf.sp.model.SPTriple;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;
import org.knoesis.rdf.sp.utils.Reporter;

import com.romix.scala.collection.concurrent.TrieMap;

public class CallableTransformer implements Callable<String>{
	PipedNodesStream transformerInputStream;
    PipedSPTripleIterator converterIter;
	Reporter reporter;

    public CallableTransformer(PipedNodesStream transformerInputStream,
    		PipedSPTripleIterator converterIter, Reporter reporter) {
		super();
		this.transformerInputStream = transformerInputStream;
		this.converterIter = converterIter;
		this.reporter = reporter;
	}

	@Override
    public String call() {
		long start = System.currentTimeMillis();
		reporter.reportStartStatus(Constants.PROCESSING_STEP_TRANFORM);
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
        } finally{
        	converterIter.close();
        	transformerInputStream.finish();
        	
			reporter.reportSystem(start, Constants.PROCESSING_STEP_TRANFORM);
        }
		return reporter.getFilename();
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
