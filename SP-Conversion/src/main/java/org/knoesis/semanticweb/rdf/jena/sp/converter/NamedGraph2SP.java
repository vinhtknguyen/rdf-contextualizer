package org.knoesis.semanticweb.rdf.jena.sp.converter;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.Quad;
import org.apache.log4j.Logger;

import org.knoesis.semanticweb.rdf.sp.model.*;
import org.knoesis.semanticweb.rdf.utils.Constants;

public class NamedGraph2SP extends ContextualRepresentationConverter{

	final static Logger logger = Logger.getLogger(NamedGraph2SP.class);

	public NamedGraph2SP(long spPrefixNum, String spPrefixStr,
			String spDelimiter, String singletonPropertyOfURI) {
		super(spPrefixNum, spPrefixStr, spDelimiter, singletonPropertyOfURI);
		this.namedGraphProp = new SPNode(NodeFactory.createURI(Constants.WAS_DERIVED_FROM), false);
		// TODO Auto-generated constructor stub
	}
	protected SPNode namedGraphProp = null;

	public NamedGraph2SP(){
		super();
		this.namedGraphProp = new SPNode(NodeFactory.createURI(Constants.WAS_DERIVED_FROM), false);
	}
	
	@Override
	public List<SPTriple> transformQuad(Quad quad){

		List<SPTriple> triples = new LinkedList<SPTriple>();
		
		if (quad == null){
			return triples;
		}
		if (quad.getGraph() != null ){

			StringBuilder singletonBdr = null;
			singletonBdr = new StringBuilder();
			singletonBdr.append(quad.getPredicate().toString());
			singletonBdr.append(this.getSPDelimiter());
			singletonBdr.append(this.getNextUUID());
			
			SPNode singletonNode = new SPNode(NodeFactory.createURI(singletonBdr.toString()), true);

			triples.add(new SPTriple(singletonNode, this.singletonPropertyOf, new SPNode(quad.getPredicate())));
			triples.add(new SPTriple(singletonNode, this.namedGraphProp, new SPNode(quad.getGraph())));
			triples.add(new SPTriple(new SPNode(quad.getSubject()), singletonNode, new SPNode(quad.getObject())));

		} else {
			triples.add(new SPTriple(new SPNode(quad.getSubject()), new SPNode(quad.getPredicate()), new SPNode(quad.getObject())));
			return triples;
		}
		return triples;
	}

	public SPNode getNamedGraphProp() {
		return namedGraphProp;
	}

	public void setNamedGraphProp(SPNode namedGraphProp) {
		this.namedGraphProp = namedGraphProp;
	}
	public void setNamedGraphProp(String prop){
		this.namedGraphProp = new SPNode(NodeFactory.createURI(prop));
	}
		
 
}
