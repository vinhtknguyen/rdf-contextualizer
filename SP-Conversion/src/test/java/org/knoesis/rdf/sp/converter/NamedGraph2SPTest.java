package org.knoesis.rdf.sp.converter;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.parser.QuadParser;

public class NamedGraph2SPTest {

	QuadParser con1 = null;
	QuadParser con2 = null;
	String rep = "ng";
	@Before
	public void setUp() throws Exception {
		con1 = new QuadParser();
//		con1.setZip(true);
		con1.setOntoDir("src/main/resources/onto");
		con1.setInfer(true);
		con1.init();
		con2 = new QuadParser();
//		con2.setZip(true);
		con2.setOntoDir("src/main/resources/onto");
		con2.setInfer(true);
		con2.init();
		
	}

	@Test
	public void testConvert() {
		con1.parse("src/test/resources/test-ng", "ttl", rep);
		con1.parse("src/test/resources/test-ng", "nt", rep);
	}

	@Test
	public void testConvertFile() {
		con1.parse("src/test/resources/test-file/test2_ng.nq", "ttl", rep);
		con1.parse("src/test/resources/test-file/test2_ng.nq", "nt", rep);
	}

}
