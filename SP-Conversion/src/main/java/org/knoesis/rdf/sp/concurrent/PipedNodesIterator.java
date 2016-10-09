package org.knoesis.rdf.sp.concurrent;

import org.apache.jena.riot.lang.PipedRDFIterator;

public class PipedNodesIterator extends PipedRDFIterator<String> {

	public PipedNodesIterator(int BUFFER_SIZE, boolean b) {
		super(BUFFER_SIZE, b);
	}

	public PipedNodesIterator(int bufferSizeStream) {
		super(bufferSizeStream);
	}

	/* 
	 * Register before start and stop the stream
	 * Only the first one registered to start
	 * and the last one to finish
	 * */
	boolean isClosed = true;
	int registered = 0;
	
	@Override 
	public void finish(){
		super.finish();
	}
	
	@Override 
	public void start(){
		super.start();
	}
	
	public boolean isClose(){
		return isClosed;
	}
}
