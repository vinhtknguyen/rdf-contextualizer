package org.knoesis.rdf.sp.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RULE_SP;

import com.romix.scala.collection.concurrent.TrieMap;

public class SPModel {
	
	// Store the hashmap of a property to its super properties obtained from the ontology
	public static Map<String, List<String>> subPropertyOfMap = new TrieMap<String,List<String>>();
	// Store the hashmap of a property to its equivalent properties obtained from the ontology mapping
	public static Map<String, List<String>> equivalentPropertyMap = new TrieMap<String,List<String>>();
	// Store the hashmap of a generic property to its number of singleton properties obtained from the data
	public static Map<String, Integer> genericPropertyMap = new TrieMap<String, Integer>();
	// Store the hashmap of a domain to its number of singleton properties obtained from the data
	public static Map<String, List<String>> domainPropertyMap = new TrieMap<String, List<String>>();
	// Store the hashmap of a domain to its number of singleton properties obtained from the data
	public static Map<String, List<String>> rangePropertyMap = new TrieMap<String, List<String>>();
	
	SPNode rdfType = new SPNode(Constants.RDF_TYPE);
	SPNode singletonPropertyOf = new SPNode(Constants.SINGLETON_PROPERTY_OF);
	SPNode singletonPropertyClass = new SPNode(Constants.RDF_SINGLETON_PROPERTY_CLASS);
	SPNode genericPropertyClass = new SPNode(Constants.RDF_GENERIC_PROPERTY_CLASS);
	SPNode domainProperty = new SPNode(Constants.RDFS_DOMAIN_PROPERTY);
	SPNode rangeProperty = new SPNode(Constants.RDFS_RANGE_PROPERTY);
	SPNode subPropertyOf = new SPNode(Constants.RDFS_SUBPROPERTYOF_PROPERTY);
	SPNode equivalentProperty = new SPNode(Constants.OWL_EQUIVALENTPROPERTY_PROPERTY);
	
	public static int addSubPropertyOfMap(String prop1, String prop2){
		List<String> props = subPropertyOfMap.get(prop1);
		if (props == null){
			props = new ArrayList<String>();
//			System.out.println(prop1.toString() + "\t subPropOf \t" + prop2.toString());
		}
//		System.out.println(props.size());
		if (!props.contains(prop2)) {
			props.add(prop2);
			subPropertyOfMap.put(prop1, props);
			return 1;
		} else return 0;
	}
	
	public static void addDomainPropertyMap(String prop1, String prop2){
		List<String> props = domainPropertyMap.get(prop1);
		if (props == null){
			props = new ArrayList<String>();
//			System.out.println(prop1.toString() + "\t domain \t" + prop2.toString());
		}
		if (!props.contains(prop2)) {
			props.add(prop2.toString());
			domainPropertyMap.put(prop1.toString(), props);
		}
	}

	public static void addRangePropertyMap(String prop1, String prop2){
		List<String> props = rangePropertyMap.get(prop1);
		if (props == null){
			props = new ArrayList<String>();
//			System.out.println(prop1.toString() + "\t range \t" + prop2.toString());
		}
		if (!props.contains(prop2)) {
			props.add(prop2);
			rangePropertyMap.put(prop1, props);
		}
	}

	public static int addEquivalentPropertyMap(String prop1, String prop2){
		List<String> props = equivalentPropertyMap.get(prop1);
		if (props == null){
			props = new ArrayList<String>();
//			System.out.println(prop1.toString() + "\t equi \t" + prop2.toString());
		}
		if (!props.contains(prop2)) {
			props.add(prop2);
			equivalentPropertyMap.put(prop1, props);
			return 1;
		} else return 0;
	}
	
	public static void addGenericPropertyMap(String prop1){
		int count;
		if (genericPropertyMap.get(prop1) != null){
			count = genericPropertyMap.get(prop1) + 1;
		} else {
			count = 1;
//			System.out.println(prop1.toString() + "\t generic ");
		}
		genericPropertyMap.put(prop1, count);
	}

	public List<SPTriple> infer(SPTriple triple, RULE_SP[] rules){
		List<SPTriple> inferred = new ArrayList<SPTriple>();
		
		return inferred;
	}
	
	public List<SPTriple> infer(List<SPTriple> triples, RULE_SP[] rules){
		List<SPTriple> inferred = new ArrayList<SPTriple>();
		for (RULE_SP rule : rules){
			inferred.addAll(infer(triples, rule));
		}
		
		// Extract the triples
		
		return inferred;
	}
	
	public List<SPTriple> expandInferredTriples(List<SPTriple> in){
		List<SPTriple> inferred = new ArrayList<SPTriple>();
		
		for (SPTriple triple : in){
			// Singleton instance
			inferred.addAll(triple.getSingletonInstanceTriples());
			// Meta property triple
			inferred.addAll(triple.getMetaTriples());
			// Inferred generic property triple
			inferred.addAll(triple.getGenericPropertyTriples());
		}
		return inferred;
	}
	
	public List<SPTriple> generateGenericPropertyTriples(){
		List<SPTriple> inferred = new ArrayList<SPTriple>();
		Iterator<Entry<String, Integer>> it = genericPropertyMap.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry<String,Integer> pair = (Map.Entry<String,Integer>)it.next();
		    inferred.add(new SPTriple(new SPNode(pair.getKey()), this.rdfType, this.genericPropertyClass));
		}
		return inferred;
	}
	
	public void clearGenericPropertyMap(){
		genericPropertyMap = new ConcurrentHashMap<String, Integer>();
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
		SPTriple out = new SPTriple(triple);
		if (triple.isSingletonTriple()){
			
			switch (rule){
			case RULE_RDF_SP_1:

				// Retrieve all the singleton property of from the singletonInstanceMap
				for (SPTriple sing : triple.singletonInstanceTriples){
					// Add the singleton instance triple to the singleton triple
					out.addMetaTriple(sing.getSubject(), this.rdfType, this.singletonPropertyClass);
				}
				break;
			
			case RULE_RDF_SP_2:
				
				// Retrieve all the singleton property of from the singletonInstanceMap
				for (SPTriple sing : triple.singletonInstanceTriples){

					// Add the generic triple to the genericPropertyMap
					addGenericPropertyMap(sing.getObject().toString());
				}
			
				break;
				
			case RULE_RDF_SP_3:
				// Retrieve all the singleton property of from the singletonInstanceMap
				for (SPTriple sing : triple.singletonInstanceTriples){
					// Add the singleton instance triple to the singleton triple
					out.addGenericPropertyTriple(triple.getSubject(), sing.getObject(), triple.getObject());
				}
				
				break;
	
			case RULE_RDFS_SP_1:
				// Retrieve all the singleton property of from the singletonInstanceMap
				for (SPTriple sing : triple.singletonInstanceTriples){
					
					// Check for the domain of the sing.getObject()
					if (domainPropertyMap.get(sing.getObject().toString()) != null){
						List<String> domains = domainPropertyMap.get(sing.getObject().toString());
						for (String domain : domains){
							// Add the singleton instance triple to the singleton triple
							out.addMetaTriple(sing.getSubject(), this.domainProperty, new SPNode(domain));
						}
					}
				}
			
				break;
	
			case RULE_RDFS_SP_2:
				// Retrieve all the singleton property of from the singletonInstanceMap
				for (SPTriple sing : triple.singletonInstanceTriples){
					
					// Check for the domain of the sing.getObject()
					if (rangePropertyMap.get(sing.getObject().toString()) != null){
						List<String> ranges = rangePropertyMap.get(sing.getObject().toString());
						for (String range : ranges){
							// Add the singleton instance triple to the singleton triple
							out.addMetaTriple(sing.getSubject(), this.rangeProperty, new SPNode(range));
						}
					}
				}
				
				break;
	
			case RULE_RDFS_SP_3:
				// Retrieve all the singleton property of from the singletonInstanceMap
				for (SPTriple sing : triple.singletonInstanceTriples){
//					System.out.println("finding super prop of " + sing.getObject().toString() + " in subPropMap of size " + subPropertyOfMap.size());
					// Check for the property hierarchy of the sing.getObject()
					if (subPropertyOfMap.get(sing.getObject().toString()) != null){
//						System.out.println("found super prop of " + sing.getObject().toString());
						List<String> properties = subPropertyOfMap.get(sing.getObject().toString());
						for (String prop : properties){
							// Add the singleton instance triple to the singleton triple
							out.addSingletonInstanceTriple(sing.getSubject(), this.singletonPropertyOf, new SPNode(prop));
						}
					} else {
//						System.out.println("not found super prop of " + sing.getObject().toString());
					}
				}
				
				break;
	
			case RULE_RDFS_SP_4:
				// Retrieve all the singleton property of from the singletonInstanceMap
				for (SPTriple sing : triple.singletonInstanceTriples){
					// Check for the property hierarchy of the sing.getObject()
					if (subPropertyOfMap.get(sing.getObject().toString()) != null){
						List<String> properties = subPropertyOfMap.get(sing.getObject().toString());
						for (String prop : properties){
							// Add the generic property to the list
							genericPropertyMap.put(prop.toString(), 1);
						}
					}
				}
				
				break;
		
			case RULE_OWL_SP_1:
				// Retrieve all the singleton property of from the singletonInstanceMap
				for (SPTriple sing : triple.singletonInstanceTriples){
					
					// Check for the domain of the sing.getObject()
					if (equivalentPropertyMap.get(sing.getObject().toString()) != null){
						List<String> properties = equivalentPropertyMap.get(sing.getObject().toString());
						for (String prop : properties){
							// Add the singleton instance triple to the singleton triple
							out.addSingletonInstanceTriple(sing.getSubject(), this.singletonPropertyOf, new SPNode(prop));
						}
					}
				}
				
				break;
	
			case RULE_OWL_SP_2:
				// Retrieve all the singleton property of from the singletonInstanceMap
				for (SPTriple sing : triple.singletonInstanceTriples){
					
					// Check for the domain of the sing.getObject()
					if (equivalentPropertyMap.get(sing.getObject().toString()) != null){
						List<String> properties = equivalentPropertyMap.get(sing.getObject().toString());
						for (String prop : properties){
							// Add the singleton instance triple to the singleton triple
							out.addSingletonInstanceTriple(sing.getSubject(), this.singletonPropertyOf, new SPNode(prop));
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
}
