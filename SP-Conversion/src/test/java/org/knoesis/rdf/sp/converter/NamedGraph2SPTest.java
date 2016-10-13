package org.knoesis.rdf.sp.converter;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.parser.Reporter;
import org.knoesis.rdf.sp.parser.SPAnalyzer;
import org.knoesis.rdf.sp.parser.SPParser;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.ConstantsTest;

public class NamedGraph2SPTest {

	SPParser con1 = null;
	SPParser con2 = null;
	String rep = Constants.NG_REP;
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
	public void testConvert1() {
		con1 = new SPParser(reporter1);
		con1.parse(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng, "ttl", rep);
		
		// Testing the case in which URIs are shortened with the pre-existing prefixes
		
	}
	@Test
	public void testConvert3(){
		con1 = new SPParser(reporter1);
		con1.parse(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng, "nt", rep);
		con1 = new SPParser(reporter1);
		con1.parse(ConstantsTest.test_data_file + "/test2_ng.nq", "nt", rep);
		con1 = new SPParser(reporter1);
		con1.parse(ConstantsTest.test_data_file + "/test2_ng.nq", "ttl", rep);
		
	}

	@Test
	public void testConvert2() {
		// Testing the case in which URIs are shortened with all possible prefixes
		con2 = new SPParser(reporter2);
		con2.parse(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng, "ttl", rep);
	}
	
	@Test
	public void testDir(){
		con2 = new SPParser(reporter2);
		con2.parse(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_all, "ttl", rep);
		con2 = new SPParser(reporter2);
		con2.parse(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_all, "ttl", rep);
	}

	@Test
	public void testParserAnalyzer(){
		reporter2.setDsName("testNG-infer");
		con2 = new SPParser(reporter2);
		con2.parse(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_all, "nt", rep);
		SPAnalyzer analyzer = new SPAnalyzer(reporter2);
		analyzer.analyze(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_all, true);
		reporter2.setInfer(false);
		reporter2.setDsName("testNG-noinfer");
		con2 = new SPParser(reporter2);
		con2.parse(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_all, "nt", rep);
		analyzer = new SPAnalyzer(reporter2);
		analyzer.analyze(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_all, true);
	}
}
