package org.knoesis.rdf.sp.inference;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.parser.SPParser;
import org.knoesis.rdf.sp.parser.SPParserFactory;

public class ContextualInferenceTest {

	ContextualInference inference;
	SPParser con;
	String rep = "ng";
	@Before
	public void setUp() throws Exception {
		con = SPParserFactory.createParser(rep);
		con.setOntoDir("src/main/resources/onto");
		con.setInfer(true);
	}

	@Test
	public void testInfer() {
		con.parse("src/test/resources/test-onto", "ttl", rep);
	}

}
