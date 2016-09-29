package org.knoesis.semanticweb.rdf.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.PrefixMapping;

public class RDFWriteUtils {
    
	public static Map<String,String> prefixMapping = new HashMap<String,String>();
	public static PrefixTrie trie = new PrefixTrie();
 
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
	public static String[] Node2N3(Node in){
		String[] output = new String[2];
		if (in == null) return output;
		
	    if (in.isURI()) {
			// shorten the whole URI with prefix 
			return RDFWriteUtils.shortenURI(in);
	    }
	    
	    if (in.isLiteral()) {
	    	StringBuilder out = new StringBuilder();
	    	out.append("\"");
	    	out.append(in.getLiteralLexicalForm());
	    	out.append("\"");

	    	// Check the language tag
	    	if (!in.getLiteralLanguage().equals("")){
	    		out.append('@');
	    		out.append(in.getLiteralLanguage());
	    	}
	    	
			// shorten the whole URI with prefix for data type
	    	if (!in.getLiteralDatatypeURI().equals("")){
	    		out.append("^^");
	    		String[] tmp = Node2N3(NodeFactory.createURI(in.getLiteralDatatypeURI()));
	    		out.append(tmp[0]);
	    		output[1] = tmp[1];
//	    		System.out.println("output datatype: " + toN3(((Literal) in).getDatatype()));
	    	}
	    	return output;
	    } 
	    
	    return output;
	}
	
	
	public static String shortenURIWithMapping(Node in){
		// shorten the whole URI with prefix 
		
		Iterator<Entry<String, String>> it = prefixMapping.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String,String> pair = (Map.Entry<String,String>)it.next();									
	        if (in.getURI().startsWith(pair.getValue().toString())){
	        	try {
					return pair.getKey().toString() + ":" + URLEncoder.encode(in.getURI().replace(pair.getValue().toString(),""),"UTF-8" ).replaceAll("[{}()\\|\\$\\*\\+\\.\\^:,]","");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    }
	    return "<" + in.toString() + ">";
	}
	public static String[] shortenURI(Node in){
		// shorten the whole URI with prefix 
		return trie.shortenURIWithPrefix(in);
		 
	}


	public static String Node2NT(Node in){
		
	    if (in instanceof Node_URI) {
			// shorten the whole URI with prefix 
			return "<" + in.toString() + ">";
	    }
	    
	    if (in instanceof Node_Literal) {
	    	StringBuilder out = new StringBuilder();
	    	out.append("\"");
	    	out.append(in.getLiteralLexicalForm());
	    	out.append("\"");

	    	// Check the language tag
	    	if (!in.getLiteralLanguage().equals("")){
	    		out.append('@');
	    		out.append(in.getLiteralLanguage());
	    	}
	    	
			// shorten the whole URI with prefix for data type
	    	if (!in.getLiteralDatatypeURI().equals("")){
	    		out.append("^^");
	    		out.append(Node2NT(NodeFactory.createURI(in.getLiteralDatatypeURI())));
	    	}
	    	return out.toString();
	    } 
	    
	    return "";
	}

	public static String Triple2N3(Node[] nodes){
		if (nodes.length != 3) return "";
		return RDFWriteUtils.Triple2N3(nodes[0], nodes[1], nodes[2]);
	}
	
	public static String Triple2N3(Node subject, Node predicate, Node object){
		StringBuilder out = new StringBuilder();
		StringBuilder prefixes = new StringBuilder();
		String[] tmp = null;

		// Check for new prefixes to add to the prefixMapping
		tmp = RDFWriteUtils.Node2N3(subject);
		// The first element of temp is the shorten form, the second element is the prefix line
		if (tmp[1] != null){
			prefixes.append(tmp[1]);
		}
		out.append(tmp[0]);
		out.append('\t');
		tmp = RDFWriteUtils.Node2N3(predicate);
		if (tmp[1] != null){
			prefixes.append(tmp[1]);
		}
		out.append(tmp[0]);
		out.append('\t');
		tmp = RDFWriteUtils.Node2N3(predicate);
		if (tmp[1] != null){
			prefixes.append(tmp[1]);
		}
		out.append(tmp[0]);
		out.append("\t . \n");
		
		prefixes.append(out);
		
		return prefixes.toString();
	}

	public static String Triple2NT(Node[] nodes){
		if (nodes.length != 3) return "";
		return RDFWriteUtils.Triple2NT(nodes[0], nodes[1], nodes[2]);
	}

	public static String Triple2NT(Node subject, Node predicate, Node object){
		StringBuilder  out = new StringBuilder(RDFWriteUtils.Node2NT(subject));
		out.append('\t');
		out.append(RDFWriteUtils.Node2NT(predicate));
		out.append('\t');
		out.append(RDFWriteUtils.Node2NT(object));
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
	
	public static String printPrefix(String namespace, String prefix){
		StringBuilder out = new StringBuilder();
		out.append("@prefix\t");
		out.append(prefix);
		out.append(":\t<");
		out.append(namespace);
		out.append(">\t . \n");
		return out.toString();
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
