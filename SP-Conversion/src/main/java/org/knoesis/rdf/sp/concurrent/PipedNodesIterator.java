package org.knoesis.rdf.sp.concurrent;

import org.apache.jena.riot.lang.PipedRDFIterator;

public class PipedNodesIterator extends PipedRDFIterator<String> {

	public PipedNodesIterator(int BUFFER_SIZE, boolean b) {
		super(BUFFER_SIZE, b);
	}

	public PipedNodesIterator(int BUFFER_SIZE, boolean b, int i, int j) {
		super(BUFFER_SIZE, b, i, j);
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
		registered--;
		if (registered == 0){
			super.finish();
			isClosed = true;
			close();
		}
	}
	
	@Override 
	public void start(){
		registered++;
		if (registered == 1){
			super.start();
			isClosed = false;
		}
	}
	
	public boolean isClose(){
		return isClosed;
	}
}
