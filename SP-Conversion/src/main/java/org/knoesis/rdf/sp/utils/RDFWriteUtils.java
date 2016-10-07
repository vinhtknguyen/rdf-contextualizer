package org.knoesis.rdf.sp.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.model.PrefixTrie;
import org.knoesis.rdf.sp.model.SPNode;
import org.knoesis.rdf.sp.model.SPTriple;

import com.romix.scala.collection.concurrent.TrieMap;

public class RDFWriteUtils {
    
	final static Logger logger = Logger.getLogger(RDFWriteUtils.class);
	public final static String prefixesFile = "prefixes.ttl";

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

	
	public static List<SPTriple> expandSingletonTriples(List<SPTriple> in){
		List<SPTriple> expanded = new ArrayList<SPTriple>();
		
		for (SPTriple triple : in){
			// Singleton instance
			expanded.addAll(triple.getSingletonInstanceTriples());
			expanded.addAll(triple.getMetaTriples());
			// Inferred generic property triple
			expanded.add(new SPTriple(triple.getSubject(), triple.getPredicate(), triple.getObject()));
			// Meta property triple
			expanded.addAll(triple.getGenericPropertyTriples());
		}
		return expanded;
	}
	
	/**
	 * Print a list of triples
	 * 
	 * */
	public static String printTriples(List<SPTriple> in, PrefixTrie prefixMapping, PrefixTrie trie, String ext, boolean shortenAllURIs){
		List<SPTriple> triples = new ArrayList<SPTriple>();
		triples.addAll(expandSingletonTriples(in));
		if (ext.toLowerCase().equals(Constants.TURTLE_EXT)){
			return printTriples2N3(triples, prefixMapping, trie, shortenAllURIs);
		} else if (ext.toLowerCase().equals(Constants.NTRIPLE_EXT)){
			return printTriples2NT(triples);
		}
		return printTriples2N3(triples, prefixMapping, trie, shortenAllURIs);
	}
	
	public static String printTriples2NT(List<SPTriple> triples){
		StringBuilder out = new StringBuilder("");
		
		for (SPTriple t:triples){
			out.append(t.printTriple2NT());
		}
		return out.toString();
	}

	/**
	 * Shorten the two triples with common subject
	 * By shorting them into common subject, common predicate, and object
	 * */
	
	public static String printTriples2N3(List<SPTriple> triples, PrefixTrie prefixMapping, PrefixTrie trie, boolean shortenAllURIs){
		if (triples == null){
			return "";
		}
		

//		System.out.println("Input triples");
//		for (SPTriple triple : triples){
//			System.out.println(triple.printAll());
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
		StringBuilder prefixes = new StringBuilder("");
	
		if (triples.size() == 0) return "";
		if (triples.size() == 1) return triples.get(0).printTriple2N3(prefixMapping, trie, shortenAllURIs);
		
		for (int i = 0; i < triples.size(); i++){
			
			cur = triples.get(i);
			
			if (cur != null){

				SPNode curSub = cur.getSubject().toN3(prefixMapping, trie, shortenAllURIs);
				SPNode curPred = cur.getPredicate().toN3(prefixMapping, trie, shortenAllURIs);
				SPNode curObj = cur.getObject().toN3(prefixMapping, trie, shortenAllURIs);
				// Generate the prefix string
				prefixes.append(cur.printTriplePrefix(prefixMapping, trie, shortenAllURIs));
				// Print the subject for the current triple 
				if (commonSubject == null && commonPredicate == null) {
					
					curStr.append(curSub.getShorten());
					curStr.append("\t" + curPred.getShorten());
					curStr.append("\t" + curObj.getShorten());
					
				} else if (commonSubject != null && commonPredicate != null ){
					
					if (curSub.getShorten().equals(commonSubject.getShorten())){
						
						if (curPred.getShorten().equals(commonPredicate.getShorten())){
							
							if (!curObj.getShorten().equals(commonObject.getShorten())){
								curStr.append(", ");
								curStr.append(curObj.getShorten());
							}
						
						} else {
							curStr.append("\t ; \n");
							curStr.append("\t" + curPred.getShorten());
							curStr.append("\t" + curObj.getShorten());
						}
					} else {
						curStr.append("\t . \n");
						curStr.append(curSub.getShorten());
						curStr.append("\t" + curPred.getShorten());
						curStr.append("\t" + curObj.getShorten());
					}
				}
				commonSubject = curSub;
				commonPredicate = curPred;
				commonObject = curObj;
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
	
	public static void loadPrefixesToTrie(PrefixTrie trie){
		loadPrefixesToTrie(trie, prefixesFile);
	}
	
	public static void loadPrefixesToTrie(PrefixTrie trie, String file){
		//read file into stream, try-with-resources
		Model model = ModelFactory.createDefaultModel();
		RDFDataMgr.read(model,file, Lang.N3);
		if (model.getNsPrefixMap() != null){
			Iterator<Entry<String, String>> it = model.getNsPrefixMap().entrySet().iterator();
			while (it.hasNext()) {
			    Map.Entry<String,String> pair = (Map.Entry<String,String>)it.next();
			    trie.insert(pair.getValue(), pair.getKey());
			}
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
	
	public static String genFileOut(String in, String ext, boolean isZip){
		if (in != null && !isZip) {
			
			return in.split("\\.")[0] + Constants.SP_SUFFIX + "." + ext.toLowerCase();
		}
		return in.split("\\.")[0] + Constants.SP_SUFFIX + "." + ext.toLowerCase() + ".gz";
		
	}
	public static String genDirOut(String in){
		return in + Constants.SP_SUFFIX;
	}

	public static String genFileOutForThread(String in, String dirOut, int num, String ext, boolean isZip){
		if (in == null) return null;
		if (dirOut == null) return in;
		String[] tmp = in.split("/");
		String filename = tmp[tmp.length-1].split("\\.")[0];
		if (!isZip) {
			return dirOut + "/" + filename + Constants.SP_SUFFIX + num + "." + ext.toLowerCase();
		} 
		return dirOut + "/" + filename + Constants.SP_SUFFIX + num + "." + ext.toLowerCase() + ".gz";
		
	}

	public static BufferedWriter getBufferedWriter(String file, boolean isZip){
		BufferedWriter writer = null;
	    OutputStream outStream = null;
	    try {
		    if (isZip){
			outStream = new GZIPOutputStream(
			        new FileOutputStream(new File(file)));
		    } else {
		    	outStream = new FileOutputStream(new File(file));
		    }
		    writer = new BufferedWriter(
		            new OutputStreamWriter(outStream, "UTF-8"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return writer;
		  
	}
	public static BufferedWriter getReportWriter(String file){
		BufferedWriter writer = null;
	    OutputStream outStream = null;
	    try {
		    outStream = new FileOutputStream(new File(file), true);
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
	

}
