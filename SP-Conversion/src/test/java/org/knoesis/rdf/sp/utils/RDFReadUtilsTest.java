package org.knoesis.rdf.sp.utils;

import org.junit.Before;
import org.junit.Test;

public class RDFReadUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testReadUrl() {
		RDFReadUtils.readUrl("http://download.bio2rdf.org/release/4/ctd/");
	}

}
