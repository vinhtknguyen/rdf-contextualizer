package org.knoesis.rdf.sp.converter;

import org.knoesis.rdf.sp.utils.Constants;

public class ContextConverterFactory {

	public static ContextualRepresentationConverter createConverter(String rep){
		
		switch (rep.toUpperCase()){
		
		case Constants.NANO_REP:
			return new NanoPub2SP();
		
		case Constants.NG_REP:
			return new NamedGraph2SP();
		
		case Constants.REI_REP:
			return new Reification2SP();
			
		case Constants.TRIPLE_REP:
			return new Triple2SP();

		case Constants.NONE_REP:
			return new ContextualRepresentationConverter();
			
		default:
			return new ContextualRepresentationConverter();
		}
	}
}
