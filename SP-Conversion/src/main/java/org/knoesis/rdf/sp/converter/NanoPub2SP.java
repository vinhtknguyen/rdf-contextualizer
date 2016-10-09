package org.knoesis.rdf.sp.converter;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.Quad;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.model.SPNode;
import org.knoesis.rdf.sp.model.SPTriple;

public class NanoPub2SP extends ContextualRepresentationConverter {
	
	final static Logger logger = Logger.getLogger(NanoPub2SP.class);


	@Override
	public SPTriple transformQuad(Quad quad){
		
		if (quad.getGraph() != null && !quad.getPredicate().toString().equals(singletonPropertyOf.toString())){
		
			StringBuilder singletonBdr = null;
			singletonBdr = new StringBuilder();
			singletonBdr.append(quad.getPredicate().toString());
			singletonBdr.append(uuid.getSPDelimiter());
			singletonBdr.append(uuid.getNextUUID());
			
			SPNode singletonNode = new SPNode(NodeFactory.createURI(singletonBdr.toString()), true);

			SPTriple singletonTriple = new SPTriple(new SPNode(quad.getSubject()), singletonNode, new SPNode(quad.getObject()));
			singletonTriple.addSingletonInstanceTriple(new SPTriple(singletonNode, singletonPropertyOf, new SPNode(quad.getPredicate())));
			return singletonTriple;
		}
		return new SPTriple(quad.getSubject(), quad.getPredicate(), quad.getObject());
	}


	public NanoPub2SP() {
		super();
	}


	public NanoPub2SP(long _uuidInitNum, String _uuidInitStr) {
		super(_uuidInitNum, _uuidInitStr);
	}

}

