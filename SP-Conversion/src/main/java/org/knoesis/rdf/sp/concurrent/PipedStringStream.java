package org.knoesis.rdf.sp.concurrent;

import java.io.BufferedWriter;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.sparql.core.Quad;

@SuppressWarnings("hiding")
public class PipedStringStream<String> extends PipedRDFStream<String> {

	protected BufferedWriter writer;
	
	public PipedStringStream(PipedRDFIterator<String> sink) {
		super(sink);
		// TODO Auto-generated constructor stub
	}

	public void string(String string){
		receive(string);
	}

	@Override
	public void triple(Triple triple) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void quad(Quad quad) {
		// TODO Auto-generated method stub
		
	}

}
