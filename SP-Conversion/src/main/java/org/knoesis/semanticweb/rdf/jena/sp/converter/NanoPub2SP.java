package org.knoesis.semanticweb.rdf.jena.sp.converter;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.Quad;
import org.apache.log4j.Logger;
import org.knoesis.semanticweb.rdf.sp.model.SPNode;
import org.knoesis.semanticweb.rdf.sp.model.SPTriple;

public class NanoPub2SP extends ContextualRepresentationConverter {
	
	final static Logger logger = Logger.getLogger(NanoPub2SP.class);


	@Override
	public List<SPTriple> transformQuad(Quad quad, String ext){
		
		List<SPTriple> triples = new LinkedList<SPTriple>();
		if (quad.getGraph() != null){
		
			StringBuilder singletonBdr = null;
			singletonBdr = new StringBuilder();
			singletonBdr.append(quad.getPredicate().toString());
			singletonBdr.append(this.getSPDelimiter());
			singletonBdr.append(this.getNextUUID());
			
			SPNode singletonNode = new SPNode(NodeFactory.createURI(singletonBdr.toString()));

			SPTriple singletonTriple = new SPTriple(new SPNode(quad.getSubject()), singletonNode, new SPNode(quad.getObject()));
			singletonTriple.addSingletonPropertyTriple(new SPTriple(singletonNode, this.singletonPropertyOf, new SPNode(quad.getPredicate())));

			triples.add(new SPTriple(singletonNode, this.singletonPropertyOf, new SPNode(quad.getPredicate())));
			triples.add(new SPTriple(new SPNode(quad.getSubject()), singletonNode, new SPNode(quad.getObject())));

		}
		return triples;
	}


	public NanoPub2SP() {
		super();
		// TODO Auto-generated constructor stub
	}


	public NanoPub2SP(long spPrefixNum, String spPrefixStr, String spDelimiter) {
		super(spPrefixNum, spPrefixStr, spDelimiter);
		// TODO Auto-generated constructor stub
	}
	

}

