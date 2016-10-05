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
	protected static SPNode metaPredicate = null;
	protected static SPNode metaObject = null;
	
	public SPNode getMetaPredicate() {
		return metaPredicate;
	}

	public void setMetaPredicate(SPNode _metaPredicate) {
		metaPredicate = _metaPredicate;
	}

	public void setMetaPredicate(String _metaPredicate) {
		SPNode sPNode = new SPNode(NodeFactory.createURI(_metaPredicate));
		if (sPNode != null){
			metaPredicate = sPNode;
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

	public void setMetaObject(SPNode _metaObject) {
		metaObject = _metaObject;
	}
	public void setMetaObject(String _metaObject) {
		SPNode sPNode = new SPNode(NodeFactory.createURI(_metaObject));
		if (sPNode != null){
			metaObject = sPNode;
		}
	}

	public static List<SPTriple> transformTriple(BufferedWriter writer, org.apache.jena.graph.Triple triple, String ext){
		
		List<SPTriple> triples = new LinkedList<SPTriple>();
		if (triple != null ){
		
			SPNode singletonNode = null;
			StringBuilder singletonBdr = null;
			
			singletonBdr = new StringBuilder();
			singletonBdr.append(triple.getSubject().toString());
			singletonBdr.append(spDelimiter);
			singletonBdr.append(getNextUUID());
			
			singletonNode = new SPNode(NodeFactory.createURI(singletonBdr.toString()), true);
			
			SPTriple singletonTriple = new SPTriple(new SPNode(triple.getSubject()), singletonNode, new SPNode(triple.getObject()));
			singletonTriple.addSingletonInstanceTriple(new SPTriple(singletonNode, singletonPropertyOf, new SPNode(triple.getPredicate())));
			if (metaObject != null && metaPredicate != null) 
				singletonTriple.addMetaTriple(new SPTriple(singletonNode, metaPredicate, metaObject));
				
		}
		return triples;
	}

}
