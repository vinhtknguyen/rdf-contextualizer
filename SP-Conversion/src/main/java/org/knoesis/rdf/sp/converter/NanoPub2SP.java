package org.knoesis.rdf.sp.converter;

import java.io.BufferedWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.Quad;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.model.SPNode;
import org.knoesis.rdf.sp.model.SPTriple;
import org.knoesis.rdf.sp.model.SpUUID;

public class NanoPub2SP extends ContextualRepresentationConverter {
	
	final static Logger logger = Logger.getLogger(NanoPub2SP.class);


	public List<SPTriple> transformQuad(BufferedWriter writer, Quad quad, String ext){
		
		List<SPTriple> triples = new LinkedList<SPTriple>();
		if (quad.getGraph() != null){
		
			StringBuilder singletonBdr = null;
			singletonBdr = new StringBuilder();
			singletonBdr.append(quad.getPredicate().toString());
			singletonBdr.append(SpUUID.spDelimiter);
			singletonBdr.append(SpUUID.getNextUUID());
			
			SPNode singletonNode = new SPNode(NodeFactory.createURI(singletonBdr.toString()), true);

			SPTriple singletonTriple = new SPTriple(new SPNode(quad.getSubject()), singletonNode, new SPNode(quad.getObject()));
			singletonTriple.addSingletonInstanceTriple(new SPTriple(singletonNode, singletonPropertyOf, new SPNode(quad.getPredicate())));
			triples.add(singletonTriple);
		}
		return triples;
	}


	public NanoPub2SP() {
		super();
	}

}

