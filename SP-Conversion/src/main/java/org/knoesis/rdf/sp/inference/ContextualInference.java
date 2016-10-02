package org.knoesis.rdf.sp.inference;

import java.util.List;

import org.knoesis.rdf.sp.model.SPModel;
import org.knoesis.rdf.sp.model.SPTriple;

public class ContextualInference {
	
	protected SPModel model = null;
	
	public ContextualInference(){
		model = new SPModel();
	}

	public List<SPTriple> infer(List<SPTriple> triples){
		return triples;
	}
	
	public void init(){
		
	}
	
	public void loadModel(String ontoDir){
		
	}
}
