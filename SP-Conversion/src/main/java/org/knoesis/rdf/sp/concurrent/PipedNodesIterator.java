package org.knoesis.rdf.sp.concurrent;

import org.apache.jena.riot.lang.PipedRDFIterator;

public class PipedNodesIterator<T> extends PipedRDFIterator<T> {

	public PipedNodesIterator(int BUFFER_SIZE, boolean b) {
		super(BUFFER_SIZE, b);
	}

	@Override
	public void start(){
		
	}
	
	@Override
	public void finish(){
		
	}
}
