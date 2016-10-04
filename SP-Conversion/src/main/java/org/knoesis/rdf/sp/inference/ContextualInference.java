package org.knoesis.rdf.sp.inference;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.knoesis.rdf.sp.model.SPModel;
import org.knoesis.rdf.sp.model.SPTriple;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RULE_SP;

public class ContextualInference {

	protected SPModel model = null;

	public ContextualInference() {
		model = new SPModel();
	}

	public List<SPTriple> infer(List<SPTriple> triples) {
		List<SPTriple> out = new ArrayList<SPTriple>();
		out.addAll(model.infer(triples, RULE_SP.RULE_ALL_SP));
		return out;
	}

	public List<SPTriple> expandInferredTriples(List<SPTriple> triples) {
		return SPModel.expandInferredTriples(triples);
	}

	public List<SPTriple> generateGenericPropertyTriples() {
		return model.generateGenericPropertyTriples();
	}

	public void init() {

	}

	public void clearGenericPropMap() {
		model.clearGenericPropertyMap();
	}

	public void loadModel(String file) {
		// Load ontologies from ontoDir to the model
		// If the input is a file
		Model onto = ModelFactory.createDefaultModel();

		if (!Files.isDirectory(Paths.get(file))) {
			// load file
			 System.out.println(file);
			onto.read(file);
		} else {
			// If the input is a directory
			// Create a new directory for output files
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths
					.get(file))) {
				/* PROCESS EACH INPUT FILE & GENERATE OUTPUT FILE */
				for (Path entry : stream) {
					 System.out.println("file: " + entry.toString());
					onto.read(entry.toString());
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
		System.out.println("compute transitive for subprop");
		// Compute transitive
		computeTransitive(subProps, true);
		System.out.println("compute transitive for equiprop");
		computeTransitive(equiProps, false);
		// Printing statistics
	}
	
	public void computeTransitive(List<String> props, boolean isSubProp){
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
										System.out.println("insert : " + prop + " subProp " +  newsuperprop + " with j: " + j);
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
