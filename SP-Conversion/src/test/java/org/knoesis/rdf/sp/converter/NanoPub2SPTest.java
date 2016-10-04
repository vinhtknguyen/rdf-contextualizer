package org.knoesis.rdf.sp.converter;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.parser.QuadParser;

public class NanoPub2SPTest {

	QuadParser con1 = null;
	String rep = "nano";
	@Before
	public void setUp() throws Exception {
		con1 = new QuadParser();
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
		con1.parse("src/test/resources/test-nano", "nt", rep);
		con1.parse("src/test/resources/test-nano", "ttl", rep);
	}

	@Test
	public void testConvertFile() {
		con1.parse("src/test/resources/test-file/test2_nano.nq", "ttl", rep);
		con1.parse("src/test/resources/test-file/test2_nano.nq", "nt", rep);
	}

	@Test
	public void testGenFileOut() {
	}

}
