package org.knoesis.semanticweb.rdf.jena.sp.converter;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.log4j.Logger;
import org.knoesis.semanticweb.rdf.utils.Constants;
import org.knoesis.semanticweb.rdf.utils.RDFWriteUtils;

public class Reification2SP extends ContextualRepresentationConverter{
	
	final static Logger logger = Logger.getLogger(Reification2SP.class);

	private Map<String,Node[]> reifiedTriples = new HashMap<String,Node[]>();
	
	private boolean type_flag = false;
	private boolean subject_flag = false;
	private boolean predicate_flag = false;
	private boolean object_flag = false;
	
	public Reification2SP() {
		super();
		
	}


	public Reification2SP(long spPrefixNum, String spPrefixStr,
			String spDelimiter, String singletonPropertyOfURI) {
		super(spPrefixNum, spPrefixStr, spDelimiter, singletonPropertyOfURI);
		// TODO Auto-generated constructor stub
	}

	private boolean isReifiedStatementCompleted(){
		return (type_flag && subject_flag && predicate_flag && object_flag);
	}
	
	private boolean addTriple(Node[] nodes){
		if (nodes.length == 3){
			if (nodes[1].toString().equals(Constants.RDF_TYPE) && nodes[2].toString().equals(Constants.RDF_STATEMENT)){
				if (nodes[0] != null){
					Node[] rei = new Node[3];
					reifiedTriples.put(nodes[0].toString(), rei);
					type_flag = true;
					return true;
				}
			}
			
			if (nodes[1].toString().equals(Constants.RDF_SUBJECT)){
				if (reifiedTriples.get(nodes[0].toString()) != null && nodes[2] != null){
					reifiedTriples.get(nodes[0].toString())[0] = nodes[2];
					subject_flag = true;
					return true;
				}
			}
			
			if (nodes[1].toString().equals(Constants.RDF_PREDICATE)){
				if (reifiedTriples.get(nodes[0].toString()) != null && nodes[2] != null){
					reifiedTriples.get(nodes[0].toString())[1] = nodes[2];
					predicate_flag = true;
					return true;
				}
			}
			
			if (nodes[1].toString().equals(Constants.RDF_OBJECT)){
				if (reifiedTriples.get(nodes[0].toString()) != null && nodes[2] != null){
					reifiedTriples.get(nodes[0].toString())[2] = nodes[2];
					object_flag = true;
					return true;
				}
			}
		}
		return false;
	}
	
	private void resetReifiedFlags(){
		this.type_flag = false;
		this.subject_flag = false;
		this.predicate_flag = false;
		this.object_flag = false;
	}
	
	@Override
	public String transform(Node[] nodes, String ext){
		
		if (nodes.length == 3 ){
			
			// Trying to add the current triple to the Reified Statement
			if (!addTriple(nodes)) {
				
				// Print the regular triple
				return super.transform(nodes, ext);
			}
			
			// Check if the reified statement is completed
			
			if (!isReifiedStatementCompleted()){
				return "";
			}
			
			Node[] reifiedStatement = reifiedTriples.get(nodes[0].toString());
			
			
			StringBuilder out = new StringBuilder();
			
			switch (ext){

			/* NANO TO NTRIPLE */
			
			case Constants.NTRIPLE_EXT:
				
				out.append(RDFWriteUtils.Triple2NT(reifiedStatement[0], nodes[0], reifiedStatement[2]));
				out.append(RDFWriteUtils.Triple2NT(nodes[0], this.singletonPropertyOf, reifiedStatement[1]));
				
				break;
			/* NANO TO TURTLE */
	
			case Constants.TURTLE_EXT:
				
				out.append(RDFWriteUtils.Triple2N3(reifiedStatement[0], nodes[0], reifiedStatement[2]));
				out.append(RDFWriteUtils.Triple2N3(nodes[0], this.singletonPropertyOf, reifiedStatement[1]));

			default:
				break;
			} 
			
			// Reset the flasg and remove the reified statement
			resetReifiedFlags();
			reifiedTriples.remove(nodes[0]);

			return out.toString();
		}
		return "";
	}

}
