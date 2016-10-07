package org.knoesis.rdf.sp.utils;

import java.awt.Color;
import java.io.*;
import java.nio.file.Paths;
import java.text.DecimalFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartUtilities;

public class SPStats {
	
	public static void reportSystem(long start, long end, String rep, String isInfer, String ext, String fileIn, String fileOut, String ds, String step) {
		
		DecimalFormat time_formatter = new DecimalFormat("#,###.00");
		
		BufferedWriter report = RDFWriteUtils.getReportWriter(Constants.STAT_FILE);
		try {
			report.write("Time\t\t" + time_formatter.format(end-start) + "\t" + rep + "\t" + isInfer + "\t" + ext + "\t" + ds + "\t" + step + "_"+ start + "\t" + fileOut + "\t" +  "\n");
			report.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void reportData(long start, String rep, String isInfer, String ext, String fileIn, String fileOut ) {
		long end = System.currentTimeMillis() - start;
		
		DecimalFormat time_formatter = new DecimalFormat("#,###.00");
		DecimalFormat size_formatter = new DecimalFormat("#,###");
		
		BufferedWriter report = RDFWriteUtils.getReportWriter(Constants.STAT_FILE);
		try {
			report.write("Time\t\t" + time_formatter.format(end) + "\t" + rep + "\t" + isInfer + "\t" + ext + "\t" + fileOut + "\t" + "\n");
			report.write("Diskspace\t" + size_formatter.format(Paths.get(fileOut).toFile().length()) + "\t" + rep + "\t" + isInfer + "\t" + ext + "\t" + fileOut + "\t" + "\n");
			report.write("Diskspace\t" + size_formatter.format(Paths.get(fileIn).toFile().length()) + "\t ORI \t" + isInfer + "\t" + ext + "\t" + fileIn + "\t" + "\n");
			report.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void genBarPlot() {

		final String fiat = "FIAT";
		final String audi = "AUDI";
		final String ford = "FORD";
		final String speed = "Speed";
		final String millage = "Millage";
		final String userrating = "User Rating";
		final String safety = "safety";

		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		dataset.addValue(1.0, fiat, speed);
		dataset.addValue(3.0, fiat, userrating);
		dataset.addValue(5.0, fiat, millage);
		dataset.addValue(5.0, fiat, safety);

		dataset.addValue(5.0, audi, speed);
		dataset.addValue(6.0, audi, userrating);
		dataset.addValue(10.0, audi, millage);
		dataset.addValue(4.0, audi, safety);

		dataset.addValue(4.0, ford, speed);
		dataset.addValue(2.0, ford, userrating);
		dataset.addValue(3.0, ford, millage);
		dataset.addValue(6.0, ford, safety);

		JFreeChart barChart = ChartFactory.createBarChart(
				"CAR USAGE STATIStICS", "Category", "Score", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		barChart.getPlot().setBackgroundPaint(Color.WHITE);

		int width = 640; /* Width of the image */
		int height = 480; /* Height of the image */
		File BarChart = new File("plotBarChart.jpeg");
		try {
			ChartUtilities.saveChartAsJPEG(BarChart, barChart, width, height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void genLinePlot() {
		DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
		line_chart_dataset.addValue(15, "schools", "1970");
		line_chart_dataset.addValue(30, "schools", "1980");
		line_chart_dataset.addValue(60, "schools", "1990");
		line_chart_dataset.addValue(120, "schools", "2000");
		line_chart_dataset.addValue(240, "schools", "2010");
		line_chart_dataset.addValue(300, "schools", "2014");

		JFreeChart lineChartObject = ChartFactory
				.createLineChart("Schools Vs Years", "Year", "Schools Count",
						line_chart_dataset, PlotOrientation.VERTICAL, true,
						true, false);
		lineChartObject.getPlot().setBackgroundPaint(Color.WHITE);

		int width = 640; /* Width of the image */
		int height = 480; /* Height of the image */
		File lineChart = new File("plotLineChart.jpeg");
		try {
			ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width,
					height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void genXYPlot() {
		final XYSeries firefox = new XYSeries("Firefox");
		firefox.add(1.0, 1.0);
		firefox.add(2.0, 4.0);
		firefox.add(3.0, 3.0);
		final XYSeries chrome = new XYSeries("Chrome");
		chrome.add(1.0, 4.0);
		chrome.add(2.0, 5.0);
		chrome.add(3.0, 6.0);
		final XYSeries iexplorer = new XYSeries("InternetExplorer");
		iexplorer.add(3.0, 4.0);
		iexplorer.add(4.0, 5.0);
		iexplorer.add(5.0, 4.0);
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(firefox);
		dataset.addSeries(chrome);
		dataset.addSeries(iexplorer);

		JFreeChart xylineChart = ChartFactory.createXYLineChart(
				"Browser usage statastics", "Category", "Score", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		xylineChart.getPlot().setBackgroundPaint(Color.WHITE);

		int width = 640; /* Width of the image */
		int height = 480; /* Height of the image */
		File XYChart = new File("plotXYLineChart.jpeg");
		try {
			ChartUtilities.saveChartAsJPEG(XYChart, xylineChart, width, height);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
