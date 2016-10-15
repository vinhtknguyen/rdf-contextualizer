package org.knoesis.rdf.sp.model;


import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.graph.Node_URI;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.exception.SPException;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;

public class SPNode {
	final static Logger logger = Logger.getLogger(SPNode.class);
	
	protected Node jenaNode = null;
	protected boolean isSingletonPropertyOf = false;
	protected boolean isSingletonPropertyNode = false;
	protected String shorten = null;
	protected String namespace = null;
	protected String prefix = null;

	protected String nodePrefix = null;
	protected String nodeSuffix = null;
	protected String datatypePrefix = null;
	protected String datatypeSuffix = null;
	
	public SPNode(Node node, boolean isSPNode) {
		this.setJenaNode(node);
		this.setSingletonPropertyNode(isSPNode);
//		getNodePrefixes();
//		getDataTypePrefixes();
	}
	
	public SPNode(Node node){
		this.setJenaNode(node);
//		getNodePrefixes();
//		getDataTypePrefixes();
	}

	public SPNode(String uri, boolean isSPNode){
		this.setJenaNode(NodeFactory.createURI(uri));
		this.setSingletonPropertyNode(isSPNode);
//		getNodePrefixes();
//		getDataTypePrefixes();
	}

	public SPNode(String uri){
		this.setJenaNode(NodeFactory.createURI(uri));
		this.setSingletonPropertyNode(false);
//		getNodePrefixes();
//		getDataTypePrefixes();
	}
	
	public void getNodePrefixes(){
		int lastNsInd = RDFWriteUtils.getLastIndexOfDelimiterWithSecondPeriod(jenaNode.toString());
		if (lastNsInd > 2 && jenaNode.toString().charAt(lastNsInd-1) != '/' && jenaNode.toString().charAt(lastNsInd-2) != ':' ) {
			nodePrefix = jenaNode.toString().substring(0, lastNsInd + 1);
			nodeSuffix = RDFWriteUtils.normalizeN3(jenaNode.toString().substring(lastNsInd+1, jenaNode.toString().length()));
		}
	}
	
	public void getDataTypePrefixes(){
		if (jenaNode.isLiteral()){
			String datatype = jenaNode.getLiteralDatatypeURI();
			int lastNsInd = RDFWriteUtils.getLastIndexOfDelimiterWithSecondPeriod(datatype);
			if (lastNsInd > 2 && datatype.charAt(lastNsInd-1) != '/' && datatype.charAt(lastNsInd-2) != ':' ) {
				datatypePrefix = datatype.substring(0, lastNsInd + 1);
				datatypeSuffix = RDFWriteUtils.normalizeN3(datatype.substring(lastNsInd+1, datatype.length()));
			}
		}
		
	}

	public SPNode toN3(Map<String,String> prefixMapping, Map<String,String> trie, boolean shortenAllURIs){
		
		if (shorten != null) return this;
		
		if (jenaNode == null) return null;
		
	    if (jenaNode.isURI()) {
			// shorten the whole URI with prefix 
			return shortenURIWithConcurrentTrieMap(prefixMapping, trie, shortenAllURIs);
	    }
	    
	    if (jenaNode.isLiteral()) {
	    	StringBuilder out = new StringBuilder();
	    	out.append("\"");
	    	out.append(jenaNode.getLiteralLexicalForm());
	    	out.append("\"");
	    	
			// shorten the whole URI with prefix for data type
	    	if (!jenaNode.getLiteralDatatypeURI().equals("")){
	    		out.append("^^");
	    		SPNode node = new SPNode(jenaNode.getLiteralDatatypeURI());
	    		node.toN3(prefixMapping, trie, shortenAllURIs);
	    		out.append(node.getShorten(prefixMapping, trie, shortenAllURIs));
		    	setNamespace(node.getNamespace(prefixMapping, trie, shortenAllURIs));
		    	setPrefix(node.getPrefix(prefixMapping, trie, shortenAllURIs));
	//		    		System.out.println("output datatype: " + toN3(((Literal) in).getDatatype()));
	    	}
	    	setShorten(out.toString());
	    }
		return this; 
	    
	}
	public SPNode toN3(Map<String,String> prefixMapping, PrefixTrie trie, boolean shortenAllURIs){
		
		if (shorten != null) return this;
		
		if (jenaNode == null) return null;
		
	    if (jenaNode.isURI()) {
			// shorten the whole URI with prefix 
			return shortenURIWithPrefixTrie(prefixMapping, trie, shortenAllURIs);
	    }
	    
	    if (jenaNode.isLiteral()) {
	    	StringBuilder out = new StringBuilder();
	    	out.append("\"");
	    	out.append(jenaNode.getLiteralLexicalForm().replaceAll("\"", ""));
	    	out.append("\"");
	    	
			// shorten the whole URI with prefix for data type
	    	if (!jenaNode.getLiteralDatatypeURI().equals("")){
	    		out.append("^^");
	    		SPNode node = new SPNode(jenaNode.getLiteralDatatypeURI());
	    		node.toN3(prefixMapping, trie, shortenAllURIs);
	    		out.append(node.getShorten(prefixMapping, trie, shortenAllURIs));
		    	setNamespace(node.getNamespace(prefixMapping, trie, shortenAllURIs));
		    	setPrefix(node.getPrefix(prefixMapping, trie, shortenAllURIs));
	//		    		System.out.println("output datatype: " + toN3(((Literal) in).getDatatype()));
	    	}
	    	setShorten(out.toString());
	    }
		return this; 
	    
	}
	
	public String printNodePrefix(Map<String,String> prefixMapping, Map<String,String> trie, boolean shortenAllURIs){
		StringBuilder out = new StringBuilder();
		if (this.shorten == null){
			SPNode node = toN3(prefixMapping, trie, shortenAllURIs);
			String existingPrefix = prefixMapping.get(node.getNamespace());
			if ( existingPrefix != null){
				
			}
			this.prefix = node.getPrefix();
			this.namespace = node.getNamespace();
			this.shorten = node.getShorten();
		}
		if (this.prefix != null && this.namespace != null && shortenAllURIs){
			if (prefixMapping.get(this.namespace) == null){
				prefixMapping.put(this.namespace, this.prefix);
				out.append("@prefix\t");
				out.append(this.prefix);
				out.append(":\t<");
				out.append(this.namespace);
				out.append(">\t . \n");
			} else {
				if (!prefixMapping.get(this.namespace).equals(this.prefix)){
					System.out.println("New prefix" + prefixMapping.get(this.namespace)+ " being created for the existing namespace " + this.prefix);
				}
			}
		}
		return out.toString();
	}
	public String printNodePrefix(Map<String,String> prefixMapping, PrefixTrie trie, boolean shortenAllURIs){
		StringBuilder out = new StringBuilder();
		if (this.shorten == null){
			SPNode node = toN3(prefixMapping, trie, shortenAllURIs);
			this.prefix = node.getPrefix();
			this.namespace = node.getNamespace();
			this.shorten = node.getShorten();
		}
		if (this.prefix != null && this.namespace != null){
			if (prefixMapping.get(this.namespace) == null){
				prefixMapping.put(this.namespace, this.prefix);
				out.append("@prefix\t");
				out.append(this.prefix);
				out.append(":\t<");
				out.append(this.namespace);
				out.append(">\t . \n");
			} else {
				if (!prefixMapping.get(this.namespace).equals(this.prefix)){
					logger.trace("New prefix" + prefixMapping.get(this.namespace)+ " being created for the existing namespace " + this.prefix);
				}
			}
		}
		return out.toString();
	}

	public SPNode shortenURIWithPrefixTrie(Map<String,String> prefixMapping, PrefixTrie trie, boolean shortenAllURIs){
		// shorten the whole URI with prefix 
		SPNode node;
		if (!shortenAllURIs) {
			// Check if this uri has prefix in the trie
			node = trie.shortenURI(this);
		} else {
			node = trie.shortenURIWithPrefix(this);
		}
		setShorten(node.getShorten());
		setPrefix(node.getPrefix());
		setNamespace(node.getNamespace());
		return this;
	
/* This code is for trie as a TrieMap<String,String>, not necessary for PrefixTrie
 * 		if (trie.containsKey(jenaNode.toString())){
			prefix = trie.get(jenaNode.toString());
			ns = jenaNode.toString();
			shorten.append(prefix + ":");
			setPrefix(prefix);
			setShorten(shorten.toString());
			setNamespace(ns);
			return this;
		} 
		
		int lastNsInd = RDFWriteUtils.getLastIndexOfDelimiterWithSecondPeriod(jenaNode.toString());
		if (lastNsInd > 2 && jenaNode.toString().charAt(lastNsInd-1) != '/' && jenaNode.toString().charAt(lastNsInd-2) != ':' ) {
			ns = jenaNode.toString().substring(0, lastNsInd + 1);
			prefix = trie.get(ns);
			if (prefix == null) {
				prefix = RDFWriteUtils.getNextPrefixNs();
				trie.put(ns, prefix);
				System.out.println("Prefix: " + ns + " \t " + prefix);
			}

			shorten.append(prefix + ":");
			if (!jenaNode.toString().substring(lastNsInd+1, len).isEmpty()){
				shorten.append(RDFWriteUtils.normalizeN3(jenaNode.toString().substring(lastNsInd+1, len)));
			}
			this.setNamespace(ns);
			this.setPrefix(prefix);
			this.setShorten(shorten.toString());
		} else {
			ns = jenaNode.toString();
			prefix = RDFWriteUtils.getNextPrefixNs();
			shorten.append(prefix + ":");
			trie.put(ns, prefix);
			System.out.println(jenaNode.toString() + " \t " + ns + "\t" + shorten);
		}
	    return this;
*/	}

	public SPNode shortenURIWithConcurrentTrieMap(Map<String,String> prefixMapping, Map<String,String> trie, boolean shortenAllURIs){
		// shorten the whole URI with prefix 
		StringBuilder shorten = new StringBuilder();
		String ns, prefix;
		int len = jenaNode.toString().length();
//		if (!shortenAllURIs){
//			setShorten("<" + jenaNode.toString() + ">");
//			return this;
//		}
//		
		int lastNsInd;
		if (shortenAllURIs){
			lastNsInd = RDFWriteUtils.getLastIndexOfDelimiter(jenaNode.toString());
		} else {
			lastNsInd = RDFWriteUtils.getLastIndexOfDelimiterWithSecondPeriod(jenaNode.toString());
		}
		if (lastNsInd > 2 && jenaNode.toString().charAt(lastNsInd-1) != '/' && jenaNode.toString().charAt(lastNsInd-2) != ':' ) {
			ns = jenaNode.toString().substring(0, lastNsInd + 1);
			prefix = trie.get(ns);
			if (prefix == null) {
				prefix = RDFWriteUtils.getNextPrefixNs();
				trie.put(ns, prefix);
				logger.trace("Prefix: " + ns + " \t " + prefix);
			}

			shorten.append(prefix + ":");
			if (!jenaNode.toString().substring(lastNsInd+1, len).isEmpty()){
				shorten.append(RDFWriteUtils.normalizeN3(jenaNode.toString().substring(lastNsInd+1, len)));
			}
			this.setNamespace(ns);
			this.setPrefix(prefix);
			this.setShorten(shorten.toString());
		} else {
			ns = jenaNode.toString();
			prefix = RDFWriteUtils.getNextPrefixNs();
			shorten.append(prefix + ":");
			trie.put(ns, prefix);
			logger.trace(jenaNode.toString() + " \t " + ns + "\t" + shorten);
		}
	    return this;
	}

	public String toNT(){
		
	    if (jenaNode instanceof Node_URI) {
			// shorten the whole URI with prefix 
			return "<" + jenaNode.toString() + ">";
	    }
	    
	    if (jenaNode instanceof Node_Literal) {
	    	StringBuilder out = new StringBuilder();
	    	out.append("\"");
	    	out.append(jenaNode.getLiteralLexicalForm().replaceAll("\"", "'"));
	    	out.append("\"");

	    	// shorten the whole URI with prefix for data type
	    	if (!jenaNode.getLiteralDatatypeURI().equals("")){
	    		out.append("^^");
	    		out.append((new SPNode(NodeFactory.createURI(jenaNode.getLiteralDatatypeURI()))).toNT());
	    	}
	    	return out.toString();
	    } 
	    
	    return "<" + jenaNode.toString() + ">";
	}

	
	public String getNamespace(Map<String,String> prefixMapping, Map<String,String> trie, boolean shortenAllURIs) {
		if (namespace == null) toN3(prefixMapping, trie, shortenAllURIs);
		return namespace;
	}

	public String getNamespace(Map<String,String> prefixMapping, PrefixTrie trie, boolean shortenAllURIs) {
		if (namespace == null) toN3(prefixMapping, trie, shortenAllURIs);
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getPrefix(Map<String,String> prefixMapping, Map<String,String> trie, boolean shortenAllURIs) {
		if (prefix == null) toN3(prefixMapping, trie, shortenAllURIs);
		return prefix;
	}

	public String getPrefix(Map<String,String> prefixMapping, PrefixTrie trie, boolean shortenAllURIs) {
		if (prefix == null) toN3(prefixMapping, trie, shortenAllURIs);
		return prefix;
	}

	public String getPrefix(){
		return prefix;
	}
	
	public String getNamespace(){
		return namespace;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public org.apache.jena.graph.Node getJenaNode() {
		return jenaNode;
	}

	public void setJenaNode(org.apache.jena.graph.Node jenaNode) {
		this.jenaNode = jenaNode;
	}

	public boolean isSingletonPropertyNode() {
		return isSingletonPropertyNode;
	}

	public void setSingletonPropertyNode(boolean isSingletonPropertyNode) {
		this.isSingletonPropertyNode = isSingletonPropertyNode;
	}

	public boolean isSingletonPropertyOf() {
		return isSingletonPropertyOf;
	}

	public void setSingletonPropertyOf(boolean isSingletonProperty) {
		this.isSingletonPropertyOf = isSingletonProperty;
	}

	public String getShorten(Map<String,String> prefixMapping, PrefixTrie trie, boolean shortenAllURIs) {
		if (prefix == null) toN3(prefixMapping, trie, shortenAllURIs);
		return shorten;
	}
	public String getShorten(Map<String,String> prefixMapping, Map<String,String> trie, boolean shortenAllURIs) {
		if (prefix == null) toN3(prefixMapping, trie, shortenAllURIs);
		return shorten;
	}

	public String getShorten() {
		return shorten;
	}

	public void setShorten(String shorten) {
		this.shorten = shorten;
	}
	
	public boolean equalsTo(SPNode node){
		return this.getJenaNode().toString().equals(node.jenaNode.toString());
	}
	
	public String toString(){
		return this.getJenaNode().toString();
	}
	public String getLongPrefix() {
		return nodePrefix;
	}

	public void setLongPrefix(String longPrefix) {
		this.nodePrefix = longPrefix;
	}

	public String getNodePrefix() {
		return nodePrefix;
	}

	public void setNodePrefix(String nodePrefix) {
		this.nodePrefix = nodePrefix;
	}

	public String getNodeSuffix() {
		return nodeSuffix;
	}

	public void setNodeSuffix(String nodeSuffix) {
		this.nodeSuffix = nodeSuffix;
	}

	public String getDatatypePrefix() {
		return datatypePrefix;
	}

	public void setDatatypePrefix(String datatypePrefix) {
		this.datatypePrefix = datatypePrefix;
	}

	public String getDatatypeSuffix() {
		return datatypeSuffix;
	}

	public void setDatatypeSuffix(String datatypeSuffix) {
		this.datatypeSuffix = datatypeSuffix;
	}

}
