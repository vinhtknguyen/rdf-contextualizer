package org.knoesis.rdf.sp.concurrent;

import org.apache.jena.riot.lang.PipedRDFIterator;
import org.knoesis.rdf.sp.model.SPTriple;

public class PipedSPTripleIterator extends PipedRDFIterator<SPTriple> {

	public PipedSPTripleIterator(int BUFFER_SIZE, boolean b) {
		super(BUFFER_SIZE, b);
	}

	public PipedSPTripleIterator(int BUFFER_SIZE) {
		super(BUFFER_SIZE);
	}
	
}
