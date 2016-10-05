package org.knoesis.rdf.sp.converter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Quad;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.model.*;
import org.knoesis.rdf.sp.utils.*;

public class ContextualRepresentationConverter {
	
	final static Logger logger = Logger.getLogger(ContextualRepresentationConverter.class);

	protected static SPNode singletonPropertyOf = new SPNode(Constants.SINGLETON_PROPERTY_OF, true);

	protected SpUUID uuid = null;

	public ContextualRepresentationConverter(long _initNum, String _initStr){
		uuid = new SpUUID(_initNum, _initStr);
	}
	
	public ContextualRepresentationConverter(){
		uuid = new SpUUID();
	}
	
	public static long directorySize(String in) {
	    long length = 0;
	    File directory = new File(in);
	    for (File file : directory.listFiles()) {
	        if (file.isFile())
	            length += file.length();
	        else
	            length += directorySize(file.toString());
	    }
	    return length;
	}
	

	public List<SPTriple> transformTriple(Triple triple) {
		List<SPTriple> triples = new LinkedList<SPTriple>();
		triples.add(new SPTriple(triple.getSubject(), triple.getPredicate(), triple.getPredicate()));
		return triples;
	}

	public List<SPTriple> transformQuad(Quad triple) {
		List<SPTriple> triples = new LinkedList<SPTriple>();
		return triples;
	}

	public SPNode getSingletonPropertyOf() {
		return singletonPropertyOf;
	}

}
