package org.knoesis.semanticweb.rdf.utils;

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
	
	public String[] shortenURIWithPrefix(Node uri) {

		String[] output = new String[2];
		Map<Character, PrefixTrieNode> children = root.children;
		StringBuilder out = new StringBuilder();
		
		PrefixTrieNode curnode = null, latestLeaf = null;
		int i = 0;
		String uriStr = uri.toString();
		for (i = 0; i < uriStr.length(); i++) {
			char c = uriStr.charAt(i);
			if (children.containsKey(c)) {
				curnode = children.get(c);
				children = curnode.children;
				if (curnode.isLeaf) latestLeaf = curnode;
			} else {
				// No leaf with prefix
				if (latestLeaf == null){
					output[0] = "<" + uriStr + ">";
					output[1] = null;
					return output;
				} else if (latestLeaf != null){
					// Construct the shorted uri
					out.append(latestLeaf.prefix);
					out.append(":");
					out.append(uriStr.subSequence(i, uriStr.length()));
			        if (RDFWriteUtils.prefixMapping.get(latestLeaf.prefix) == null) {
			        	RDFWriteUtils.prefixMapping.put( latestLeaf.prefix, uriStr.substring(0, i) );
			        	output[1] = RDFWriteUtils.printPrefix(uriStr.substring(0, i), latestLeaf.prefix);
			        }
			        output[0] = out.toString();
					return output;
				}
						
			}
		}
		
		return output;
	}
}
