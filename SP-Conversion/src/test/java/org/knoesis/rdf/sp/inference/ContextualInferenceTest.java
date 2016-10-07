package org.knoesis.rdf.sp.inference;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.parser.SPParser;
import org.knoesis.rdf.sp.parser.SPParserFactory;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.ConstantsTest;

public class ContextualInferenceTest {

	ContextualInference inference;
	SPParser con;
	String rep = Constants.NG_REP;
	@Before
	public void setUp() throws Exception {
		con = SPParserFactory.createParser(rep);
		con.setOntoDir(ConstantsTest.test_data_onto);
		con.setInfer(true);
		con.setShortenURI(true);
		con.init();
	}

	@Test
	public void testInfer() {
		con.parse(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_onto, "ttl", rep);
	}

}
