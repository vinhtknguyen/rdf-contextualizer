package org.knoesis.rdf.sp.runnable;

import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.knoesis.rdf.sp.concurrent.PipedNodesStream;
import org.knoesis.rdf.sp.model.SPTriple;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;
import org.knoesis.rdf.sp.utils.Reporter;

import com.romix.scala.collection.concurrent.TrieMap;

public class CallableTransformer implements Callable<String>{
	PipedRDFStream<String> transformerInputStream;
    PipedRDFIterator<SPTriple> converterIter;
	Reporter reporter;

    public CallableTransformer(PipedRDFStream<String> transformerInputStream,
			PipedRDFIterator<SPTriple> converterIter, Reporter reporter) {
		super();
		this.transformerInputStream = transformerInputStream;
		this.converterIter = converterIter;
		this.reporter = reporter;
	}

	@Override
    public String call() {
		long start = System.currentTimeMillis();
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
	        transformerInputStream.finish();
			reporter.reportSystem(start, Constants.PROCESSING_STEP_TRANFORM);
        }
		return reporter.getFilename();
    }

	public PipedRDFStream<String> getTransformerInputStream() {
		return transformerInputStream;
	}

	public void setTransformerInputStream(
			PipedRDFStream<String> transformerInputStream) {
		this.transformerInputStream = transformerInputStream;
	}

	public PipedRDFIterator<SPTriple> getConverterIter() {
		return converterIter;
	}

	public void setConverterIter(PipedRDFIterator<SPTriple> converterIter) {
		this.converterIter = converterIter;
	}

}
