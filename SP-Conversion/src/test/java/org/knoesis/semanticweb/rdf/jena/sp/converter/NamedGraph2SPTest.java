package org.knoesis.semanticweb.rdf.jena.sp.converter;

import org.junit.Before;
import org.junit.Test;

public class NamedGraph2SPTest {

	NamedGraph2SP con1 = null;
	NamedGraph2SP con2 = null;
	@Before
	public void setUp() throws Exception {
		con1 = new NamedGraph2SP();
		con2 = new NamedGraph2SP(10, "_sp", "___", "http://knoesis.org/rdf:singletonPropertyOf");
	}

	@Test
	public void testTransform() {
	}

	@Test
	public void testSetNamedGraphPropString() {
	}

	@Test
	public void testConvert() {
		con1.convert("src/test/resources/test-ng", "ttl", "ng");
		con1.convert("src/test/resources/test-ng", "nt", "ng");
	}

	@Test
	public void testConvertFile() {
		con1.convert("src/test/resources/test-file/test2_ng.nq", "ttl", "ng");
		con1.convert("src/test/resources/test-file/test2_ng.nq", "nt", "ng");
	}

}
