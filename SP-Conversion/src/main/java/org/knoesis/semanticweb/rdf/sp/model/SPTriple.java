package org.knoesis.semanticweb.rdf.sp.model;

public class SPTriple {

	protected SPNode subject = null;
	protected SPNode predicate = null;
	protected SPNode object = null;

	public SPTriple(SPNode s, SPNode p, SPNode o) {
		subject = s;
		predicate = p;
		object = o;
	}
	
	public SPTriple(org.apache.jena.graph.Node s, org.apache.jena.graph.Node p, org.apache.jena.graph.Node o){
		subject = new SPNode(s);
		predicate = new SPNode(p);
		object = new SPNode(o);
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
	
	public String toString(){
		
		return this.subject.getJenaNode().toString() + "\t" + this.predicate.getJenaNode().toString() + "\t" + this.object.getJenaNode().toString() ;
		
	}
	
	public String toShortenString(){
		
		return this.subject.shorten + "\t" + this.predicate.shorten + "\t" + this.object.shorten ;
		
	}
	

}
