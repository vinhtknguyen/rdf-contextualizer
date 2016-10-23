package org.knoesis.rdf.sp.supplier;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.Quad;
import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.model.SPTriple;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.ConstantsTest;

public class SupplierTransformerTest {
	SPProcessor quadProcessor;
	SupplierTransformer transformer = new SupplierTransformer();
	@Before
	public void setUp() throws Exception {
		quadProcessor = new SPProcessor(Constants.NG_REP);
		quadProcessor.setIsinfer(true);
		quadProcessor.setOntoDir(ConstantsTest.test_data_onto);
		quadProcessor.setDsName("ds1");
		quadProcessor.setExt("nt");
		quadProcessor.setShortenURI(false);
		quadProcessor.setPrefix(ConstantsTest.test_data_prefix + "/bio2rdf_prefixes.ttl");
		
		
		quadProcessor.start();
	}

	@Test
	public void testPrintSPTriple2N3() {
		SPTriple triple = quadProcessor.process(new Quad(NodeFactory.createURI("http://example.com/s1"), 
				NodeFactory.createURI("http://xmlns.com/foaf/0.1/gender"), 
				NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), 
				NodeFactory.createURI("http://example.com/g1")));
		System.out.println(triple.printAll());
//		System.out.println(transformer.printSPTriple2N3(triple));
	}
	
	@Test
	public void testToN3(){
//		System.out.println(transformer.toN3(new SPNode("http://example.com/p1")));
	}

}
