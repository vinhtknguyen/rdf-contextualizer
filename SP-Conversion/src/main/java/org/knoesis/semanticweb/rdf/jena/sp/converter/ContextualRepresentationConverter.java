package org.knoesis.semanticweb.rdf.jena.sp.converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.Quad;
import org.apache.log4j.Logger;
import org.knoesis.semanticweb.rdf.jena.sp.parser.Parser;
import org.knoesis.semanticweb.rdf.jena.sp.parser.QuadParser;
import org.knoesis.semanticweb.rdf.jena.sp.parser.TripleParser;
import org.knoesis.semanticweb.rdf.utils.*;
import org.knoesis.semanticweb.rdf.sp.model.*;
/*	Subclasses: 
 * 		NamedGraph2SP
 * 			Input: a property specifying the relationship between the triple and the graph
 * 		NanoPub2SP
 * 		Rei2SP
 * 		Triple2SP
 * 			Input: a metadata resource and a property specifying the relationship between the triple and the metadata
 * 	Default rep conversion is triple cleaning and return it as it is 
 * */

public class ContextualRepresentationConverter {
	
	final static Logger logger = Logger.getLogger(ContextualRepresentationConverter.class);

	private String prefixesFile = "prefixes.ttl";

	protected long initUUIDNumber = -1;
	protected String initUUIDPrefix = null;

	protected String spDelimiter;
	protected SPNode singletonPropertyOf = new SPNode(NodeFactory.createURI(Constants.SINGLETON_PROPERTY_OF));

	protected boolean infer = false;
	protected boolean zip = false;

	public boolean isZip() {
		return zip;
	}

	public void setZip(boolean zip) {
		this.zip = zip;
	}

	public boolean isInfer() {
		return infer;
	}

	public void setInfer(boolean infer) {
		this.infer = infer;
	}

	public ContextualRepresentationConverter(){
		initUUIDNumber = System.currentTimeMillis();
		spDelimiter = Constants.SP_START_DELIMITER;
		initUUIDPrefix = Constants.SP_UUID_PREFIX;
		infer = false;
		zip = false;
		
		RDFWriteUtils.loadPrefixes(this.prefixesFile);
	}
	
	public ContextualRepresentationConverter(long spPrefixNum, String spPrefixStr, String spDelimiter, String singletonPropertyOfURI){
		this.setInitUUIDNumber(spPrefixNum);
		this.setInitUUIDPrefix(spPrefixStr);
		this.setSPDelimiter(spDelimiter);
		this.singletonPropertyOf = new SPNode(NodeFactory.createURI(singletonPropertyOfURI));
		
		RDFWriteUtils.loadPrefixes(this.prefixesFile);
	}
	
	public BufferedWriter getBufferedWriter(String file){
	    BufferedWriter writer = null;
	    OutputStream outStream = null;
	    try {
		    if (this.isZip()){
			outStream = new GZIPOutputStream(
			        new FileOutputStream(new File(file)));
		    } else {
		    	outStream = new FileOutputStream(new File(file));
		    }
		    writer = new BufferedWriter(
		            new OutputStreamWriter(outStream, "UTF-8"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return writer;
		  
	}
	public void convert(String file, String ext, String rep) {
		
		// If the input is a file
		if (!Files.isDirectory(Paths.get(file))){
			try {
				String fileOut = genFileOut(file, ext);

				long start = System.currentTimeMillis();
				
				BufferedWriter writer = getBufferedWriter(fileOut);
				convertFile(file, writer, ext, rep);
				writer.close();
				logger.debug("Finished generating file " + fileOut);

				long end = System.currentTimeMillis() - start;
				
				System.out.println("Time\t" + file + "\t" + end);
				
				System.out.println("Size\t" + file + "\t" + Paths.get(file).toFile().length() + "\t" + fileOut + "\t" + Paths.get(fileOut).toFile().length());

			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else {
			// If the input is a directory

			// Create a new directory for output files
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(file))) {
				String dirOut = file + (ext.equals(Constants.NTRIPLE_EXT)?Constants.CONVERTED_TO_SP_NT:Constants.CONVERTED_TO_SP_TTL);
				Files.createDirectories(Paths.get(dirOut));
		        
				String fileOut = null;
				
				/* PROCESS EACH INPUT FILE & GENERATE OUTPUT FILE */
				for (Path entry : stream) {
					fileOut = dirOut + "/" + genFileOut(entry.getFileName().toString(), ext);

					long start = System.currentTimeMillis();
		        	
					BufferedWriter writer = getBufferedWriter(fileOut);
					
					convertFile(entry.toString(), writer, ext, rep);
					
			        writer.close();
					logger.debug("Finished generating file " + fileOut);
					
		        	long end = System.currentTimeMillis() - start;

					System.out.println("Time(s)\t" + entry.toString() + "\t" + end);
					
					System.out.println("Size(mb)\t" + entry.toString() + "\t" + Paths.get(entry.toString()).toFile().length() + "\t" + fileOut + "\t" + Paths.get(fileOut).toFile().length());
		        }
		    } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				
		}
		System.out.println("Namespaces:\t" + RDFWriteUtils.getCurrentAutoPrefixNsNum());

	}
	
	
	public void convertFile(String file, BufferedWriter writer, String ext, String rep) throws FileNotFoundException {

		System.out.println("Processing " + file + " to generate file " + writer );

		// Parse the file to read and process every line
		if (file != null) {
			InputStream stream = new FileInputStream(file);

			try {
				
				// Write the credentials
				String cred = "# This file is generated by SP-Conversion.\n";
				writer.write(cred);
				
				// Write the prefixes if ttl
				if (ext.equalsIgnoreCase(Constants.TURTLE_EXT)) {
					RDFWriteUtils.resetPrefixMapping();
				}
				Parser nxp = null;
				switch (rep.toUpperCase()) {
				case Constants.NG_REP:
					nxp = new QuadParser();
					break;
				case Constants.NANO_REP:
					nxp = new QuadParser();
					break;
				case Constants.REI_REP:
					nxp = new TripleParser();
					break;
				case Constants.TRIPLE_REP:
					nxp = new TripleParser();
					break;
				default:
					nxp = new TripleParser();
					break;
				}
				
				nxp.parse(this, file, writer, ext);
				stream.close();
	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
		

	public List<SPTriple> transformTriple(org.apache.jena.graph.Triple triple) {
		List<SPTriple> triples = new LinkedList<SPTriple>();
		triples.add(new SPTriple(triple.getSubject(), triple.getPredicate(), triple.getPredicate()));
		return triples;
	}

	public List<SPTriple> transformQuad(Quad triple) {
		
		List<SPTriple> triples = new LinkedList<SPTriple>();
		
		return triples;
	}


	protected String genFileOut(String in, String ext){
		if (in != null && !this.isZip()) {
			
			return in.split("\\.")[0] + Constants.SP_SUFFIX + "." + ext.toLowerCase();
		}
		return in.split("\\.")[0] + Constants.SP_SUFFIX + "." + ext.toLowerCase() + ".gz";
		
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
