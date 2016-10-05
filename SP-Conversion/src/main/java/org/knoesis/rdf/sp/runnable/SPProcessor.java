package org.knoesis.rdf.sp.runnable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Quad;
import org.knoesis.rdf.sp.converter.ContextConverterFactory;
import org.knoesis.rdf.sp.converter.ContextualRepresentationConverter;
import org.knoesis.rdf.sp.converter.NamedGraph2SP;
import org.knoesis.rdf.sp.converter.NanoPub2SP;
import org.knoesis.rdf.sp.converter.Reification2SP;
import org.knoesis.rdf.sp.converter.Triple2SP;
import org.knoesis.rdf.sp.inference.ContextualInference;
import org.knoesis.rdf.sp.model.SPTriple;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;
import org.knoesis.rdf.sp.utils.SPStats;

import com.romix.scala.collection.concurrent.TrieMap;

public class SPProcessor{

	protected String filein;
	protected String dirout;
	protected String fileout;
	protected int threadnum;
	protected String ext;
	protected String rep;
	protected boolean iszip;
	protected boolean isinfer;
	protected long start;
	protected String dsName;
	
	static Map<String,String> prefixMapping = new TrieMap<String,String>();
	static Map<String,String> trie = new TrieMap<String,String>();
	BufferedWriter writer;
	ContextualInference reasoner = new ContextualInference();
	ContextualRepresentationConverter converter;
	
	public SPProcessor(){
	}
	
	public void start(){
		prefixMapping = new TrieMap<String,String>();
		start = System.currentTimeMillis();
		RDFWriteUtils.loadPrefixesToTrie(trie);
		converter = ContextConverterFactory.createConverter(rep);
		fileout = RDFWriteUtils.genFileOutForThread(filein, dirout, threadnum, ext, iszip);
		writer = RDFWriteUtils.getBufferedWriter(fileout, iszip);
		try {
			writer.write(Constants.WRITE_FILE_PREFIX);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void process(Quad quad){
		// Write the credentials
		try {
			List<SPTriple> triples = new ArrayList<SPTriple>();
			if (isinfer){
				// infer new triples and add them to the list
				triples.addAll(reasoner.infer(convert(quad)));
			} 
			if (triples.size() > 0) {
				writer.write(RDFWriteUtils.printTriples(triples, prefixMapping, trie, ext));
			}
			triples.clear();
       		
			
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void process(Triple triple){
		// Write the credentials
		try {
			List<SPTriple> triples = new ArrayList<SPTriple>();
			if (isinfer){
				// infer new triples and add them to the list
				triples.addAll(reasoner.infer(convert(triple)));
			} 
			writer.write(RDFWriteUtils.printTriples(triples, prefixMapping, trie, ext));
			triples.clear();
			
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	protected List<SPTriple> convert(Quad quad){
		List<SPTriple> triples = new ArrayList<SPTriple>();
		switch (rep.toUpperCase()){
		
		case Constants.NANO_REP:
			return ((NanoPub2SP)converter).transformQuad(writer, quad, ext);
		
		case Constants.NG_REP:
			return ((NamedGraph2SP)converter).transformQuad(writer, quad, ext);
		
		default:
			return triples;
		}

	}
	protected List<SPTriple> convert(Triple triple){
		switch (rep.toUpperCase()){
		
		case Constants.REI_REP:
			return ((Reification2SP)converter).transformTriple(writer, triple, ext);
			
		case Constants.TRIPLE_REP:
			return ((Triple2SP)converter).transformTriple(writer, triple, ext);

		case Constants.NONE_REP:
			return ((ContextualRepresentationConverter)converter).transformTriple(writer, triple, ext);
			
		default:
			return ((Triple2SP)converter).transformTriple(writer, triple, ext);
		}

	}
	
	
	public void finish(){
		List<SPTriple> triples = new ArrayList<SPTriple>();
 		if (isinfer) {
       		// Generate the generic property triples
   			triples.addAll(reasoner.generateGenericPropertyTriplesPerFile());
   			try {
				writer.write(RDFWriteUtils.printTriples(triples, prefixMapping, trie, ext));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   		}

	}
	
	public void close(){
		try {
			writer.close();
			SPStats.reportSystem(start, rep, (isinfer?"infer":"no-infer"), ext, filein, fileout, dsName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getFilein() {
		return filein;
	}

	public void setFilein(String filein) {
		this.filein = filein;
	}

	public String getDirout() {
		return dirout;
	}

	public void setDirout(String dirout) {
		this.dirout = dirout;
	}

	public String getFileout() {
		return fileout;
	}

	public void setFileout(String fileout) {
		this.fileout = fileout;
	}

	public int getThreadnum() {
		return threadnum;
	}

	public void setThreadnum(int threadnum) {
		this.threadnum = threadnum;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getRep() {
		return rep;
	}

	public void setRep(String rep) {
		this.rep = rep;
	}

	public boolean isIszip() {
		return iszip;
	}

	public void setIszip(boolean iszip) {
		this.iszip = iszip;
	}

	public boolean isIsinfer() {
		return isinfer;
	}

	public void setIsinfer(boolean isinfer) {
		this.isinfer = isinfer;
	}

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

}
