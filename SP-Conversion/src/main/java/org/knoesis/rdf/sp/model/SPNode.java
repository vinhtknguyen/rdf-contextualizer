package org.knoesis.rdf.sp.model;

import java.util.Map;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.graph.Node_URI;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;

public class SPNode {
	
	protected org.apache.jena.graph.Node jenaNode = null;
	protected boolean isSingletonPropertyOf = false;
	protected boolean isSingletonProperty = false;
	protected String shorten = null;
	protected String namespace = null;
	protected String prefix = null;
	
	public SPNode(org.apache.jena.graph.Node node, boolean isSP) {
		this.setJenaNode(node);
		this.setSingletonPropertyOf(isSP);
	}
	
	public SPNode(org.apache.jena.graph.Node node){
		this.setJenaNode(node);
		this.setSingletonProperty(false);
	}

	public SPNode(String uri, boolean isSP){
		this.setJenaNode(NodeFactory.createURI(uri));
		this.setSingletonProperty(isSP);
	}

	public SPNode(String uri){
		this.setJenaNode(NodeFactory.createURI(uri));
		this.setSingletonProperty(false);
	}

	public SPNode toN3(PrefixTrie prefixMapping, Map<String,String> trie, boolean shortenAllURIs){
		
		if (shorten != null) return this;
		
		if (jenaNode == null) return null;
		
	    if (jenaNode.isURI()) {
			// shorten the whole URI with prefix 
			return shortenURI(prefixMapping, trie, shortenAllURIs);
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
	
	public String printNodePrefix(PrefixTrie prefixMapping, Map<String,String> trie, boolean shortenAllURIs){
		if (this.shorten == null){
			SPNode node = toN3(prefixMapping, trie, shortenAllURIs);
			this.prefix = node.getPrefix();
			this.namespace = node.getNamespace();
			this.shorten = node.getShorten();
		}
		StringBuilder out = new StringBuilder();
		if (shortenAllURIs && prefixMapping.searchPrefix(this.prefix) == null){
			prefixMapping.insert(this.namespace, this.prefix);
			out.append("@prefix\t");
			out.append(this.prefix);
			out.append(":\t<");
			out.append(this.namespace);
			out.append(">\t . \n");
			
		}
		return out.toString();
	}

	public SPNode shortenURI(PrefixTrie prefixMapping, Map<String,String> trie, boolean shortenAllURIs){
		// shorten the whole URI with prefix 
		// shorten the whole URI with prefix 
		int len = jenaNode.toString().length();
		String ns = null, prefix = null;
		StringBuilder shorten = new StringBuilder();
		
		if (!shortenAllURIs) {
			SPNode node = prefixMapping.shortenURI(this);
			setShorten(node.getShorten());
			return this;
		}
		
		if (trie.containsKey(jenaNode.toString())){
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
	}


	public String toNT(){
		
	    if (jenaNode instanceof Node_URI) {
			// shorten the whole URI with prefix 
			return "<" + jenaNode.toString() + ">";
	    }
	    
	    if (jenaNode instanceof Node_Literal) {
	    	StringBuilder out = new StringBuilder();
	    	out.append("\"");
	    	out.append(jenaNode.getLiteralLexicalForm());
	    	out.append("\"");

	    	// shorten the whole URI with prefix for data type
	    	if (!jenaNode.getLiteralDatatypeURI().equals("")){
	    		out.append("^^");
	    		out.append((new SPNode(NodeFactory.createURI(jenaNode.getLiteralDatatypeURI()))).toNT());
	    	}
	    	return out.toString();
	    } 
	    
	    return jenaNode.toString();
	}

	
	public String getNamespace(PrefixTrie prefixMapping, Map<String,String> trie, boolean shortenAllURIs) {
		if (namespace == null) toN3(prefixMapping, trie, shortenAllURIs);
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getPrefix(PrefixTrie prefixMapping, Map<String,String> trie, boolean shortenAllURIs) {
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

	public boolean isSingletonProperty() {
		return isSingletonProperty;
	}

	public void setSingletonProperty(boolean isSingletonProperty) {
		this.isSingletonProperty = isSingletonProperty;
	}

	public boolean isSingletonPropertyOf() {
		return isSingletonPropertyOf;
	}

	public void setSingletonPropertyOf(boolean isSingletonProperty) {
		this.isSingletonPropertyOf = isSingletonProperty;
	}

	public String getShorten(PrefixTrie prefixMapping, Map<String,String> trie, boolean shortenAllURIs) {
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
		if (this.shorten != null && node.shorten != null){
			return this.shorten.equals(node.shorten);
		} else {
			return this.getJenaNode().toString().equals(node.jenaNode.toString());
		}
	}
	
	public String toString(){
		return this.getJenaNode().toString();
	}

}
