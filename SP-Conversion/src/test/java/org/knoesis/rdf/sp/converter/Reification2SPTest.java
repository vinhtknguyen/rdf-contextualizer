package org.knoesis.rdf.sp.converter;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.parser.TripleParser;

public class Reification2SPTest {

	TripleParser con1 = null;
	String rep = "rei";
	@Before
	public void setUp() throws Exception {
		con1 = new TripleParser();
//		con1.setZip(true);
		con1.setOntoDir("src/main/resources/onto");
		con1.setInfer(true);
		con1.init();
	}
	@Test
	public void testTransform() {
	}

	@Test
	public void testConvert() {
		con1.parse("src/test/resources/test-rei", "nt", rep);
		con1.parse("src/test/resources/test-rei", "ttl", rep);
	}

	@Test
	public void testConvertFile() {
		con1.parse("src/test/resources/test-file/test2_rei.ttl", "ttl", rep);
		con1.parse("src/test/resources/test-file/test2_rei.ttl", "nt", rep);
	}

}
