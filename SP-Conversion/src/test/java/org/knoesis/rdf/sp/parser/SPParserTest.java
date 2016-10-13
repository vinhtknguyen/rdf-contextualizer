package org.knoesis.rdf.sp.parser;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.ConstantsTest;

public class SPParserTest {

	SPParser parser;
	String rep = Constants.NG_REP;
	@Before
	public void setUp() throws Exception {
		Reporter reporter1 = new Reporter();
		reporter1.setRep(rep);
		reporter1.setZip(false);
		reporter1.setOntoDir(ConstantsTest.test_data_onto);
		reporter1.setInfer(false);
		reporter1.setDsName("testNG1");
		reporter1.setParallel(1);
		parser = new SPParser(reporter1);
	}

	@Test
	public void testGenFileList() {
		parser.getReporter().setExt("nt");
		parser.genFileList(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_all, null, Constants.PROCESSING_TASK_GENERATE);
		assertEquals(parser.getManager().size(),4);
		parser.getManager().printParserElements();
	}

}
