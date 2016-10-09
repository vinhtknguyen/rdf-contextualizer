package org.knoesis.rdf.sp.concurrent;

import org.apache.jena.riot.lang.PipedRDFIterator;
import org.knoesis.rdf.sp.model.SPTriple;

public class PipedSPTripleIterator extends PipedRDFIterator<SPTriple> {

	public PipedSPTripleIterator(int BUFFER_SIZE, boolean b) {
		super(BUFFER_SIZE, b);
	}

	@Override 
	public void finish(){
		super.finish();
	}
	
	@Override 
	public void start(){
		super.start();
	}
	
}
