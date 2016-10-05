package org.knoesis.rdf.sp.converter;

import java.io.BufferedWriter;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.sparql.core.Quad;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.model.*;
import org.knoesis.rdf.sp.utils.*;

public class ContextualRepresentationConverter {
	
	final static Logger logger = Logger.getLogger(ContextualRepresentationConverter.class);

	protected static long initUUIDNumber = -1;
	protected static String initUUIDPrefix = null;

	protected static String spDelimiter;
	protected static SPNode singletonPropertyOf = null;


	public ContextualRepresentationConverter(){
		initUUIDNumber = System.currentTimeMillis();
		spDelimiter = Constants.SP_START_DELIMITER;
		initUUIDPrefix = Constants.SP_UUID_PREFIX;
		singletonPropertyOf = new SPNode(Constants.SINGLETON_PROPERTY_OF);
		singletonPropertyOf.setSingletonPropertyOf(true);
		
	}
	
	public ContextualRepresentationConverter(long spPrefixNum, String spPrefixStr, String spDelimiter){
		this.setInitUUIDNumber(spPrefixNum);
		this.setInitUUIDPrefix(spPrefixStr);
		this.setSPDelimiter(spDelimiter);
	}
	
	public static long directorySize(String in) {
	    long length = 0;
	    File directory = new File(in);
	    for (File file : directory.listFiles()) {
	        if (file.isFile())
	            length += file.length();
	        else
	            length += directorySize(file.toString());
	    }
	    return length;
	}
	

	public static List<SPTriple> transformTriple(BufferedWriter writer, org.apache.jena.graph.Triple triple, String ext) {
		List<SPTriple> triples = new LinkedList<SPTriple>();
		triples.add(new SPTriple(triple.getSubject(), triple.getPredicate(), triple.getPredicate(), ext));
		return triples;
	}

	public static List<SPTriple> transformQuad(BufferedWriter writer, Quad triple, String ext) {
		List<SPTriple> triples = new LinkedList<SPTriple>();
		return triples;
	}


	public void setSPDelimiter(String delimiter){
		spDelimiter = delimiter;
	}
	
	public String getSPDelimiter(){
		return spDelimiter;
	}
	
	public void setInitUUIDPrefix(String pre){
		initUUIDPrefix = pre;
	}
	
	public String getInitUUIDPrefix(){
		return initUUIDPrefix;
	}
	
	public void setInitUUIDNumber(long num){
		initUUIDNumber = num;
	}

	public long getInitUUIDNumber(){
		return initUUIDNumber;
	}
	
	public static String getNextUUID(){
		StringBuilder uuid = new StringBuilder(spDelimiter);
		uuid.append(initUUIDPrefix);
		uuid.append(Constants.SP_MID_DELIMITER);
		uuid.append(initUUIDNumber);
		uuid.append(Constants.SP_END_DELIMITER);
		initUUIDNumber++;
		
		return uuid.toString();
	}


	public SPNode getSingletonPropertyOf() {
		return singletonPropertyOf;
	}

}
