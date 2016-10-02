package org.knoesis.semanticweb.rdf.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.log4j.Logger;
import org.knoesis.semanticweb.rdf.sp.model.SPNode;
import org.knoesis.semanticweb.rdf.sp.model.SPTriple;

public class RDFWriteUtils {
    
	final static Logger logger = Logger.getLogger(RDFWriteUtils.class);

	public static Map<String,String> prefixMapping = new HashMap<String,String>();
	public static PrefixTrie trie = new PrefixTrie();
	private static int currentAutoPrefixNsNum = 0;
 
	public static int getCurrentAutoPrefixNsNum() {
		return currentAutoPrefixNsNum;
	}

	public static String getNextPrefixNs(){
		currentAutoPrefixNsNum++;
		return Constants.NS_STR + currentAutoPrefixNsNum;
	}
    /**
     * Register a new prefix/namespace mapping which will be used to shorten
     * the print strings for resources in known namespaces.
     */
    public static void registerPrefix(String prefix, String namespace) {
        trie.insert(namespace, prefix);
    }
    
    /**
     * Register a set of new prefix/namespace mapping which will be used to shorten
     * the print strings for resources in known namespaces.
     */
    public static void registerPrefixMap(Map<String, String> map) {
        prefixMapping = map;
        loadPrefixesToTrie(map);
    }
    
    /**
     * Remove a registered prefix from the table of known short forms
     */
    public static void removePrefix(String prefix) {
        prefixMapping.remove(prefix);
    }
    
    /**
     * Remove a set of prefix mappings from the table of known short forms
     */
    public static void removePrefixMap(Map<String, String> map) {
        for ( String s : map.keySet() )
        {
            prefixMapping.remove( s );
        }
    }

    /** 
     * Reset the prefixMapping 
     */
    
    public static void resetPrefixMapping(){
    	prefixMapping = new HashMap<String,String>();
    }
    /** This method returns an array of size 2 
     * First element is the shorten URI string
     * Second element is the line of prefix from the shorten one
     * */
	public static URIShorteningTriplet Node2N3(SPNode in){
		URIShorteningTriplet triplet = null;
		if (in == null) return triplet;
		
	    if (in.getJenaNode().isURI()) {
			// shorten the whole URI with prefix 
			return RDFWriteUtils.shortenURI(in);
	    }
	    
	    if (in.getJenaNode().isLiteral()) {
	    	StringBuilder out = new StringBuilder();
	    	out.append("\"");
	    	out.append(in.getJenaNode().getLiteralLexicalForm());
	    	out.append("\"");
	    	
			// shorten the whole URI with prefix for data type
	    	if (!in.getJenaNode().getLiteralDatatypeURI().equals("")){
	    		out.append("^^");
	    		triplet = Node2N3(new SPNode(NodeFactory.createURI(in.getJenaNode().getLiteralDatatypeURI())));
	    		out.append(triplet.getShortenURI());
//	    		System.out.println("output datatype: " + toN3(((Literal) in).getDatatype()));
	    	}
	    	return URIShorteningTriplet.createTriplet(out.toString(), triplet.getPrefix(), triplet.getNamespace());
	    } 
	    
	    return triplet;
	}
	
	
	public static String shortenURIWithMapping(SPNode in){
		// shorten the whole URI with prefix 
		
		Iterator<Entry<String, String>> it = prefixMapping.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String,String> pair = (Map.Entry<String,String>)it.next();									
	        if (in.getJenaNode().getURI().startsWith(pair.getValue().toString())){
	        	try {
					return pair.getKey().toString() + ":" + URLEncoder.encode(in.getJenaNode().getURI().replace(pair.getValue().toString(),""),"UTF-8" ).replaceAll("[{}()\\|\\$\\*\\+\\.\\^:,]","");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    }
	    return "<" + in.toString() + ">";
	}
	public static URIShorteningTriplet shortenURI(SPNode in){
		// shorten the whole URI with prefix 
		return trie.shortenURIWithPrefix(in.getJenaNode());
		 
	}


	public static String Node2NT(SPNode in){
		
	    if (in.getJenaNode() instanceof Node_URI) {
			// shorten the whole URI with prefix 
			return "<" + in.toString() + ">";
	    }
	    
	    if (in.getJenaNode() instanceof Node_Literal) {
	    	StringBuilder out = new StringBuilder();
	    	out.append("\"");
	    	out.append(in.getJenaNode().getLiteralLexicalForm());
	    	out.append("\"");

	    	// shorten the whole URI with prefix for data type
	    	if (!in.getJenaNode().getLiteralDatatypeURI().equals("")){
	    		out.append("^^");
	    		out.append(Node2NT(new SPNode(NodeFactory.createURI(in.getJenaNode().getLiteralDatatypeURI()))));
	    	}
	    	return out.toString();
	    } 
	    
	    return "";
	}

	/**
	 * Print a list of triples
	 * 
	 * */
	public static String printTriples(List<SPTriple> triples, String ext){
		if (ext.toUpperCase().equals(Constants.TURTLE_EXT)){
			return printTriples2N3(triples);
		} else if (ext.toUpperCase().equals(Constants.NTRIPLE_EXT)){
			return printTriples2NT(triples);
		}
		return printTriples2N3(triples);
	}
	
	/**
	 * Shorten the two triples with common subject
	 * By shorting them into common subject, common predicate, and object
	 * */
	
	public static String printTriples2N3(List<SPTriple> triples){
		if (triples == null){
			return "";
		}
		
		StringBuilder prefixes = new StringBuilder(), triplesStr = new StringBuilder();
		
		// Shorten the nodes in the triples.
		for (int i = 0; i < triples.size(); i++){
			URIShorteningTriplet triplet = RDFWriteUtils.Node2N3(triples.get(i).getSubject());
			triples.get(i).getSubject().setShorten(triplet.getShortenURI());
			prefixes.append(printPrefix(triplet.getPrefix(), triplet.getNamespace()));
			
			triplet = RDFWriteUtils.Node2N3(triples.get(i).getPredicate());
			triples.get(i).getPredicate().setShorten(triplet.getShortenURI());
			prefixes.append(printPrefix(triplet.getPrefix(), triplet.getNamespace()));

			triplet = RDFWriteUtils.Node2N3(triples.get(i).getObject());
			triples.get(i).getObject().setShorten(triplet.getShortenURI());
			prefixes.append(printPrefix(triplet.getPrefix(), triplet.getNamespace()));
		}
		logger.debug("Input triples");
		for (SPTriple triple : triples){
			logger.debug(triple.toString());
		}
		
		// Sort the input triples
		Collections.sort(triples, new TripleComparator());
	
		logger.debug("Sorted in triples");
		for (SPTriple triple : triples){
			logger.debug(triple.toShortenString());
		}
		
		// Print the triple in shorten turtle form 
		SPTriple cur = null; 
		SPNode commonSubject = null, commonPredicate = null;
		StringBuilder curStr = new StringBuilder();
		
		if (triples.size() == 0) return "";
		if (triples.size() == 1) return printTriple2N3(triples.get(0));
		
		for (int i = 0; i < triples.size(); i++){
			cur = triples.get(i);
			if (cur != null){

				// Print the subject for the current triple 
				if (commonSubject == null && commonPredicate == null) {
					
					curStr.append(cur.getSubject().getShorten());
					curStr.append("\t" + cur.getPredicate().getShorten());
					curStr.append("\t" + cur.getObject().getShorten());
					
				} else if (commonSubject != null && commonPredicate != null ){
					
					if (cur.getSubject().getShorten().equals(commonSubject.getShorten())){
						
						if (cur.getPredicate().getShorten().equals(commonPredicate.getShorten())){
							
							curStr.append(", ");
							curStr.append(cur.getObject().getShorten());
						
						} else {
							curStr.append("\t ; \n");
							curStr.append("\t" + cur.getPredicate().getShorten());
							curStr.append("\t" + cur.getObject().getShorten());
						}
					} else {
						curStr.append("\t . \n");
						curStr.append(cur.getSubject().getShorten());
						curStr.append("\t" + cur.getPredicate().getShorten());
						curStr.append("\t" + cur.getObject().getShorten());
					}
				}
				commonSubject = cur.getSubject();
				commonPredicate = cur.getPredicate();
			}
		}
		
		curStr.append("\t . \n");
		// Merge prefix and triples together
		prefixes.append(curStr);
		logger.debug("Output " + prefixes.toString());
		return prefixes.toString();
	}
	
	public static String printPrefix(String prefix, String namespace){
		if (namespace != null && prefix != null){
			StringBuilder out = new StringBuilder();
			out.append("@prefix\t");
			out.append(prefix);
			out.append(":\t<");
			out.append(namespace);
			out.append(">\t . \n");
			return out.toString();
		}
		return "";
	}
	
	public static String printTriple2N3(SPTriple triple){
		StringBuilder out = new StringBuilder();
		StringBuilder prefixes = new StringBuilder();
		// The first element of temp is the shorten form, the second element is the prefix, and the third is the namespace
		URIShorteningTriplet triplet = null;

		// Check for new prefixes to add to the prefixMapping
		// In Subject
		triplet = RDFWriteUtils.Node2N3(triple.getSubject());
		
		prefixes.append(printPrefix(triplet.getPrefix(),triplet.getNamespace()));
		
		out.append(triplet.getShortenURI());
		out.append('\t');
		
		// In Predicate
		triplet = RDFWriteUtils.Node2N3(triple.getPredicate());
		
		prefixes.append(printPrefix(triplet.getPrefix(),triplet.getNamespace()));
		
		out.append(triplet.getShortenURI());
		out.append('\t');
		
		// In Object
		triplet = RDFWriteUtils.Node2N3(triple.getObject());
		
		prefixes.append(printPrefix(triplet.getPrefix(),triplet.getNamespace()));
		
		out.append(triplet.getShortenURI());
		out.append('\t');
		out.append("\t . \n");
		
		prefixes.append(out);
		
		return prefixes.toString();
	}
	
	public static String printTriples2NT(List<SPTriple> triples){
		StringBuilder out = new StringBuilder();
		
		for (SPTriple t:triples){
			out.append(printTriple2NT(t));
		}
		return out.toString();
	}

	public static String printTriple2NT(SPTriple triple){
		
		if (triple == null) return "";
		
		StringBuilder  out = new StringBuilder(RDFWriteUtils.Node2NT(triple.getSubject()));
		out.append('\t');
		out.append(RDFWriteUtils.Node2NT(triple.getPredicate()));
		out.append('\t');
		out.append(RDFWriteUtils.Node2NT(triple.getObject()));
		out.append("\t . \n");
		
		return out.toString();
	}

	public static void writePrefixes(String file){
		try {
			if (prefixMapping == null) return;
			Iterator<Entry<String, String>> it = prefixMapping.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry<String,String> pair = (Map.Entry<String,String>)it.next();
				String prefixStr = "@prefix " + pair.getKey() + ":\t <" + pair.getValue() + "> \t .\n";
				Files.write(Paths.get(file), prefixStr.getBytes(), StandardOpenOption.APPEND);
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Map<String,String> parsePrefixes(String file){
		//read file into stream, try-with-resources
		Map<String,String> map = new HashMap<String,String>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {

			String line;
			String[] tmp;
			while ((line = br.readLine()) != null) {
				tmp = line.split("=");
				if (tmp.length == 2){
					map.put(tmp[0], tmp[1]);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static void loadPrefixes(String file){
		//read file into stream, try-with-resources
		Model model = ModelFactory.createDefaultModel();
		model.read(file);
		if (model.getNsPrefixMap() == null) return;
		else loadPrefixesToTrie(model.getNsPrefixMap());
	}
	
	private static void loadPrefixesToTrie(Map<String,String> map){
		if (map == null) return;
		Iterator<Entry<String, String>> it = map.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry<String,String> pair = (Map.Entry<String,String>)it.next();
		    trie.insert(pair.getValue(), pair.getKey());
		}
	}

}
