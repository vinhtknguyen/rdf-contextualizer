package org.knoesis.rdf.sp.model;

import java.util.ArrayList;
import java.util.Map;

/**
 * This class represents a singleton triple with its associated triples in the singleton property pattern
 * sub	sp	obj						:singleton triple
 * sp	rdf:singletonPropertyOf	p	: singleton instance triple
 * sp	mp	mv						: meta triple
 * @author vinh
 *
 */
public class SPTriple {

	protected SPNode subject = null;
	protected SPNode predicate = null;
	protected SPNode object = null;
	/* *
	 * This contains all the meta triples of this singleton triple
	 * */
	protected ArrayList<SPTriple> metaTriples = new ArrayList<SPTriple>();
	/* *
	 * This contains all the meta triples of this singleton triple
	 * */
	protected ArrayList<SPTriple> singletonInstanceTriples = new ArrayList<SPTriple>();
	/* *
	 * This contains all the generic triples of this singleton triple
	 * */
	protected ArrayList<SPTriple> genericPropertyTriples = new ArrayList<SPTriple>();

	public SPTriple(SPNode s, SPNode p, SPNode o) {
		subject = s;
		predicate = p;
		object = o;
	}
	
	public SPTriple(SPTriple triple){
		subject = triple.getSubject();
		predicate = triple.getPredicate();
		object = triple.getObject();
		metaTriples = new ArrayList<SPTriple>();
		metaTriples.addAll(triple.getMetaTriples());
		singletonInstanceTriples = new ArrayList<SPTriple>();
		singletonInstanceTriples.addAll(triple.getSingletonInstanceTriples());
		genericPropertyTriples = new ArrayList<SPTriple>();
		genericPropertyTriples.addAll(triple.getGenericPropertyTriples());
		
	}
	
	public SPTriple(org.apache.jena.graph.Node s, org.apache.jena.graph.Node p, org.apache.jena.graph.Node o, String ext){
		subject = new SPNode(s, false);
		predicate = new SPNode(p, false);
		object = new SPNode(o, false);
	}
	
	public SPTriple(org.apache.jena.graph.Node s, org.apache.jena.graph.Node p, org.apache.jena.graph.Node o){
		subject = new SPNode(s, false);
		predicate = new SPNode(p, false);
		object = new SPNode(o, false);
	}
	
	public void addMetaTriple(SPTriple triple){
		if (this.isSingletonTriple() && predicate.equalsTo(triple.subject)){
			metaTriples.add(triple);
		}
	}
	
	public void addSingletonInstanceTriple(SPTriple triple){
		if (this.isSingletonTriple() && predicate.equalsTo(triple.subject)){
			singletonInstanceTriples.add(triple);
		} 
	}
	
	public void addGenericPropertyTriple(SPTriple triple){
		if (this.isSingletonTriple() && subject.equalsTo(triple.subject) && object.equalsTo(triple.object)){
			genericPropertyTriples.add(triple);
		}
	}
	public void addMetaTriple(SPNode s, SPNode p, SPNode o){
//		System.out.println("adding meta triples" + s.toString() + "\t" + p.toString() + "\t" + o.toString());
//		System.out.println("for " + this.toString());
//		System.out.println(this.isSingletonTriple() + " and " + predicate.equalsTo(s));
		if (this.isSingletonTriple() && predicate.equalsTo(s)){
			metaTriples.add(new SPTriple(s, p, o));
		}
	}
	
	public void addSingletonInstanceTriple(SPNode s, SPNode p, SPNode o){
		if (this.isSingletonTriple() && predicate.equalsTo(s)){
//			System.out.println("adding singleton triples" + s.toString() + "\t" + p.toString() + "\t" + o.toString());
			singletonInstanceTriples.add(new SPTriple(s, p, o));
		}
	}
	
	public void addGenericPropertyTriple(SPNode s, SPNode p, SPNode o){
		if (this.isSingletonTriple() && subject.equalsTo(s) && object.equalsTo(o)){
			genericPropertyTriples.add(new SPTriple(s, p, o));
		}
	}
	
	public ArrayList<SPTriple> getMetaTriples() {
		return metaTriples;
	}

	public void setMetaTriples(ArrayList<SPTriple> metaTriples) {
		this.metaTriples = metaTriples;
	}

	public ArrayList<SPTriple> getSingletonInstanceTriples() {
		return singletonInstanceTriples;
	}

	public void setSingletonPropertyTriples(
			ArrayList<SPTriple> singletonPropertyTriples) {
		this.singletonInstanceTriples = singletonPropertyTriples;
	}

	public String printTriple2N3(Map<String,String> prefixMapping, Map<String,String> trie, boolean shortenAllURIs){
		
		StringBuilder out = new StringBuilder();
		StringBuilder prefixes = new StringBuilder();

		if (shortenAllURIs) prefixes.append(this.printTriplePrefix(prefixMapping, trie, shortenAllURIs));

		out.append(this.getSubject().getShorten(prefixMapping, trie, shortenAllURIs));
		out.append('\t');
		
		out.append(this.getPredicate().getShorten(prefixMapping, trie, shortenAllURIs));
		out.append('\t');
		
		out.append(this.getObject().getShorten(prefixMapping, trie, shortenAllURIs));
		out.append("\t . \n");
		
		prefixes.append(out);
		
		return prefixes.toString();
	}
	
	public String printTriplePrefix(Map<String,String> prefixMapping, Map<String,String> trie, boolean shortenAllURIs){
		
		StringBuilder out = new StringBuilder();
			// Print the prefix if not added before
		out.append(this.subject.printNodePrefix(prefixMapping, trie, shortenAllURIs));
		out.append(this.predicate.printNodePrefix(prefixMapping, trie, shortenAllURIs));
		out.append(this.object.printNodePrefix(prefixMapping, trie, shortenAllURIs));
		return out.toString();
	}

	
	public String printTriple2NT(){
		
		StringBuilder  out = new StringBuilder(this.getSubject().toNT());
		out.append('\t');
		out.append(this.getPredicate().toNT());
		out.append('\t');
		out.append(this.getObject().toNT());
		out.append("\t . \n");
		
		return out.toString();
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
		return this.predicate.isSingletonPropertyNode();
	}
	public ArrayList<SPTriple> getGenericPropertyTriples() {
		return genericPropertyTriples;
	}

	public void setGenericPropertyTriples(ArrayList<SPTriple> genericPropertyTriples) {
		this.genericPropertyTriples = genericPropertyTriples;
	}

	public String toString(){
		StringBuilder out = new StringBuilder();
		if (this.subject != null) out.append(this.subject.getJenaNode().toString() + "\t");
		if (this.predicate != null) out.append(this.predicate.getJenaNode().toString() + "\t");
		if (this.object != null) out.append(this.object.getJenaNode().toString() + "\t");
		return  out.append(" \t . \n").toString();
		
	}
	
	public String printAll(){
		StringBuilder out = new StringBuilder();
		if (this.toString() != null){
			out.append(this.toString());
			for (SPTriple triple : this.singletonInstanceTriples){
				out.append("\t\t Singleton triples: " + triple.toString());
			}
			for (SPTriple triple : this.genericPropertyTriples){
				out.append("\t\t Generic triples: " + triple.toString());
			}
			for (SPTriple triple : this.metaTriples){
				out.append("\t\t Meta triples: " + triple.toString());
			}
		}
		return out.toString();
	}
	
	public String toShortenString(){
		
		return this.subject.shorten + "\t" + this.predicate.shorten + "\t" + this.object.shorten ;
		
	}
	

}
