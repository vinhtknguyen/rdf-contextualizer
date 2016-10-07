package org.knoesis.rdf.sp.concurrent;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.sparql.core.Quad;

@SuppressWarnings("hiding")
public class PipedNodesStream<String> extends PipedRDFStream<String> {

	public PipedNodesStream(PipedRDFIterator<String> sink) {
		super(sink);
		// TODO Auto-generated constructor stub
	}

	public void node(String node){
//		System.out.println("received node: " + node);
		receive(node);
	}

	@Override
	public void triple(Triple triple) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void quad(Quad quad) {
		// TODO Auto-generated method stub
		
	}
	
	@Override 
	public void finish(){
		super.finish();
	}

}
