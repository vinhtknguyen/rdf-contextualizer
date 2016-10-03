package org.knoesis.rdf.sp.model;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.graph.Node_URI;
import org.knoesis.rdf.sp.utils.Constants;
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
		if (Constants.DEFAULT_EXT.equals(Constants.TURTLE_EXT)) this.toN3();
	}

	public SPNode toN3(){
		
		if (shorten != null & namespace != null && prefix != null) return this;
		
		if (jenaNode == null) return null;
		
	    if (jenaNode.isURI()) {
			// shorten the whole URI with prefix 
			return shortenURI();
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
	    		out.append(node.toN3().getShorten());
		    	setNamespace(node.toN3().getNamespace());
		    	setPrefix(node.toN3().getPrefix());
	//		    		System.out.println("output datatype: " + toN3(((Literal) in).getDatatype()));
	    	}
	    	setShorten(out.toString());
	    }
		return this; 
	    
	}
	
	public String printNodePrefix(){
		toN3();
		if (RDFWriteUtils.prefixMapping.get(prefix) == null){
			RDFWriteUtils.prefixMapping.put(prefix, namespace);
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

	public SPNode shortenURI(){
		// shorten the whole URI with prefix 
		return RDFWriteUtils.trie.shortenURIWithPrefix(this);
		 
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

	
	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getPrefix() {
		return prefix;
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
