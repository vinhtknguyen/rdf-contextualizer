package org.knoesis.rdf.sp.converter;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.parser.SPParser;
import org.knoesis.rdf.sp.parser.SPParserFactory;

public class ContextualRepresentationConverterTest {

	SPParser con1 = null;
	SPParser con2 = null;
	SPParser con3 = null;
	String rep = "none";
	@Before
	public void setUp() throws Exception {
		con1 = SPParserFactory.createParser(rep);
//		con1.setZip(true);
		con1.setOntoDir("src/main/resources/onto");
		con1.setInfer(true);
		con1.init();
	}

	@Test
	public void testConvert() {
		con1.parse("src/test/resources/test-default", "ttl", rep);
		con1.parse("src/test/resources/test-default", "nt", rep);
	}

	@Test
	public void testConvertFile() {
		con1.parse("src/test/resources/test-file/yagoFacts.ttl", "ttl", rep);
		con1.parse("src/test/resources/test-file/yagoFacts.ttl", "nt", rep);
	}

	@Test
	public void testTransform() {
	}

}
