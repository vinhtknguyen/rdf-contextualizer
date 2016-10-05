package org.knoesis.rdf.sp.concurrent;

import org.apache.jena.riot.lang.PipedRDFIterator;

public class PipedSPTripleIterator<T> extends PipedRDFIterator<T> {

	public PipedSPTripleIterator(int BUFFER_SIZE, boolean b) {
		super(BUFFER_SIZE, b);
	}

	@Override
	public void start(){
		
	}
	
	@Override
	public void finish(){
		
	}
}
