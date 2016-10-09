package org.knoesis.rdf.sp.concurrent;

import org.apache.jena.riot.lang.PipedRDFIterator;

public class PipedQuadTripleIterator extends PipedRDFIterator<Object> {

	public PipedQuadTripleIterator(int BUFFER_SIZE, boolean b) {
		super(BUFFER_SIZE, b);
	}

}
