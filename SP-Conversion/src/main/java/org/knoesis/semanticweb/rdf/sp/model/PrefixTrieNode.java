package org.knoesis.semanticweb.rdf.sp.model;

import java.util.HashMap;

public class PrefixTrieNode {
	char c;
	HashMap<Character, PrefixTrieNode> children = new HashMap<Character, PrefixTrieNode>();
	boolean isLeaf;
	boolean isPrefix;
	String prefix = null;
	

	public PrefixTrieNode() {
	}

	public PrefixTrieNode(char c) {
		this.c = c;
	}
	
}
