package org.knoesis.rdf.sp.inference;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.converter.NamedGraph2SP;

public class ContextualInferenceTest {

	ContextualInference inference;
	NamedGraph2SP con;
	@Before
	public void setUp() throws Exception {
		con = new NamedGraph2SP();
		con.setOntoDir("src/main/resources/onto");
		con.setInfer(true);
	}

	@Test
	public void testInfer() {
		con.convert("src/test/resources/test-onto", "ttl", "ng");
	}

}
