package org.knoesis.semanticweb.rdf.jena.sp.parser;

import java.io.BufferedWriter;

import org.knoesis.semanticweb.rdf.jena.sp.converter.ContextualRepresentationConverter;

public interface Parser {
	
	
	public boolean hasNext();
	
	public void parse(ContextualRepresentationConverter con, String file, BufferedWriter writer, String ext);

}
