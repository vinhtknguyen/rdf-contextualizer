package org.knoesis.rdf.sp.converter;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.parser.SPParser;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.ConstantsTest;

public class NamedGraph2SPTest {

	SPParser con1 = null;
	SPParser con2 = null;
	String rep = Constants.NG_REP;
	@Before
	public void setUp() throws Exception {
		con1 = new SPParser(rep);
		con1.setZip(false);
		con1.setOntoDir(ConstantsTest.test_data_onto);
		con1.setInfer(false);
		con1.setDsName("testNG1");
		con1.setParallel(1);
		
		con2 = new SPParser(rep, 10, "str1");
		con2.setZip(false);
		con2.setOntoDir(ConstantsTest.test_data_onto);
		con2.setInfer(true);
		con2.setShortenURI(true);
		con2.setDsName("testNG2");
		con2.setParallel(1);
		con2.setPrefix(ConstantsTest.test_data_prefix + "/bio2rdf_prefixes.ttl");
	}

	@Test
	public void testConvert1() {
		con1.parse(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng, "nt", rep);
		
		// Testing the case in which URIs are shortened with the pre-existing prefixes
		
	}
	@Test
	public void testConvert3(){
		con1.parse(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng, "nt", rep);
		con1.parse(ConstantsTest.test_data_file + "/test2_ng.nq", "nt", rep);
		con1.parse(ConstantsTest.test_data_file + "/test2_ng.nq", "ttl", rep);
		
	}

	@Test
	public void testConvert2() {
		// Testing the case in which URIs are shortened with all possible prefixes
		con2.parse(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng, "ttl", rep);
	}
	
	@Test
	public void testDir(){
		con2.parse(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_all, "ttl", rep);
	}

}
