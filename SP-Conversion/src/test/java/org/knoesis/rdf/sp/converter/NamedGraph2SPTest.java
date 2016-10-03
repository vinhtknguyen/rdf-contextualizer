package org.knoesis.rdf.sp.converter;

import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.converter.NamedGraph2SP;

public class NamedGraph2SPTest {

	NamedGraph2SP con1 = null;
	NamedGraph2SP con2 = null;
	NamedGraph2SP con3 = null;
	@Before
	public void setUp() throws Exception {
		con1 = new NamedGraph2SP();
		con2 = new NamedGraph2SP(10, "crc_", "___");
		con3 = new NamedGraph2SP();
		con3.setZip(true);
		con1.setOntoDir("src/main/resources/onto");
		con1.setInfer(true);
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
		con3.convert("src/test/resources/test-ng", "ttl", "ng");
		con3.convert("src/test/resources/test-ng", "nt", "ng");
	}

	@Test
	public void testConvertFile() {
		con1.convert("src/test/resources/test-file/test2_ng.nq", "ttl", "ng");
//		con1.convert("src/test/resources/test-file/test2_ng.nq", "nt", "ng");
	}

}
