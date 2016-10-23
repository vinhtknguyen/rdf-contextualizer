package org.knoesis.rdf.sp.pipeline;

import org.apache.jena.riot.lang.PipedRDFIterator;

public class PipedQuadTripleIterator extends PipedRDFIterator<Object> {

	public PipedQuadTripleIterator(int BUFFER_SIZE, boolean b) {
		super(BUFFER_SIZE, b);
	}

	public PipedQuadTripleIterator(int bufferStream, boolean b, int i, int j) {
		super(bufferStream, b, i, j);
	}

}
