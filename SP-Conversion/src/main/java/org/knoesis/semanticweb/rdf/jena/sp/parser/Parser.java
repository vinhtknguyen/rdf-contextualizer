package org.knoesis.semanticweb.rdf.jena.sp.parser;

import org.apache.jena.graph.Node;
import org.knoesis.semanticweb.rdf.jena.sp.converter.ContextualRepresentationConverter;

public interface Parser {
	
	
	public boolean hasNext();
	
	public Node[] next();
	
	public void parse(ContextualRepresentationConverter con, String file, String fileOut, String ext);

}
