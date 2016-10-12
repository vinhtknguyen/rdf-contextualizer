package org.knoesis.rdf.sp.converter;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.parser.Reporter;
import org.knoesis.rdf.sp.parser.SPParser;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.ConstantsTest;
public class ContextualRepresentationConverterTest {

	SPParser con1 = null;
	SPParser con2 = null;
	SPParser con3 = null;
	Reporter reporter;
	String rep = Constants.TRIPLE_REP;
	@Before
	public void setUp() throws Exception {
		reporter = new Reporter();
//		con1.setZip(true);
		reporter.setOntoDir(ConstantsTest.test_data_onto);
		reporter.setInfer(true);
		reporter.setShortenURI(true);
		reporter.setParallel(2);
		
		con1 = new SPParser(reporter);
		con1.parse(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_default, "ttl", rep);
	}

	@Test
	public void testConvert() {
		
		con1 = new SPParser(reporter);
		con1.parse(ConstantsTest.test_data_file + "/yagoFacts.ttl", "nt", rep);
		
	}

	@Test
	public void testConvertFile() {
		con1 = new SPParser(reporter);
		con1.parse(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_default, "nt", rep);
	}

	@Test
	public void testTransform() {
		con1 = new SPParser(reporter);
		con1.parse(ConstantsTest.test_data_file + "/yagoFacts.ttl", "ttl", rep);
	}

}
