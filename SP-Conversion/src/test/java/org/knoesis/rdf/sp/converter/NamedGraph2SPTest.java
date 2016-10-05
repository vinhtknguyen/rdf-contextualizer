package org.knoesis.rdf.sp.converter;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.parser.SPParser;
import org.knoesis.rdf.sp.parser.SPParserFactory;

public class NamedGraph2SPTest {

	SPParser con1 = null;
	SPParser con2 = null;
	String rep = "ng";
	@Before
	public void setUp() throws Exception {
		con1 = SPParserFactory.createParser(rep);
//		con1.setZip(true);
		con1.setOntoDir("src/main/resources/onto");
		con1.setInfer(false);
		con1.init();
		con2 = SPParserFactory.createParser(rep, 10, "str1");
//		con2.setZip(true);
		con2.setOntoDir("src/main/resources/onto");
		con2.setInfer(true);
		con2.init();
	}

	@Test
	public void testConvert() {
		con1.parse("src/test/resources/test-ng", "nt", rep);
		con2.parse("src/test/resources/test-ng", "ttl", rep);
	}

	@Test
	public void testConvertFile() {
		con1.parse("src/test/resources/test-file/test2_ng.nq", "nt", rep);
		con2.parse("src/test/resources/test-file/test2_ng.nq", "ttl", rep);
	}

}
