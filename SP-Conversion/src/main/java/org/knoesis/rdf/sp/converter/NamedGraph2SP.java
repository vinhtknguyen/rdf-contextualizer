package org.knoesis.rdf.sp.converter;

import java.io.BufferedWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.sparql.core.Quad;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.model.*;
import org.knoesis.rdf.sp.utils.Constants;

public class NamedGraph2SP extends ContextualRepresentationConverter{

	final static Logger logger = Logger.getLogger(NamedGraph2SP.class);

	protected static SPNode namedGraphProp = new SPNode(Constants.WAS_DERIVED_FROM);

	public NamedGraph2SP(){
		super();
	}
	
	public NamedGraph2SP(long _uuidInitNum, String _uuidInitStr) {
		super(_uuidInitNum, _uuidInitStr);
	}

	public List<SPTriple> transformQuad(BufferedWriter writer, Quad quad, String ext){

		List<SPTriple> triples = new LinkedList<SPTriple>();
		
		if (quad == null){
			return null;
		}
		
		if (quad.getGraph() != null ){

			StringBuilder singletonBdr = null;
			singletonBdr = new StringBuilder();
			singletonBdr.append(quad.getPredicate().toString());
			singletonBdr.append(uuid.getSPDelimiter());
			singletonBdr.append(uuid.getNextUUID());
			
			SPNode singletonNode = new SPNode(singletonBdr.toString(), true);
			
			SPTriple singletonTriple = new SPTriple(new SPNode(quad.getSubject()), singletonNode, new SPNode(quad.getObject()));
			singletonTriple.addSingletonInstanceTriple(new SPTriple(singletonNode, singletonPropertyOf, new SPNode(quad.getPredicate())));
			singletonTriple.addMetaTriple(new SPTriple(singletonNode, namedGraphProp, new SPNode(quad.getGraph())));
			
			triples.add(singletonTriple);

		} else {
			triples.add(new SPTriple(new SPNode(quad.getSubject()), new SPNode(quad.getPredicate()), new SPNode(quad.getObject())));
		}
		return triples;
	}

	public SPNode getNamedGraphProp() {
		if (namedGraphProp == null)
			namedGraphProp = new SPNode(Constants.WAS_DERIVED_FROM);
		return namedGraphProp;
	}

	public void setNamedGraphProp(SPNode _namedGraphProp) {
		namedGraphProp = _namedGraphProp;
	}
		
	public void setNamedGraphProp(String prop){
		if (prop != null)
		namedGraphProp = new SPNode(prop);
	}
		
 
}
