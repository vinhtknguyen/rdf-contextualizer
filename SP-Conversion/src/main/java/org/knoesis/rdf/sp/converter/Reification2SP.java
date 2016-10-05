package org.knoesis.rdf.sp.converter;

import java.io.BufferedWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.model.SPNode;
import org.knoesis.rdf.sp.model.SPTriple;
import org.knoesis.rdf.sp.utils.Constants;

import com.romix.scala.collection.concurrent.TrieMap;

public class Reification2SP extends ContextualRepresentationConverter{
	
	final static Logger logger = Logger.getLogger(Reification2SP.class);

	private static Map<String,org.apache.jena.graph.Node[]> reifiedTriples = new TrieMap<String,org.apache.jena.graph.Node[]>();
	
	private static boolean type_flag = false;
	private static boolean subject_flag = false;
	private static boolean predicate_flag = false;
	private static boolean object_flag = false;
	
	public Reification2SP() {
		super();
		
	}

	public Reification2SP(long _uuidInitNum, String _uuidInitStr) {
		super(_uuidInitNum, _uuidInitStr);
	}

	private static boolean isReifiedPatternCompleted(){
		return (type_flag && subject_flag && predicate_flag && object_flag);
	}
	
	private static boolean addTripleToReifiedPattern(org.apache.jena.graph.Triple triple){
		if (triple != null){
			if (triple.getPredicate().toString().equals(Constants.RDF_TYPE) && triple.getObject().toString().equals(Constants.RDF_STATEMENT)){
				if (triple.getSubject() != null){
					org.apache.jena.graph.Node[] rei = new org.apache.jena.graph.Node[3];
					reifiedTriples.put(triple.getSubject().toString(), rei);
					type_flag = true;
					return true;
				}
			}
			
			if (triple.getPredicate().toString().equals(Constants.RDF_SUBJECT)){
				if (reifiedTriples.get(triple.getSubject().toString()) != null && triple.getObject() != null){
					reifiedTriples.get(triple.getSubject().toString())[0] = triple.getObject();
					subject_flag = true;
					return true;
				}
			}
			
			if (triple.getPredicate().toString().equals(Constants.RDF_PREDICATE)){
				if (reifiedTriples.get(triple.getSubject().toString()) != null && triple.getObject() != null){
					reifiedTriples.get(triple.getSubject().toString())[1] = triple.getObject();
					predicate_flag = true;
					return true;
				}
			}
			
			if (triple.getPredicate().toString().equals(Constants.RDF_OBJECT)){
				if (reifiedTriples.get(triple.getSubject().toString()) != null && triple.getObject() != null){
					reifiedTriples.get(triple.getSubject().toString())[2] = triple.getObject();
					object_flag = true;
					return true;
				}
			}
		}
		return false;
	}
	
	private static void resetReifiedFlags(){
		type_flag = false;
		subject_flag = false;
		predicate_flag = false;
		object_flag = false;
	}
	
	public List<SPTriple> transformTriple(BufferedWriter writer, org.apache.jena.graph.Triple triple, String ext){
		List<SPTriple> triples = new LinkedList<SPTriple>();
		
		if (triple != null){
			
			// Trying to add the current triple to the Reified Statement
			if (!addTripleToReifiedPattern(triple)) {
				
				// Print the regular triple
				super.transformTriple(writer, triple, ext);
				return triples;
			}
			
			// Check if the reified statement is completed
			
			if (!isReifiedPatternCompleted()){
				return triples;
			}
			
			org.apache.jena.graph.Node[] reifiedStatement = reifiedTriples.get(triple.getSubject().toString());

			SPNode singletonNode;
			if (triple.getSubject().isBlank()){
				// Constructing a new singleton property
				StringBuilder singletonBdr = null;
				singletonBdr = new StringBuilder();
				singletonBdr.append(reifiedStatement[1].toString());
				singletonBdr.append(uuid.getSPDelimiter());
				singletonBdr.append(uuid.getNextUUID());
				
				singletonNode = new SPNode(singletonBdr.toString(), true);
			} else{
				singletonNode = new SPNode(triple.getSubject(), true);
			}
			SPTriple singletonTriple = new SPTriple(new SPNode(reifiedStatement[0]), singletonNode, new SPNode(reifiedStatement[2]));
			singletonTriple.addSingletonInstanceTriple(new SPTriple(singletonNode, singletonPropertyOf, new SPNode(reifiedStatement[1])));
			
			triples.add(singletonTriple);
			
			// Reset the flasg and remove the reified statement
			resetReifiedFlags();
			reifiedTriples.remove(triple.getSubject());
		}
		return triples;
	}

}
