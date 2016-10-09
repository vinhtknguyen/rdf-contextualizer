package org.knoesis.rdf.sp.converter;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.parser.SPParser;
import org.knoesis.rdf.sp.parser.SPParserFactory;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.ConstantsTest;
public class ContextualRepresentationConverterTest {

	SPParser con1 = null;
	SPParser con2 = null;
	SPParser con3 = null;
	String rep = Constants.TRIPLE_REP;
	@Before
	public void setUp() throws Exception {
		con1 = SPParserFactory.createParser(rep);
//		con1.setZip(true);
		con1.setOntoDir(ConstantsTest.test_data_onto);
		con1.setInfer(false);
		con1.init();
	}

	@Test
	public void testConvert() {
//		con1.parse(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_default, "ttl", rep);
//		con1.parse(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_default, "nt", rep);
//		con1.parse(ConstantsTest.test_data_file + "/yagoFacts.ttl", "ttl", rep);
//		con1.parse(ConstantsTest.test_data_file + "/yagoFacts.ttl", "nt", rep);
	}

	@Test
	public void testConvertFile() {
	}

	@Test
	public void testTransform() {
	}

}
