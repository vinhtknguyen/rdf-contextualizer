package org.knoesis.rdf.sp.model;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.knoesis.rdf.sp.utils.Constants;

import com.romix.scala.collection.concurrent.TrieMap;


/**
 * This class represents the model with property resources for contextual inference
 * 
 * */
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
	
	public static SPNode rdfType = new SPNode(Constants.RDF_TYPE);
	public static SPNode singletonPropertyOf = new SPNode(Constants.SINGLETON_PROPERTY_OF);
	public static SPNode singletonPropertyClass = new SPNode(Constants.RDF_SINGLETON_PROPERTY_CLASS);
	public static SPNode genericPropertyClass = new SPNode(Constants.RDF_GENERIC_PROPERTY_CLASS);
	public static SPNode domainProperty = new SPNode(Constants.RDFS_DOMAIN_PROPERTY);
	public static SPNode rangeProperty = new SPNode(Constants.RDFS_RANGE_PROPERTY);
	public static SPNode subPropertyOf = new SPNode(Constants.RDFS_SUBPROPERTYOF_PROPERTY);
	public static SPNode equivalentProperty = new SPNode(Constants.OWL_EQUIVALENTPROPERTY_PROPERTY);
	
	public static int addSubPropertyOfMap(String prop1, String prop2){
		List<String> props = subPropertyOfMap.get(prop1);
		if (props == null){
			props = new ArrayList<String>();
		}
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
	
	public static void addGenericPropertyMap(Map<String,Integer> mapPerFile){
		Iterator<Entry<String, Integer>> it = mapPerFile.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String,Integer> pair = (Map.Entry<String,Integer>)it.next();
	        if (genericPropertyMap.containsKey(pair.getKey())){
	        	int count = genericPropertyMap.get(pair.getKey());
	        	genericPropertyMap.put(pair.getKey(), count + pair.getValue());
	        }
	    }
	}

	
	public void clearGenericPropertyMap(){
		genericPropertyMap = new ConcurrentHashMap<String, Integer>();
	}
	
	
	public static void loadModel(String file) {
		// Load ontologies from ontoDir to the model
		// If the input is a file
		Model onto = ModelFactory.createDefaultModel();

		if (!Files.isDirectory(Paths.get(file))) {
			// load file
//			 System.out.println(file);
			 RDFDataMgr.read(onto, file);
		} else {
			// If the input is a directory
			// Create a new directory for output files
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths
					.get(file))) {
				/* PROCESS EACH INPUT FILE & GENERATE OUTPUT FILE */
				for (Path entry : stream) {
//					 System.out.println("file: " + entry.toString());
					 RDFDataMgr.read(onto, entry.toString());
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		// Process the model to construct the SPModel
		StmtIterator iter = onto.listStatements();
		List<String> subProps = new ArrayList<String>();
		List<String> equiProps = new ArrayList<String>();
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement(); // get next statement
			Resource subject = stmt.getSubject(); // get the subject
			Property predicate = stmt.getPredicate(); // get the predicate
			RDFNode object = stmt.getObject(); // get the object

			// Check for subPropertyOf
			switch (predicate.toString()) {
			case Constants.RDFS_SUBPROPERTYOF_PROPERTY:
				SPModel.addSubPropertyOfMap(subject.toString(), object.toString());
				if (!subProps.contains(subject.toString()))subProps.add(subject.toString());
				if (!subProps.contains(object.toString()))subProps.add(object.toString());
				break;
			case Constants.OWL_EQUIVALENTPROPERTY_PROPERTY:
				SPModel.addEquivalentPropertyMap(subject.toString(), object.toString());
				if (!equiProps.contains(subject.toString()))subProps.add(subject.toString());
				if (!equiProps.contains(object.toString()))subProps.add(object.toString());
				break;
			case Constants.RDFS_DOMAIN_PROPERTY:
				SPModel.addDomainPropertyMap(subject.toString(), object.toString());
				break;
			case Constants.RDFS_RANGE_PROPERTY:
				SPModel.addRangePropertyMap(subject.toString(), object.toString());
				break;
			default:
				break;
			}
		}
//		System.out.println("compute transitive for subprop");
		// Compute transitive
		computeTransitive(subProps, true);
//		System.out.println("compute transitive for equiprop");
		computeTransitive(equiProps, false);
		// Printing statistics
	}
	
	public static void computeTransitive(List<String> props, boolean isSubProp){
		List<String> lst = new ArrayList<String>();
		lst.addAll(props);
		int count = 1;
		while (count > 0) {
			count = 0;
			for (String prop : lst) {
//				System.out.println("outer for: " + prop);
				// Compute the transitive
				List<String> supers = new ArrayList<String>();
				if (isSubProp){
					if (SPModel.subPropertyOfMap.get(prop) != null) supers.addAll(SPModel.subPropertyOfMap.get(prop));
				} else {
					if (SPModel.equivalentPropertyMap.get(prop) != null) supers.addAll(SPModel.equivalentPropertyMap.get(prop));
				}
					// Get all the super properties
				if (supers != null){
					for (String newProp : supers) {
						List<String> newsupers = new ArrayList<String>();
						if (isSubProp){
							if (SPModel.subPropertyOfMap.get(newProp) != null) newsupers.addAll(SPModel.subPropertyOfMap.get(newProp));
						} else {
							if (SPModel.equivalentPropertyMap.get(newProp) != null) newsupers.addAll(SPModel.equivalentPropertyMap.get(newProp));
						}
//						System.out.println("1st inner for: " + newProp);
						if (newsupers != null){
							for (String newsuperprop: newsupers){
								if (!newsuperprop.equals(prop)) {
									int j = 0;
									if (isSubProp){
										j = SPModel.addSubPropertyOfMap(prop, newsuperprop);
									} else {
										j = SPModel.addEquivalentPropertyMap(prop, newsuperprop);
			
									}
									if (j > 0){
										count++;
//										System.out.println("insert : " + prop + " subProp " +  newsuperprop + " with j: " + j);
									}
//									System.out.println("count: " + count);
			
									if (!lst.contains(newsuperprop)) lst.add(newsuperprop);
								}
								
							}
						}
					}
				}
			}
			if (count == 0) break;
		}

	}

}
