package org.knoesis.semanticweb.rdf.jena.sp.converter;

import org.apache.jena.graph.Node;
import org.apache.log4j.Logger;
import org.knoesis.semanticweb.rdf.utils.Constants;
import org.knoesis.semanticweb.rdf.utils.RDFWriteUtils;

public class NanoPub2SP extends ContextualRepresentationConverter {
	
	final static Logger logger = Logger.getLogger(NanoPub2SP.class);


	@Override
	public String transform(Node[] nodes, String ext){
		
		if (nodes.length == 4 ){
		
			Node singletonNode = null;
			StringBuilder out = null;
			
			switch (ext){

			/* NANO TO NTRIPLE */
			
			case Constants.NTRIPLE_EXT:
				
				singletonNode = nodes[3];
				
				out = new StringBuilder();
				out.append(RDFWriteUtils.Triple2NT(nodes[0], singletonNode, nodes[2]));
				out.append(RDFWriteUtils.Triple2NT(singletonNode, this.singletonPropertyOf, nodes[1]));
				return out.toString();
				
			/* NANO TO TURTLE */
	
			case Constants.TURTLE_EXT:
				
				singletonNode = nodes[3];

				out = new StringBuilder();
				out.append(RDFWriteUtils.Triple2N3(nodes[0], singletonNode, nodes[2]));
				out.append(RDFWriteUtils.Triple2N3(singletonNode, this.singletonPropertyOf, nodes[1]));
				return out.toString();

			default:
				break;
			} 
		}
		return "";
	}


	public NanoPub2SP() {
		super();
		// TODO Auto-generated constructor stub
	}


	public NanoPub2SP(long spPrefixNum, String spPrefixStr, String spDelimiter,
			String singletonPropertyOfURI) {
		super(spPrefixNum, spPrefixStr, spDelimiter, singletonPropertyOfURI);
		// TODO Auto-generated constructor stub
	}
	

}

