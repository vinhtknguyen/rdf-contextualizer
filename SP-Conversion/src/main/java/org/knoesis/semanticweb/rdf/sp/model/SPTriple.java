package org.knoesis.semanticweb.rdf.sp.model;

import java.util.ArrayList;

public class SPTriple {

	protected SPNode subject = null;
	protected SPNode predicate = null;
	protected SPNode object = null;
	protected ArrayList<SPTriple> metaTriples = new ArrayList<SPTriple>();
	protected ArrayList<SPTriple> singletonPropertyTriples = new ArrayList<SPTriple>();
	protected boolean isSingletonTriple;
	
	public SPTriple(SPNode s, SPNode p, SPNode o) {
		subject = s;
		predicate = p;
		object = o;
	}
	
	public SPTriple(org.apache.jena.graph.Node s, org.apache.jena.graph.Node p, org.apache.jena.graph.Node o, String ext){
		subject = new SPNode(s, false);
		predicate = new SPNode(p, false);
		object = new SPNode(o, false);
		this.setSingletonTriple(false);
	}
	
	public SPTriple(org.apache.jena.graph.Node s, org.apache.jena.graph.Node p, org.apache.jena.graph.Node o){
		subject = new SPNode(s, false);
		predicate = new SPNode(p, false);
		object = new SPNode(o, false);
	}
	
	public void addMetaTriple(SPTriple triple){
		if (predicate.equalsTo(triple.subject)){
			metaTriples.add(triple);
		}
	}
	
	public void addSingletonPropertyTriple(SPTriple triple){
		if (predicate.equalsTo(triple.subject) && triple.getPredicate().isSingletonPropertyOf()){
			singletonPropertyTriples.add(triple);
		}
	}
	public ArrayList<SPTriple> getMetaTriples() {
		return metaTriples;
	}

	public void setMetaTriples(ArrayList<SPTriple> metaTriples) {
		this.metaTriples = metaTriples;
	}

	public ArrayList<SPTriple> getSingletonPropertyTriples() {
		return singletonPropertyTriples;
	}

	public void setSingletonPropertyTriples(
			ArrayList<SPTriple> singletonPropertyTriples) {
		this.singletonPropertyTriples = singletonPropertyTriples;
	}

	public SPNode getSubject() {
		return subject;
	}

	public void setSubject(SPNode subject) {
		this.subject = subject;
	}

	public SPNode getPredicate() {
		return predicate;
	}

	public void setPredicate(SPNode predicate) {
		this.predicate = predicate;
	}

	public SPNode getObject() {
		return object;
	}

	public void setObject(SPNode object) {
		this.object = object;
	}
	
	public boolean isSingletonTriple() {
		if (this.predicate.isSingletonProperty())
			this.setSingletonTriple(true);
		return isSingletonTriple;
	}

	public void setSingletonTriple(boolean isSingletonTriple) {
		this.isSingletonTriple = isSingletonTriple;
	}
	public String toString(){
		
		return this.subject.getJenaNode().toString() + "\t" + this.predicate.getJenaNode().toString() + "\t" + this.object.getJenaNode().toString() ;
		
	}
	
	public String toShortenString(){
		
		return this.subject.shorten + "\t" + this.predicate.shorten + "\t" + this.object.shorten ;
		
	}
	

}
