package org.knoesis.rdf.sp.converter;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.parser.Reporter;
import org.knoesis.rdf.sp.parser.SPParser;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.ConstantsTest;

public class Reification2SPTest {

	SPParser con1 = null;
	SPParser con2 = null;
	String rep = Constants.REI_REP;
	Reporter reporter1, reporter2;
	@Before
	public void setUp() throws Exception {
		 reporter1 = new Reporter();
		reporter1.setRep(rep);
		reporter1.setZip(false);
		reporter1.setOntoDir(ConstantsTest.test_data_onto);
		reporter1.setInfer(false);
		reporter1.setDsName("testNG1");
		reporter1.setParallel(1);

		con1 = new SPParser(reporter1);
		
		 reporter2 = new Reporter();
		reporter2.setZip(false);
		reporter2.setOntoDir(ConstantsTest.test_data_onto);
		reporter2.setInfer(true);
		reporter2.setShortenURI(true);
		reporter2.setDsName("testNG2");
		reporter2.setParallel(1);
		reporter2.setPrefix(ConstantsTest.test_data_prefix + "/bio2rdf_prefixes.ttl");
		reporter2.setRep(rep);
		reporter2.setUuidInitNum(10);
		reporter2.setUuidInitStr("str1");
		con2 = new SPParser(reporter2);
	}

	@Test
	public void testConvert() {
		con2 = new SPParser(reporter2);
		con2.parse(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_rei, "nt", rep);
//		con1.parse("src/test/resources/test-file/test2_rei.ttl", "nt", rep);
//		con2.parse("src/test/resources/test-file/test2_rei.ttl", "ttl", rep);
	}

	@Test
	public void testConvertFile() {
		con2 = new SPParser(reporter2);
		con2.parse(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_rei, "ttl", rep);
	}

}
