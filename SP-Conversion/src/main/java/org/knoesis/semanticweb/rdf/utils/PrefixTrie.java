package org.knoesis.semanticweb.rdf.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.graph.Node;

public class PrefixTrie {
	private PrefixTrieNode root;

	public PrefixTrie() {
		root = new PrefixTrieNode();
	}

	// Inserts a prefix into the trie.
	
	public void insert(String uri, String prefix) {
		HashMap<Character, PrefixTrieNode> children = root.children;

		PrefixTrieNode node;
		for (int i = 0; i < uri.length(); i++) {
			char c = uri.charAt(i);

			if (children.containsKey(c)) {
				node = children.get(c);
			} else {
				node = new PrefixTrieNode(c);
				children.put(c, node);
			}

			children = node.children;

			// set leaf node
			if (i == uri.length() - 1){
				node.isLeaf = true;
				node.prefix = prefix;
			}
			
		}
	}

	// Returns if the uri is a prefix in the trie.
	public String searchPrefix(String uri) {
		PrefixTrieNode node = searchNode(uri);

		if (node != null && node.isLeaf)
			return node.prefix;
		else
			return null;
	}

	// Returns if there is any word in the trie
	// that starts with the given prefix.
	public boolean matchingPrefix(String uri) {
		if (searchNode(uri) == null)
			return false;
		else
			return true;
	}

	public PrefixTrieNode searchNode(String uri) {
		Map<Character, PrefixTrieNode> children = root.children;
		PrefixTrieNode node = null;
		for (int i = 0; i < uri.length(); i++) {
			char c = uri.charAt(i);
			if (children.containsKey(c)) {
				node = children.get(c);
				children = node.children;
			} else {
				return null;
			}
		}

		return node;
	}
	
	/**
	 * Return a pair of <shortenURI, <prefix, namespace>>
	 * */
	public URIShorteningTriplet shortenURIWithPrefix(Node uri) {

		Map<Character, PrefixTrieNode> children = root.children;
		String ns = null, prefix;
		
		PrefixTrieNode curnode = null, latestLeaf = null;
		int i = 0, lastNsInd = 0;
		final String uriStr = uri.toString();
		StringBuilder shorten = new StringBuilder();
		
		for (i = 0; i < uriStr.length(); i++) {
			
			char c = uriStr.charAt(i);
			
			if (latestLeaf != null && isSPDelimiter(c)){
				latestLeaf = null;
			}
			if (children.containsKey(c)) {
				
				curnode = children.get(c);
				children = curnode.children;
				if (curnode.isLeaf) {
					latestLeaf = curnode;
				}
				
			} else {
				lastNsInd = i;
				break;
			}
		}
		
		// Special treatment for URI with SP prefix to shorten the URI even more
		if (latestLeaf != null){
			// Construct the shorted uri
			lastNsInd = i;
			prefix = latestLeaf.prefix;
			ns = uriStr.substring(0, lastNsInd);
			shorten.append(prefix + ":");
			try {
				shorten.append(URLEncoder.encode(uriStr.substring(lastNsInd, uriStr.length()),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else{
			// Generating new prefix and ns, insert it to the trie,
			lastNsInd = getLastIndexOfDelimiter(uriStr);
			prefix = RDFWriteUtils.getNextPrefixNs();
			if (lastNsInd > 0 && uriStr.charAt(lastNsInd-1) != '/' && uriStr.charAt(lastNsInd-2) != ':' ) {
				ns = uriStr.substring(0, lastNsInd + 1);
				shorten.append(prefix + ":");
				try {
					if (!uriStr.substring(lastNsInd+1, uriStr.length()).isEmpty()){
						shorten.append(URLEncoder.encode(uriStr.substring(lastNsInd+1, uriStr.length()),"UTF-8"));
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				ns = uriStr;
				shorten.append(prefix + ":");
			}
			System.out.println(uriStr + "\t" + ns + "\t" + prefix + "\t" + shorten.toString());
			insert(ns, prefix);
		}
		
        if (RDFWriteUtils.prefixMapping.get(prefix) == null) {
        	RDFWriteUtils.prefixMapping.put( prefix, ns );
        	return URIShorteningTriplet.createTriplet(shorten.toString(), prefix, ns);
        }
		return URIShorteningTriplet.createTriplet(shorten.toString(), null, null);

	}
	
	public int getLastIndexOfDelimiter(String uri){
		int ind = uri.length()-1;
		int pastProtocol = 0;
		while (ind >=0){
			if (uri.charAt(ind) == '/' || uri.charAt(ind) == '#' || uri.charAt(ind) == ':'){
				if (pastProtocol < 3) return ind;
			}
			if (uri.charAt(ind) == '/' && pastProtocol == 0 || pastProtocol == 1) pastProtocol++;
			if (uri.charAt(ind) == ':' && pastProtocol == 2) pastProtocol++;
			ind--;
		}
		return ind;
	}
	
	public boolean isSPDelimiter(char c){
		return (c == '/' || c == '#' || c == ':');
	}
}
