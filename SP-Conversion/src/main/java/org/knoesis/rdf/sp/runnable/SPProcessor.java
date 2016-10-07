package org.knoesis.rdf.sp.runnable;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Quad;
import org.knoesis.rdf.sp.converter.ContextConverterFactory;
import org.knoesis.rdf.sp.converter.ContextualRepresentationConverter;
import org.knoesis.rdf.sp.converter.NamedGraph2SP;
import org.knoesis.rdf.sp.converter.NanoPub2SP;
import org.knoesis.rdf.sp.converter.Reification2SP;
import org.knoesis.rdf.sp.converter.Triple2SP;
import org.knoesis.rdf.sp.inference.ContextualInference;
import org.knoesis.rdf.sp.model.PrefixTrie;
import org.knoesis.rdf.sp.model.SPModel;
import org.knoesis.rdf.sp.model.SPTriple;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;

public class SPProcessor{

	protected String ext;
	protected String rep;
	protected boolean isInfer;
	protected String ontoDir;
	protected long start;
	protected String dsName;
	protected boolean shortenURI = true;
	protected String prefix;
	
	// For all already-printed prefixes and namespaces
	PrefixTrie prefixMapping = new PrefixTrie();
	// For all possible prefixes and namespaces
	PrefixTrie trie = new PrefixTrie();
	ContextualInference reasoner = new ContextualInference();
	ContextualRepresentationConverter converter;
	
	public SPProcessor(String _rep){
		rep = _rep;
		converter = ContextConverterFactory.createConverter(_rep);
	}

	public SPProcessor(String _rep, long uuidInitNum, String uuidInitStr) {
		rep = _rep;
		converter = ContextConverterFactory.createConverter(rep, uuidInitStr, uuidInitNum);
	}
	
	public void start(){
		start = System.currentTimeMillis();
//		RDFWriteUtils.loadPrefixesToTrie(trie);
		if (this.prefix != null) {
			System.out.println("Loading prefixes ..." + prefix);
			RDFWriteUtils.loadPrefixesToTrie(trie, this.prefix);
			System.out.println("Done loading prefixes.");
		}

		if (isInfer){
			System.out.println("Loading ontologies from " + this.getOntoDir());
			SPModel.loadModel(this.getOntoDir());
			System.out.println("Done loading ontologies.");
		}
	}
	
	public String process(Quad quad){
		// Write the credentials
//			System.out.println("Processing quad: " + quad.toString());
		List<SPTriple> triples = new ArrayList<SPTriple>();
		if (isInfer){
			// infer new triples and add them to the list
			triples.addAll(reasoner.infer(convert(quad)));
		} else {
			triples.addAll(convert(quad));
		}
		return RDFWriteUtils.printTriples(triples, prefixMapping, trie, ext, this.isShortenURI());
	}
	
	public String process(Triple triple){
//		System.out.println("Processing quad: " + quad.toString());
		List<SPTriple> triples = new ArrayList<SPTriple>();
		if (isInfer){
			// infer new triples and add them to the list
			triples.addAll(reasoner.infer(convert(triple)));
		} else {
			triples.addAll(convert(triple));
		}
		return RDFWriteUtils.printTriples(triples, prefixMapping, trie, ext, this.shortenURI);

	}
	protected List<SPTriple> convert(Quad quad){
		List<SPTriple> triples = new ArrayList<SPTriple>();
		switch (rep.toUpperCase()){
		
		case Constants.NANO_REP:
			return ((NanoPub2SP)converter).transformQuad(quad);
		
		case Constants.NG_REP:
			return ((NamedGraph2SP)converter).transformQuad(quad);
		
		default:
			return triples;
		}

	}
	protected List<SPTriple> convert(Triple triple){
		switch (rep.toUpperCase()){
		
		case Constants.REI_REP:
			return ((Reification2SP)converter).transformTriple(triple);
			
		case Constants.TRIPLE_REP:
			return ((Triple2SP)converter).transformTriple(triple);

		case Constants.NONE_REP:
			return ((ContextualRepresentationConverter)converter).transformTriple(triple);
			
		default:
			return ((Triple2SP)converter).transformTriple(triple);
		}

	}
	
	
	public String finish(){
		List<SPTriple> triples = new ArrayList<SPTriple>();
 		if (isInfer) {
       		// Generate the generic property triples
   			triples.addAll(reasoner.generateGenericPropertyTriplesPerFile());
   			return RDFWriteUtils.printTriples(triples, prefixMapping, trie, ext, this.shortenURI);
   		}
 		return "";
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

	public boolean isIsInfer() {
		return isInfer;
	}

	public void setIsinfer(boolean _isInfer) {
		this.isInfer = _isInfer;
	}

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	public String getOntoDir() {
		return ontoDir;
	}

	public void setOntoDir(String ontoDir) {
		this.ontoDir = ontoDir;
	}

	public boolean isInfer() {
		return isInfer;
	}

	public void setInfer(boolean isInfer) {
		this.isInfer = isInfer;
	}

	public boolean isShortenURI() {
		return shortenURI;
	}

	public void setShortenURI(boolean shortenURI) {
		this.shortenURI = shortenURI;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}


}
