package org.knoesis.rdf.sp.converter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.sparql.core.Quad;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.inference.ContextualInference;
import org.knoesis.rdf.sp.model.*;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;

public class NamedGraph2SP extends ContextualRepresentationConverter{

	final static Logger logger = Logger.getLogger(NamedGraph2SP.class);

	public NamedGraph2SP(long spPrefixNum, String spPrefixStr,
			String spDelimiter) {
		super(spPrefixNum, spPrefixStr, spDelimiter);
		// TODO Auto-generated constructor stub
	}
	protected SPNode namedGraphProp = null;

	public NamedGraph2SP(){
		super();
	}
	
	@Override
	public void transformQuad(BufferedWriter writer, Quad quad, String ext, boolean isInfer, ContextualInference con){

		List<SPTriple> triples = new LinkedList<SPTriple>();
		
		if (quad == null){
			return;
		}
		
		if (quad.getGraph() != null ){

			StringBuilder singletonBdr = null;
			singletonBdr = new StringBuilder();
			singletonBdr.append(quad.getPredicate().toString());
			singletonBdr.append(this.getSPDelimiter());
			singletonBdr.append(this.getNextUUID());
			
			SPNode singletonNode = new SPNode(singletonBdr.toString(), true);
			
			SPTriple singletonTriple = new SPTriple(new SPNode(quad.getSubject()), singletonNode, new SPNode(quad.getObject()));
			singletonTriple.addSingletonInstanceTriple(new SPTriple(singletonNode, this.singletonPropertyOf, new SPNode(quad.getPredicate())));
			singletonTriple.addMetaTriple(new SPTriple(singletonNode, this.getNamedGraphProp(), new SPNode(quad.getGraph())));
			
			triples.add(singletonTriple);

		} else {
			triples.add(new SPTriple(new SPNode(quad.getSubject()), new SPNode(quad.getPredicate()), new SPNode(quad.getObject())));
		}
		if (isInfer){
			// infer new triples and add them to the list
			triples.addAll(con.expandInferredTriples(con.infer(triples)));
		}
		try {
			writer.write(RDFWriteUtils.printTriples(triples, ext));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public SPNode getNamedGraphProp() {
		if (namedGraphProp == null)
			this.namedGraphProp = new SPNode(Constants.WAS_DERIVED_FROM);
		return namedGraphProp;
	}

	public void setNamedGraphProp(SPNode namedGraphProp) {
		this.namedGraphProp = namedGraphProp;
	}
		
	public void setNamedGraphProp(String prop){
		if (prop != null)
		this.namedGraphProp = new SPNode(prop);
	}
		
 
}
