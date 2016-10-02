package org.knoesis.semanticweb.rdf.jena.sp.converter;

import org.junit.Before;
import org.junit.Test;

public class ContextualRepresentationConverterTest {

	ContextualRepresentationConverter con1 = null;
	ContextualRepresentationConverter con2 = null;
	ContextualRepresentationConverter con3 = null;
	@Before
	public void setUp() throws Exception {
		con1 = new ContextualRepresentationConverter();
		con2 = new ContextualRepresentationConverter(10, "crc_", "_");
		con3 = new ContextualRepresentationConverter();
		con3.setZip(true);
	}

	@Test
	public void testConvert() {
		con1.convert("src/test/resources/test-default", "ttl", "none");
		con1.convert("src/test/resources/test-default", "nt", "none");
		con3.convert("src/test/resources/test-default", "ttl", "none");
		con3.convert("src/test/resources/test-default", "nt", "none");
	}

	@Test
	public void testConvertFile() {
		con1.convert("src/test/resources/test-file/yagoFacts.ttl", "ttl", "none");
		con1.convert("src/test/resources/test-file/yagoFacts.ttl", "nt", "none");
	}

	@Test
	public void testTransform() {
	}

}
