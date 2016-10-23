package org.knoesis.rdf.sp.inference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.knoesis.rdf.sp.model.SPModel;
import org.knoesis.rdf.sp.model.SPNode;
import org.knoesis.rdf.sp.model.SPTriple;

import com.romix.scala.collection.concurrent.TrieMap;

public class ContextualInference {

	// Store the hashmap of a generic property to its number of singleton properties obtained from the data
	protected Map<String, Integer> genericPropertyMapPerFile = new TrieMap<String, Integer>();
	RULE_SP[] rules = RULE_SP.values();

	public ContextualInference() {
		Comparator<RULE_SP> comparator1 = new Comparator<RULE_SP>() {
			  public int compare(RULE_SP e1, RULE_SP e2) {
			     //your magic happens here
				  return e1.getNum() - e2.getNum();
			  }
		};
		
		Arrays.sort(rules, comparator1);
	}
	
	public void addGenericPropertyMap(String prop1){
		int count;
		if (genericPropertyMapPerFile.containsKey(prop1)){
			count = genericPropertyMapPerFile.get(prop1) + 1;
		} else {
			count = 1;
//			System.out.println(prop1.toString() + "\t generic ");
		}
		genericPropertyMapPerFile.put(prop1, count);
	}

	public List<SPTriple> generateGenericPropertyTriplesPerFile(){
		List<SPTriple> inferred = new ArrayList<SPTriple>();
		Iterator<Entry<String, Integer>> it = genericPropertyMapPerFile.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry<String,Integer> pair = (Map.Entry<String,Integer>)it.next();
		    inferred.add(new SPTriple(new SPNode(pair.getKey()), SPModel.rdfType, SPModel.genericPropertyClass));
		}
		return inferred;
	}

	public List<SPTriple> infer(List<SPTriple> triples) {
		List<SPTriple> out = new ArrayList<SPTriple>();
		out.addAll(infer(triples, RULE_SP.RULE_ALL_SP));
		
//		SPModel.addGenericPropertyMap(this.genericPropertyMapPerFile);
		
		return out;
	}

	public SPTriple infer(SPTriple triple){
		
		for (RULE_SP r: rules ){
//					System.out.println("rules: " + r.toString());
			triple = infer(triple, r);
		}
		// Run the rules RULE_OWL_SP_3 and RULE_RDFS_SP_5
		return triple;
	}
	
	public List<SPTriple> infer(List<SPTriple> triples, RULE_SP rule){
		List<SPTriple> inferred = new ArrayList<SPTriple>();
		
		Comparator<RULE_SP> comparator1 = new Comparator<RULE_SP>() {
			  public int compare(RULE_SP e1, RULE_SP e2) {
			     //your magic happens here
				  return e1.getNum() - e2.getNum();
			  }
		};
		
		RULE_SP[] rules = RULE_SP.values();
		Arrays.sort(rules, comparator1);
		for (SPTriple triple : triples){
			if (rule == RULE_SP.RULE_ALL_SP){
				
				for (RULE_SP r: rules ){
//					System.out.println("rules: " + r.toString());
					triple = infer(triple, r);
				}
			}
			inferred.add(triple);
		}
		// Run the rules RULE_OWL_SP_3 and RULE_RDFS_SP_5
		return inferred;
	}
	
	public SPTriple infer(SPTriple triple, RULE_SP rule){
		if (triple == null) return null;
		SPTriple out = new SPTriple(triple);
		if (triple.isSingletonTriple()){
			
			switch (rule){
			case RULE_RDF_SP_1:

				// Retrieve all the singleton property of from the singletonInstanceMap
				for (SPTriple sing : triple.getSingletonInstanceTriples()){
					// Add the singleton instance triple to the singleton triple
					out.addMetaTriple(sing.getSubject(), SPModel.rdfType, SPModel.singletonPropertyClass);
				}
				break;
			
			case RULE_RDF_SP_2:
				
				// Retrieve all the singleton property of from the singletonInstanceMap
				for (SPTriple sing : triple.getSingletonInstanceTriples()){

					// Add the generic triple to the genericPropertyMap
					addGenericPropertyMap(sing.getObject().toString());
				}
			
				break;
				
			case RULE_RDF_SP_3:
				// Retrieve all the singleton property of from the singletonInstanceMap
				for (SPTriple sing : triple.getSingletonInstanceTriples()){
					// Add the singleton instance triple to the singleton triple
					out.addGenericPropertyTriple(triple.getSubject(), sing.getObject(), triple.getObject());
				}
				
				break;
	
			case RULE_RDFS_SP_1:
				// Retrieve all the singleton property of from the singletonInstanceMap
				for (SPTriple sing : triple.getSingletonInstanceTriples()){
					
					// Check for the domain of the sing.getObject()
					if (SPModel.domainPropertyMap.get(sing.getObject().toString()) != null){
						List<String> domains = SPModel.domainPropertyMap.get(sing.getObject().toString());
						for (String domain : domains){
							// Add the singleton instance triple to the singleton triple
							out.addMetaTriple(sing.getSubject(), SPModel.domainProperty, new SPNode(domain));
						}
					}
				}
			
				break;
	
			case RULE_RDFS_SP_2:
				// Retrieve all the singleton property of from the singletonInstanceMap
				for (SPTriple sing : triple.getSingletonInstanceTriples()){
					
					// Check for the domain of the sing.getObject()
					if (SPModel.rangePropertyMap.get(sing.getObject().toString()) != null){
						List<String> ranges = SPModel.rangePropertyMap.get(sing.getObject().toString());
						for (String range : ranges){
							// Add the singleton instance triple to the singleton triple
							out.addMetaTriple(sing.getSubject(), SPModel.rangeProperty, new SPNode(range));
						}
					}
				}
				
				break;
	
			case RULE_RDFS_SP_3:
				// Retrieve all the singleton property of from the singletonInstanceMap
				for (SPTriple sing : triple.getSingletonInstanceTriples()){
//					System.out.println("finding super prop of " + sing.getObject().toString() + " in subPropMap of size " + subPropertyOfMap.size());
					// Check for the property hierarchy of the sing.getObject()
					if (SPModel.subPropertyOfMap.get(sing.getObject().toString()) != null){
//						System.out.println("found super prop of " + sing.getObject().toString());
						List<String> properties = SPModel.subPropertyOfMap.get(sing.getObject().toString());
						for (String prop : properties){
							// Add the singleton instance triple to the singleton triple
							out.addSingletonInstanceTriple(sing.getSubject(), SPModel.singletonPropertyOf, new SPNode(prop));
						}
					} else {
//						System.out.println("not found super prop of " + sing.getObject().toString());
					}
				}
				
				break;
	
			case RULE_RDFS_SP_4:
				// Retrieve all the singleton property of from the singletonInstanceMap
				for (SPTriple sing : triple.getSingletonInstanceTriples()){
					// Check for the property hierarchy of the sing.getObject()
					if (SPModel.subPropertyOfMap.get(sing.getObject().toString()) != null){
						List<String> properties = SPModel.subPropertyOfMap.get(sing.getObject().toString());
						for (String prop : properties){
							// Add the generic property to the list
							addGenericPropertyMap(prop);
						}
					}
				}
				
				break;
		
			case RULE_OWL_SP_1:
				// Retrieve all the singleton property of from the singletonInstanceMap
				for (SPTriple sing : triple.getSingletonInstanceTriples()){
					
					// Check for the domain of the sing.getObject()
					if (SPModel.equivalentPropertyMap.get(sing.getObject().toString()) != null){
						List<String> properties = SPModel.equivalentPropertyMap.get(sing.getObject().toString());
						for (String prop : properties){
							// Add the singleton instance triple to the singleton triple
							out.addSingletonInstanceTriple(sing.getSubject(), SPModel.singletonPropertyOf, new SPNode(prop));
						}
					}
				}
				
				break;
	
			case RULE_OWL_SP_2:
				// Retrieve all the singleton property of from the singletonInstanceMap
				for (SPTriple sing : triple.getSingletonInstanceTriples()){
					
					// Check for the domain of the sing.getObject()
					if (SPModel.equivalentPropertyMap.get(sing.getObject().toString()) != null){
						List<String> properties = SPModel.equivalentPropertyMap.get(sing.getObject().toString());
						for (String prop : properties){
							// Add the singleton instance triple to the singleton triple
							addGenericPropertyMap(prop);
						}
					}
				}
				
				break;
	
			case RULE_OWL_SP_3:
				// Write this rule separately
				break;
			case RULE_RDFS_SP_5:
				// Write this rule separately
				break;
			case RULE_ALL_SP:
				break;
	
			}
		}
		return out;
	}
	
	public Map<String, Integer> getGenericPropertyMapPerFile() {
		return genericPropertyMapPerFile;
	}

	public void setGenericPropertyMapPerFile(
			Map<String, Integer> genericPropertyMapPerFile) {
		this.genericPropertyMapPerFile = genericPropertyMapPerFile;
	}

	public void clearGenericPropMap() {
		genericPropertyMapPerFile.clear();
	}
}
