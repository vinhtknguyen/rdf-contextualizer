package org.knoesis.rdf.sp.concurrent;

import org.apache.jena.riot.lang.PipedRDFIterator;

public class PipedNodeIterator<T> extends PipedRDFIterator<T> {

	public PipedNodeIterator(int BUFFER_SIZE, boolean b) {
		super(BUFFER_SIZE, b);
	}

	@Override
	public void start(){
		
	}
	
	@Override
	public void finish(){
		
	}
}
