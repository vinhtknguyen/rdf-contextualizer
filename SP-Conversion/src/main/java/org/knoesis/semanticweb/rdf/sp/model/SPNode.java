package org.knoesis.semanticweb.rdf.sp.model;

public class SPNode {
	
	protected org.apache.jena.graph.Node jenaNode = null;
	public org.apache.jena.graph.Node getJenaNode() {
		return jenaNode;
	}

	public void setJenaNode(org.apache.jena.graph.Node jenaNode) {
		this.jenaNode = jenaNode;
	}

	protected boolean isSingletonProperty = false;
	protected String shorten = null;

	public SPNode(org.apache.jena.graph.Node node, boolean isSP) {
		this.setJenaNode(node);
		this.setSingletonProperty(isSP);
	}
	
	public SPNode(org.apache.jena.graph.Node node){
		this.setJenaNode(node);
		this.setSingletonProperty(false);
	}


	public boolean isSingletonProperty() {
		return isSingletonProperty;
	}

	public void setSingletonProperty(boolean isSingletonProperty) {
		this.isSingletonProperty = isSingletonProperty;
	}

	public String getShorten() {
		return shorten;
	}

	public void setShorten(String shorten) {
		this.shorten = shorten;
	}

	
}
