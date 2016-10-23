package org.knoesis.rdf.sp.converter;

import org.knoesis.rdf.sp.utils.Constants;

public class ContextConverterFactory {

	public static ContextualRepresentationConverter createConverter(String rep, String _uuidInitStr, long _uuidInitNum){
		
		ContextualRepresentationConverter con;
		
		switch (rep.toUpperCase()){
		
		case Constants.NANO_REP:
			con = new NanoPub2SP(_uuidInitNum, _uuidInitStr);
			break;
		
		case Constants.NG_REP:
			con = new NamedGraph2SP(_uuidInitNum, _uuidInitStr);
			break;
		
		case Constants.REI_REP:
			con = new Reification2SP(_uuidInitNum, _uuidInitStr);
			break;
			
		case Constants.TRIPLE_REP:
			con = new Triple2SP(_uuidInitNum, _uuidInitStr);
			break;

		case Constants.NONE_REP:
			con = new ContextualRepresentationConverter(_uuidInitNum, _uuidInitStr);
			break;
			
		default:
			con = new ContextualRepresentationConverter(_uuidInitNum, _uuidInitStr);
			break;
		}
		return con;
	}
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
