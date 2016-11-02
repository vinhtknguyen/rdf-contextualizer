package org.knoesis.rdf.sp.stat;

import org.junit.Before;
import org.junit.Test;

public class SPChartGeneratorTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test1() {
		SPChartGenerator gen = new SPChartGenerator(false);
		gen.genCharts("reports_249", "/Volumes/Data/Submissions/ReasoningWithSP/images/charts_249");
	}
	@Test
	public void test2() {
		SPChartGenerator gen = new SPChartGenerator(true);
		gen.genCharts("reports_249_nodup", "/Volumes/Data/Submissions/ReasoningWithSP/images/charts_249_nodup");
	}

}
