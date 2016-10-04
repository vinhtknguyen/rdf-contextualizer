package org.knoesis.rdf.sp.converter;

import java.io.BufferedWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.graph.NodeFactory;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.model.SPNode;
import org.knoesis.rdf.sp.model.SPTriple;

public class Triple2SP extends ContextualRepresentationConverter {
	
	final static Logger logger = Logger.getLogger(NamedGraph2SP.class);
	protected SPNode metaPredicate = null;
	protected SPNode metaObject = null;
	
	public SPNode getMetaPredicate() {
		return metaPredicate;
	}

	public void setMetaPredicate(SPNode metaPredicate) {
		this.metaPredicate = metaPredicate;
	}

	public void setMetaPredicate(String metaPredicate) {
		SPNode sPNode = new SPNode(NodeFactory.createURI(metaPredicate));
		if (sPNode != null){
			this.metaPredicate = sPNode;
		}
	}

	public SPNode getMetaObject() {
		return metaObject;
	}

	public Triple2SP() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Triple2SP(long spPrefixNum, String spPrefixStr, String spDelimiter,
			String singletonPropertyOfURI) {
		super(spPrefixNum, spPrefixStr, spDelimiter);
		// TODO Auto-generated constructor stub
	}

	public void setMetaObject(SPNode metaObject) {
		this.metaObject = metaObject;
	}
	public void setMetaObject(String metaObject) {
		SPNode sPNode = new SPNode(NodeFactory.createURI(metaObject));
		if (sPNode != null){
			this.metaObject = sPNode;
		}
	}

	@Override
	public List<SPTriple> transformTriple(BufferedWriter writer, org.apache.jena.graph.Triple triple, String ext){
		
		List<SPTriple> triples = new LinkedList<SPTriple>();
		if (triple != null ){
		
			SPNode singletonNode = null;
			StringBuilder singletonBdr = null;
			
			singletonBdr = new StringBuilder();
			singletonBdr.append(triple.getSubject().toString());
			singletonBdr.append(this.getSPDelimiter());
			singletonBdr.append(this.getNextUUID());
			
			singletonNode = new SPNode(NodeFactory.createURI(singletonBdr.toString()), true);
			
			SPTriple singletonTriple = new SPTriple(new SPNode(triple.getSubject()), singletonNode, new SPNode(triple.getObject()));
			singletonTriple.addSingletonInstanceTriple(new SPTriple(singletonNode, this.singletonPropertyOf, new SPNode(triple.getPredicate())));
			if (this.getMetaObject() != null && this.getMetaPredicate() != null) 
				singletonTriple.addMetaTriple(new SPTriple(singletonNode, this.metaPredicate, this.metaObject));
				
		}
		return triples;
	}

}