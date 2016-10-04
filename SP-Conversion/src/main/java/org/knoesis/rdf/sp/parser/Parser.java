package org.knoesis.rdf.sp.parser;

import org.knoesis.rdf.sp.converter.ContextualRepresentationConverter;

public interface Parser {
	
	
	public boolean hasNext();
	
	public void parse(ContextualRepresentationConverter con, String file, String out, String ext);

}
