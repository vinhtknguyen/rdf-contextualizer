package org.knoesis.rdf.sp.converter;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.converter.Reification2SP;

public class Reification2SPTest {

	Reification2SP con1 = null;
	Reification2SP con2 = null;
	@Before
	public void setUp() throws Exception {
		con1 = new Reification2SP();
		con2 = new Reification2SP(10, "crc_", "___", "http://knoesis.org/rdf:singletonPropertyOf");
	}

	@Test
	public void testTransform() {
	}

	@Test
	public void testConvert() {
		con1.convert("src/test/resources/test-rei", "nt", "rei");
		con1.convert("src/test/resources/test-rei", "ttl", "rei");
	}

	@Test
	public void testConvertFile() {
		con1.convert("src/test/resources/test-file/test2_rei.ttl", "ttl", "rei");
		con1.convert("src/test/resources/test-file/test2_rei.ttl", "nt", "rei");
	}

}
