package org.knoesis.semanticweb.rdf.jena.sp.converter;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.log4j.Logger;
import org.knoesis.semanticweb.rdf.utils.Constants;
import org.knoesis.semanticweb.rdf.utils.RDFWriteUtils;

public class NamedGraph2SP extends ContextualRepresentationConverter{

	final static Logger logger = Logger.getLogger(NamedGraph2SP.class);

	public NamedGraph2SP(long spPrefixNum, String spPrefixStr,
			String spDelimiter, String singletonPropertyOfURI) {
		super(spPrefixNum, spPrefixStr, spDelimiter, singletonPropertyOfURI);
		this.namedGraphProp = NodeFactory.createURI(Constants.WAS_DERIVED_FROM);
		// TODO Auto-generated constructor stub
	}
	protected Node namedGraphProp = null;

	public NamedGraph2SP(){
		super();
		this.namedGraphProp = NodeFactory.createURI(Constants.WAS_DERIVED_FROM);
	}
	
	@Override
	public String transform(Node[] nodes, String ext){
		
		if (nodes == null){
			return "";
		}
		if (nodes.length == 4 ){

			StringBuilder out = null, singletonBdr = null;
			Node singletonNode = null;
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
				out.append(RDFWriteUtils.Triple2NT(singletonNode, this.namedGraphProp, nodes[3]));
				return out.toString();
				
			/* NANO TO TURTLE */
	
			case Constants.TURTLE_EXT:
				singletonBdr = new StringBuilder(nodes[1].toString());
				singletonBdr.append(this.getSPDelimiter());
				singletonBdr.append(this.getNextUUID());

				singletonNode = NodeFactory.createURI(singletonBdr.toString());
				out = new StringBuilder();
				out.append(RDFWriteUtils.Triple2N3(nodes[0], singletonNode, nodes[2]));
				out.append(RDFWriteUtils.TwoTriples2N3(singletonNode, this.singletonPropertyOf, nodes[1], singletonNode, this.namedGraphProp, nodes[3]));
				return out.toString();
				default:
				break;
			} 
		}
		
		if (nodes.length == 3 ){
			
			StringBuilder out = null;
			switch (ext){

			/* TRIPLE TO NTRIPLE */
			
			case Constants.NTRIPLE_EXT:

				out = new StringBuilder();
				out.append(RDFWriteUtils.Triple2NT(nodes[0], nodes[1], nodes[2]));
				
				return out.toString();

			/* TRIPLE TO TURTLE */
	
			case Constants.TURTLE_EXT:
				
				out = new StringBuilder();
				out.append(RDFWriteUtils.Triple2N3(nodes[0], nodes[1], nodes[2]));

				return out.toString();
				
			default:
				break;
			} 
		}
 		return "";
	}

	public Node getNamedGraphProp() {
		return namedGraphProp;
	}

	public void setNamedGraphProp(Node namedGraphProp) {
		this.namedGraphProp = namedGraphProp;
	}
	public void setNamedGraphProp(String prop){
		this.namedGraphProp = NodeFactory.createURI(prop);
	}
		
 
}
