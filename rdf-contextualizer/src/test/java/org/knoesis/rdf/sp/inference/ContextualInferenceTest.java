package org.knoesis.rdf.sp.inference;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.parser.Reporter;
import org.knoesis.rdf.sp.parser.SPParser;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.ConstantsTest;

public class ContextualInferenceTest {

	ContextualInference inference;
	SPParser con;
	String rep = Constants.NG_REP;
	@Before
	public void setUp() throws Exception {
		Reporter reporter1 = new Reporter();
		reporter1.setRep(rep);
		reporter1.setZip(false);
		reporter1.setOntoDir(ConstantsTest.test_data_onto);
		reporter1.setInfer(true);
		reporter1.setDsName("testNG1");
		reporter1.setParallel(1);

		con = new SPParser(reporter1);
	}

	@Test
	public void testInfer() {
		con.parse(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_onto, null, "ttl", rep);
	}

}
