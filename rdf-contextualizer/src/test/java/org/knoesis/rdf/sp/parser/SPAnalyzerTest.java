package org.knoesis.rdf.sp.parser;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.ConstantsTest;

public class SPAnalyzerTest {

	SPAnalyzer analyzer;
	Reporter reporter1;
	String rep = Constants.NG_REP;
	@Before
	public void setUp() throws Exception {
		reporter1 = new Reporter();
		reporter1.setRep(rep);
		reporter1.setZip(false);
		reporter1.setOntoDir(ConstantsTest.test_data_onto);
		reporter1.setInfer(true);
		reporter1.setDsName("testNG1");
		analyzer = new SPAnalyzer(reporter1);
	}

	@Test
	public void testAnalyze() {
		analyzer.getReporter().setExt("nt");
		analyzer.analyze(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_all);
	}

}
