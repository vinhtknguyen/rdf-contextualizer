package org.knoesis.rdf.sp.converter;

import org.knoesis.rdf.sp.utils.Constants;

public class ContextConverterFactory {

	public static ContextualRepresentationConverter createConverter(String rep){
		
		ContextualRepresentationConverter con;
		
		switch (rep.toUpperCase()){
		
		case Constants.NANO_REP:
			con = new NanoPub2SP();
			break;
		
		case Constants.NG_REP:
			con = new NamedGraph2SP();
			break;
		
		case Constants.REI_REP:
			con = new Reification2SP();
			break;
			
		case Constants.TRIPLE_REP:
			con = new Triple2SP();
			break;

		case Constants.NONE_REP:
			con = new ContextualRepresentationConverter();
			break;
			
		default:
			con = new ContextualRepresentationConverter();
			break;
		}
		return con;
	}
}
