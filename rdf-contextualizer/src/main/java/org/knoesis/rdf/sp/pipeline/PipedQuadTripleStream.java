package org.knoesis.rdf.sp.pipeline;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.core.Quad;
import org.knoesis.rdf.sp.model.SPTriple;

public class PipedQuadTripleStream  extends PipedRDFStream<Object> implements StreamRDF{
	public PipedQuadTripleStream(PipedRDFIterator<Object> sink) {
		super(sink);
		// TODO Auto-generated constructor stub
	}

	public void sptriple(SPTriple node){
//		System.out.println("received node: " + node);
		if (node != null) receive(node);
	}

	public void node(String node){
//		System.out.println("received node: " + node);
		if (node != null) receive(node);
	}

	@Override
	public void triple(Triple triple) {
		// TODO Auto-generated method stub
		if (triple != null) receive(triple);
	}

	@Override
	public void quad(Quad quad) {
		// TODO Auto-generated method stub
		if (quad != null) receive(quad);
	}
	
	public void object(Object obj) {
		if (obj != null) receive(obj);
	}
	
}
