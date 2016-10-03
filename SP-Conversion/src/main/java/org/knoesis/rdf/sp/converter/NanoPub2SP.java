package org.knoesis.rdf.sp.converter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.Quad;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.inference.ContextualInference;
import org.knoesis.rdf.sp.model.SPNode;
import org.knoesis.rdf.sp.model.SPTriple;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;

public class NanoPub2SP extends ContextualRepresentationConverter {
	
	final static Logger logger = Logger.getLogger(NanoPub2SP.class);


	@Override
	public void transformQuad(BufferedWriter writer, Quad quad, String ext, boolean isInfer, ContextualInference con){
		
		List<SPTriple> triples = new LinkedList<SPTriple>();
		if (quad.getGraph() != null){
		
			StringBuilder singletonBdr = null;
			singletonBdr = new StringBuilder();
			singletonBdr.append(quad.getPredicate().toString());
			singletonBdr.append(this.getSPDelimiter());
			singletonBdr.append(this.getNextUUID());
			
			SPNode singletonNode = new SPNode(NodeFactory.createURI(singletonBdr.toString()));

			SPTriple singletonTriple = new SPTriple(new SPNode(quad.getSubject()), singletonNode, new SPNode(quad.getObject()));
			singletonTriple.addSingletonInstanceTriple(new SPTriple(singletonNode, this.singletonPropertyOf, new SPNode(quad.getPredicate())));

			triples.add(new SPTriple(singletonNode, this.singletonPropertyOf, new SPNode(quad.getPredicate())));
			triples.add(new SPTriple(new SPNode(quad.getSubject()), singletonNode, new SPNode(quad.getObject())));

		}
		try {
			if (isInfer){
				// infer new triples and add them to the list
				triples.addAll(con.expandInferredTriples(con.infer(triples)));
			}
			writer.write(RDFWriteUtils.printTriples(triples, ext));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

