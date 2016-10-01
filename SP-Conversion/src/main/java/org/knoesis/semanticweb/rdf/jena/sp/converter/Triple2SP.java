package org.knoesis.semanticweb.rdf.jena.sp.converter;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.log4j.Logger;
import org.knoesis.semanticweb.rdf.utils.Constants;
import org.knoesis.semanticweb.rdf.utils.RDFWriteUtils;

public class Triple2SP extends ContextualRepresentationConverter {
	
	final static Logger logger = Logger.getLogger(NamedGraph2SP.class);
	protected Node metaPredicate = null;
	protected Node metaObject = null;
	
	public Node getMetaPredicate() {
		return metaPredicate;
	}

	public void setMetaPredicate(Node metaPredicate) {
		this.metaPredicate = metaPredicate;
	}

	public void setMetaPredicate(String metaPredicate) {
		Node node = NodeFactory.createURI(metaPredicate);
		if (node != null){
			this.metaPredicate = node;
		}
	}

	public Node getMetaObject() {
		return metaObject;
	}

	public Triple2SP() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Triple2SP(long spPrefixNum, String spPrefixStr, String spDelimiter,
			String singletonPropertyOfURI) {
		super(spPrefixNum, spPrefixStr, spDelimiter, singletonPropertyOfURI);
		// TODO Auto-generated constructor stub
	}

	public void setMetaObject(Node metaObject) {
		this.metaObject = metaObject;
	}
	public void setMetaObject(String metaObject) {
		Node node = NodeFactory.createURI(metaObject);
		if (node != null){
			this.metaObject = node;
		}
	}

	@Override
	public String transform(Node[] nodes, String ext){
		
		if (nodes.length == 3 ){
		
			Node singletonNode = null;
			StringBuilder singletonBdr = null, out = null;
			
			switch (ext){

			/* NANO TO NTRIPLE */
			
			case Constants.NTRIPLE_EXT:
				
				singletonBdr = new StringBuilder();
				singletonBdr.append(nodes[1].toString());
				singletonBdr.append(this.getSPDelimiter());
				singletonBdr.append(this.getNextUUID());
				
				singletonNode = NodeFactory.createURI(singletonBdr.toString());
				
				out = new StringBuilder();
				out.append(RDFWriteUtils.Triple2NT(nodes[0], singletonNode, nodes[2]));
				out.append(RDFWriteUtils.Triple2NT(singletonNode, this.singletonPropertyOf, nodes[1]));
				if (this.getMetaObject() != null && this.getMetaPredicate() != null) 
					out.append(RDFWriteUtils.Triple2NT(singletonNode, this.metaPredicate, this.metaObject));
				return out.toString();
				
			/* NANO TO TURTLE */
	
			case Constants.TURTLE_EXT:
				
				singletonBdr = new StringBuilder(nodes[1].toString());
				singletonBdr.append(this.getSPDelimiter());
				singletonBdr.append(this.getNextUUID());

				singletonNode = NodeFactory.createURI(singletonBdr.toString());

				out = new StringBuilder();
				out.append(RDFWriteUtils.Triple2N3(nodes[0], singletonNode, nodes[2]));
				out.append(RDFWriteUtils.Triple2N3(singletonNode, this.singletonPropertyOf, nodes[1]));
				if (this.getMetaObject() != null && this.getMetaPredicate() != null) {
					out.append(RDFWriteUtils.TwoTriples2N3(singletonNode, this.singletonPropertyOf, nodes[1], singletonNode, this.metaPredicate, this.metaObject));
				} else {
					out.append(RDFWriteUtils.Triple2N3(singletonNode, this.singletonPropertyOf, nodes[1]));
				}
				return out.toString();

			default:
				break;
			} 
		}
		return "";
	}

}
