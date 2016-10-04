package org.knoesis.rdf.sp.converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.Quad;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.model.*;
import org.knoesis.rdf.sp.utils.*;

public class ContextualRepresentationConverter {
	
	final static Logger logger = Logger.getLogger(ContextualRepresentationConverter.class);

	protected long initUUIDNumber = -1;
	protected String initUUIDPrefix = null;

	protected String spDelimiter;
	protected SPNode singletonPropertyOf = null;


	public ContextualRepresentationConverter(){
		initUUIDNumber = System.currentTimeMillis();
		spDelimiter = Constants.SP_START_DELIMITER;
		initUUIDPrefix = Constants.SP_UUID_PREFIX;
		this.singletonPropertyOf = new SPNode(Constants.SINGLETON_PROPERTY_OF);
		this.singletonPropertyOf.setSingletonPropertyOf(true);
		
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
	
	public void convertFile(String file, String writer, String ext, String rep) throws FileNotFoundException {

		System.out.println("Processing " + file + " to generate file " + writer );

		// Parse the file to read and process every line
		if (file != null) {
			InputStream stream = new FileInputStream(file);

			try {
				
				
				// Write the prefixes if ttl
				if (ext.equalsIgnoreCase(Constants.TURTLE_EXT)) {
					RDFWriteUtils.resetPrefixMapping();
				}
				stream.close();
	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
		

	public List<SPTriple> transformTriple(BufferedWriter writer, org.apache.jena.graph.Triple triple, String ext) {
		List<SPTriple> triples = new LinkedList<SPTriple>();
		triples.add(new SPTriple(triple.getSubject(), triple.getPredicate(), triple.getPredicate(), ext));
		return triples;
	}

	public List<SPTriple> transformQuad(BufferedWriter writer, Quad triple, String ext) {
		List<SPTriple> triples = new LinkedList<SPTriple>();
		return triples;
	}


	public void setSPDelimiter(String delimiter){
		this.spDelimiter = delimiter;
	}
	
	public String getSPDelimiter(){
		return this.spDelimiter;
	}
	
	public void setInitUUIDPrefix(String pre){
		this.initUUIDPrefix = pre;
	}
	
	public String getInitUUIDPrefix(){
		return this.initUUIDPrefix;
	}
	
	public void setInitUUIDNumber(long num){
		this.initUUIDNumber = num;
	}

	public long getInitUUIDNumber(){
		return this.initUUIDNumber;
	}
	
	protected String getNextUUID(){
		StringBuilder uuid = new StringBuilder(this.spDelimiter);
		uuid.append(initUUIDPrefix);
		uuid.append(Constants.SP_MID_DELIMITER);
		uuid.append(this.initUUIDNumber);
		uuid.append(Constants.SP_END_DELIMITER);
		this.initUUIDNumber++;
		
		return uuid.toString();
	}


	public SPNode getSingletonPropertyOf() {
		return singletonPropertyOf;
	}


	public void setSingletonPropertyOf(SPNode singletonPropertyOf) {
		this.singletonPropertyOf = singletonPropertyOf;
	}
	
	public void setSingletonPropertyOf(String singletonPropertyOf) {
		this.singletonPropertyOf = new SPNode(NodeFactory.createURI(singletonPropertyOf));
	}
}
