package org.knoesis.semanticweb.rdf.jena.sp.converter;

import org.junit.Before;
import org.junit.Test;

public class ContextualRepresentationConverterTest {

	ContextualRepresentationConverter con1 = null;
	ContextualRepresentationConverter con2 = null;
	@Before
	public void setUp() throws Exception {
		con1 = new ContextualRepresentationConverter();
		con2 = new ContextualRepresentationConverter(10, "crc_", "_", "http://knoesis.org/rdf:singletonPropertyOf");
	}

	@Test
	public void testConvert() {
		con1.convert("src/test/resources/test-default", "ttl", "none");
		con1.convert("src/test/resources/test-default", "nt", "none");
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
