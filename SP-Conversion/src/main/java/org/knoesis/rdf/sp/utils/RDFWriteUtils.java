package org.knoesis.rdf.sp.utils;

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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.model.SPNode;
import org.knoesis.rdf.sp.model.SPTriple;

import com.romix.scala.collection.concurrent.TrieMap;

public class RDFWriteUtils {
    
	final static Logger logger = Logger.getLogger(RDFWriteUtils.class);

	public static Map<String,String> prefixMapping = new TrieMap<String,String>();
//	public static PrefixTrie trie = new PrefixTrie();
	public static Map<String,String> trie = new TrieMap<String,String>();
	private static int currentAutoPrefixNsNum = 0;
 
	public static int getCurrentAutoPrefixNsNum() {
		return currentAutoPrefixNsNum;
	}

	public static String getNextPrefixNs(){
		currentAutoPrefixNsNum++;
		return Constants.NS_STR + currentAutoPrefixNsNum;
	}
    /** 
     * Reset the prefixMapping 
     */
    
    public static void resetPrefixMapping(){
    	prefixMapping = new HashMap<String,String>();
    }

	
	public static SPNode shortenURIWithPrefixMapping(SPNode in){
		// shorten the whole URI with prefix 
		int len = in.toString().length();
		String ns = null, prefix = null;
		StringBuilder shorten = new StringBuilder();
		
		if (trie.get(in.toString()) != null){
			prefix = trie.get(in.toString());
			ns = in.toString();
			shorten.append(prefix + ":");
			in.setPrefix(prefix);
			in.setShorten(shorten.toString());
			in.setNamespace(ns);
			return in;
		} 
		
		int lastNsInd = getLastIndexOfDelimiterWithSecondPeriod(in.toString());
		if (lastNsInd > 2 && in.toString().charAt(lastNsInd-1) != '/' && in.toString().charAt(lastNsInd-2) != ':' ) {
			ns = in.toString().substring(0, lastNsInd + 1);
			prefix = trie.get(ns);
			if (prefix == null) prefix = RDFWriteUtils.getNextPrefixNs();
			trie.put(ns, prefix);

			shorten.append(prefix + ":");
			if (!in.toString().substring(lastNsInd+1, len).isEmpty()){
				shorten.append(RDFWriteUtils.normalizeN3(in.toString().substring(lastNsInd+1, len)));
			}
			in.setNamespace(ns);
			in.setPrefix(prefix);
			in.setShorten(shorten.toString());
		} else {
			ns = in.toString();
			shorten.append(prefix + ":");
			prefix = RDFWriteUtils.getNextPrefixNs();
			trie.put(ns, prefix);
		}
	    return in;
	}

	/**
	 * Print a list of triples
	 * 
	 * */
	public static String printTriples(List<SPTriple> triples, String ext){
		if (ext.toLowerCase().equals(Constants.TURTLE_EXT)){
			return printTriples2N3(triples);
		} else if (ext.toLowerCase().equals(Constants.NTRIPLE_EXT)){
			return printTriples2NT(triples);
		}
		return printTriples2N3(triples);
	}
	
	public static String printTriples2NT(List<SPTriple> triples){
		StringBuilder out = new StringBuilder();
		
		for (SPTriple t:triples){
			out.append(t.printTriple2NT());
		}
		return out.toString();
	}

	/**
	 * Shorten the two triples with common subject
	 * By shorting them into common subject, common predicate, and object
	 * */
	
	public static String printTriples2N3(List<SPTriple> triples){
		if (triples == null){
			return "";
		}
		

//		System.out.println("Input triples");
//		for (SPTriple triple : triples){
//			System.out.println(triple.toString());
//		}
		
		/* No sorting as it takes too long to finish
		// Sort the input triples
		Collections.sort(triples, new TripleComparator());
	
		logger.debug("Sorted in triples");
		for (SPTriple triple : triples){
			logger.debug(triple.toShortenString());
		}
		
		*/
		
		// Print the triple in shorten turtle form 
		SPTriple cur = null; 
		SPNode commonSubject = null, commonPredicate = null, commonObject = null;
		StringBuilder curStr = new StringBuilder();
		StringBuilder prefixes = new StringBuilder();
	
		if (triples.size() == 0) return "";
		if (triples.size() == 1) return triples.get(0).printTriple2N3();
		
		for (int i = 0; i < triples.size(); i++){
			
			cur = triples.get(i);
			
			if (cur != null){

				// Generate the prefix string
				prefixes.append(cur.printTriplePrefix());

				// Print the subject for the current triple 
				if (commonSubject == null && commonPredicate == null) {
					
					curStr.append(cur.getSubject().getShorten());
					curStr.append("\t" + cur.getPredicate().getShorten());
					curStr.append("\t" + cur.getObject().getShorten());
					
				} else if (commonSubject != null && commonPredicate != null ){
					
					if (cur.getSubject().getShorten().equals(commonSubject.getShorten())){
						
						if (cur.getPredicate().getShorten().equals(commonPredicate.getShorten())){
							
							if (!cur.getObject().getShorten().equals(commonObject.getShorten())){
								curStr.append(", ");
								curStr.append(cur.getObject().getShorten());
							}
						
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
				commonObject = cur.getObject();
			}
		}
		
		curStr.append("\t . \n");
		// Merge prefix and triples together
		prefixes.append(curStr);
		logger.debug("Output " + prefixes.toString());
		return prefixes.toString();
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
		    trie.put(pair.getValue(), pair.getKey());
		}
		
	}
	public static int getLastIndexOfDelimiter(String uri){
		int ind = uri.length()-1;
				
		boolean pastProtocol = false;
		while (ind >=0){
			if (uri.charAt(ind) == '/' || uri.charAt(ind) == '#' || uri.charAt(ind) == ':' || uri.charAt(ind) == '.'){
				if (!pastProtocol) return ind;
			}
			if (uri.charAt(ind) == '/' && uri.charAt(ind-1) == '/' && uri.charAt(ind-2) == ':') pastProtocol = true;
			ind--;
		}
		return ind;
	}
	
	public static int getLastIndexOfDelimiterWithSecondPeriod(String uri){
		int ind = uri.length()-1;
		
		// Find the 3rd slash symbol
		int lastSlash = 0, slashCount = 0;
		while (lastSlash < ind & slashCount < 3){
			if (uri.charAt(lastSlash) == '/') slashCount++;
			lastSlash++;
		}
//		System.out.println("last slash of " + uri + " is at: " + lastSlash);
		
		boolean pastProtocol = false;
		boolean foundPeriod = false;
		while (ind >=0){
			if (uri.charAt(ind) == '/' || uri.charAt(ind) == '#' || uri.charAt(ind) == ':'){
				if (!pastProtocol) return ind;
			}
			if (uri.charAt(ind) == '.' && ind >= lastSlash){
				if (foundPeriod) return ind;
				foundPeriod = true;
			}
			if (uri.charAt(ind) == '/' && uri.charAt(ind-1) == '/' && uri.charAt(ind-2) == ':') pastProtocol = true;
			ind--;
		}
		return ind;
	}
	

	public static boolean isSPDelimiter(char c){
		return (c == '/' || c == '#' || c == ':' || c == '.');
	}
	
	public static String normalizeN3(String in){
		try {
			return URLEncoder.encode(in, "UTF-8").replaceAll("\\.", "%2E");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return in;
	}

}
