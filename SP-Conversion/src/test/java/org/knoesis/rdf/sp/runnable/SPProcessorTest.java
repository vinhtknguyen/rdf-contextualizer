package org.knoesis.rdf.sp.runnable;


import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Quad;
import org.junit.Before;
import org.junit.Test;

public class SPProcessorTest {

	SPProcessor quadProcessor;
	SPProcessor tripleProcessor;

	@Before
	public void setUp() throws Exception {
		quadProcessor = new SPProcessor("ng");
		quadProcessor.setIsinfer(true);
		quadProcessor.setOntoDir("src/main/resources/onto");
		quadProcessor.setDsName("ds1");
		quadProcessor.setExt("ttl");
		
		quadProcessor.start();

		tripleProcessor = new SPProcessor("triple");
		tripleProcessor.setIsinfer(true);
		tripleProcessor.setOntoDir("src/main/resources/onto");
		tripleProcessor.setDsName("ds1");
		tripleProcessor.setExt("ttl");
		tripleProcessor.setShortenURI(true);
		
		quadProcessor.start();

	}

	@Test
	public void testProcessQuad() {
		String out = quadProcessor.process(new Quad(NodeFactory.createURI("http://example.com/s1"), 
				NodeFactory.createURI("http://example.com/p1"), 
				NodeFactory.createURI("http://example.com/o1"), 
				NodeFactory.createURI("http://example.com/g1")));
		System.out.println("Quad processing output ======= \n " + out);
	}

	@Test
	public void testProcessTriple() {
		String out = tripleProcessor.process(new Triple(NodeFactory.createURI("http://example.com/s1"), 
				NodeFactory.createURI("http://example.com/p1"), 
				NodeFactory.createURI("http://example.com/o1")));
		System.out.println("Triple processing output ======= \n " + out);
	}

}
