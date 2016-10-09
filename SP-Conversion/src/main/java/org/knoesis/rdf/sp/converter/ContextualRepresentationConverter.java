package org.knoesis.rdf.sp.converter;

import java.io.File;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Quad;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.model.*;
import org.knoesis.rdf.sp.utils.*;

public class ContextualRepresentationConverter {
	
	final static Logger logger = Logger.getLogger(ContextualRepresentationConverter.class);

	protected static SPNode singletonPropertyOf = new SPNode(Constants.SINGLETON_PROPERTY_OF, false);

	protected SpUUID uuid = null;

	public ContextualRepresentationConverter(long _initNum, String _initStr){
		uuid = new SpUUID(_initNum, _initStr);
		singletonPropertyOf.setSingletonPropertyOf(true);
	}
	
	public ContextualRepresentationConverter(){
		uuid = new SpUUID();
		singletonPropertyOf.setSingletonPropertyOf(true);
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
	

	public SPTriple transformTriple(Triple triple) {
		return new SPTriple(triple.getSubject(), triple.getPredicate(), triple.getPredicate());
	}

	public SPTriple transformQuad(Quad triple) {
		return null;
	}

	public SPNode getSingletonPropertyOf() {
		return singletonPropertyOf;
	}

}
