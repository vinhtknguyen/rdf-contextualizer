package org.knoesis.rdf.sp.parser;

import java.io.BufferedWriter;

import org.knoesis.rdf.sp.converter.ContextualRepresentationConverter;

public interface Parser {
	
	
	public boolean hasNext();
	
	public void parse(ContextualRepresentationConverter con, String file, BufferedWriter writer, String ext);

}
