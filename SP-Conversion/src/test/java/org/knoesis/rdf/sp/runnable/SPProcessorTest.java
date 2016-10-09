package org.knoesis.rdf.sp.runnable;


import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Quad;
import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.callable.SPProcessor;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.ConstantsTest;

public class SPProcessorTest {

	SPProcessor quadProcessor;
	SPProcessor tripleProcessor;

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

		tripleProcessor = new SPProcessor(Constants.TRIPLE_REP);
		tripleProcessor.setIsinfer(true);
		tripleProcessor.setOntoDir(ConstantsTest.test_data_onto);
		tripleProcessor.setDsName("ds1");
		tripleProcessor.setExt("nt");
		tripleProcessor.setShortenURI(true);
		
		tripleProcessor.start();
	}

	@Test
	public void testProcessQuad() {
		String triple = quadProcessor.processQuad(new Quad(NodeFactory.createURI("http://example.com/s1"), 
				NodeFactory.createURI("http://example.com/p1"), 
				NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#singletonPropertyOf"), 
				NodeFactory.createURI("http://example.com/g1")));
		System.out.println("Quad processing output ======= \n " + triple);
	}

	@Test
	public void testProcessTriple() {
		String triple = tripleProcessor.processTriple(new Triple(NodeFactory.createURI("http://example.com/s1"), 
				NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), 
				NodeFactory.createURI("http://example.com/o1")));
		System.out.println("Triple processing output ======= \n " + triple);
	}

}
