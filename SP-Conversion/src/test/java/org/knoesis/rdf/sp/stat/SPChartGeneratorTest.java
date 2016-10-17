package org.knoesis.rdf.sp.stat;

import org.junit.Before;
import org.junit.Test;

public class SPChartGeneratorTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		SPChartGenerator gen = new SPChartGenerator();
		gen.genCharts("reports_249", "charts_249");
	}

}
