package org.knoesis.rdf.sp.parser;

import org.knoesis.rdf.sp.utils.Constants;

public class SPParserFactory {

	public static SPParser createParser(String rep){
		switch (rep.toUpperCase()){
		
		case Constants.NANO_REP:
			return new QuadParser();
		
		case Constants.NG_REP:
			return new QuadParser();
		
		case Constants.REI_REP:
			return new TripleParser();
			
		case Constants.TRIPLE_REP:
			return new TripleParser();

		case Constants.NONE_REP:
			return new TripleParser();
			
		default:
			return new TripleParser();
		}
	}
}
