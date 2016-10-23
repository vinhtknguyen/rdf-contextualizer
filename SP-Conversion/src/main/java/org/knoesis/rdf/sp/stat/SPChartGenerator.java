package org.knoesis.rdf.sp.stat;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.knoesis.rdf.sp.utils.Constants;

import com.fasterxml.jackson.annotation.JsonFormat.Shape;

public class SPChartGenerator {

	private HashMap<String, SPDataset> allDSs;
	private List<SPDataset> sortedDSs;
	int width = 480;
	int height = 300;
	
	public void genCharts(String reportDir, String chartDir){
		allDSs = new HashMap<String, SPDataset>();
		sortedDSs = new ArrayList<SPDataset>();
		
		// Read the report data to ds
		readTime(reportDir);
		readData(reportDir);
		readDisk(reportDir);
		readSingletonProp(reportDir);
		
		Iterator<Entry<String, SPDataset>> it = allDSs.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry<String,SPDataset> pair = (Map.Entry<String,SPDataset>)it.next();
		    if (pair.getValue().getTotalTriples() > 2*Math.pow(10,8)) sortedDSs.add(pair.getValue());
		}

		Collections.sort(sortedDSs, new Comparator<SPDataset>(){

			@Override
			public int compare(SPDataset o1, SPDataset o2) {
				// TODO Auto-generated method stub
				return Long.compare(o2.getTotalTriples(), o1.getTotalTriples());
			}
		});
		for (int i = 0; i < sortedDSs.size()-1; i++){
			SPDataset spr = sortedDSs.get(i);
			if (spr.isInfer()){
				String key = spr.getDsName() + spr.getExt() + Constants.DS_TYPE_SP;
				SPDataset sp = allDSs.get(key);
				if (spr.getDsName().equals(sp.getDsName()) && spr.getExt().equals(sp.getExt())){
					spr.setTotalInferredTriples(spr.getTotalTriples() - sp.getTotalTriples());
					spr.setTimeTakenInferredTriples(spr.getAverageTimeTaken() - sp.getAverageTimeTaken());
					spr.setDiskInferredTriples(spr.getDisk() - sp.getDisk());
				}
			}
			System.out.println(sortedDSs.get(i).toString());
		}

	    try {
			Files.createDirectories(Paths.get(chartDir));
			// Generate charts
			generateDataChart(chartDir);
			generateTimeChart(chartDir);
			generateSingletonChart(chartDir);
			generateDiskChart(chartDir);
			generateTimeSizeChart(chartDir);
			generateTimeInferredTriplesChart(chartDir);
			generateDiskInferredTriplesChart(chartDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	private void formatChart(JFreeChart chart){
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinePaint(Color.black);		
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setSeriesPaint(0, Color.red);
		renderer.setSeriesPaint(1, Color.black);
		renderer.setDrawBarOutline(false);
		renderer.setItemMargin(0.0);

		CategoryItemRenderer renderer2 = plot.getRenderer();
		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator(
		"{2}", new DecimalFormat("0.00"));
		renderer2.setItemLabelGenerator(generator);
	}
	
	@SuppressWarnings("deprecation")
	private void formatXYLineChart(JFreeChart chart){
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainGridlinePaint(Color.BLACK);
		plot.setRangeGridlinePaint(Color.BLACK);
		XYLineAndShapeRenderer renderer1 = (XYLineAndShapeRenderer) plot.getRenderer();
		renderer1.setShapesVisible(true);
		renderer1.setShapesFilled(true);
		XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint(0, Color.red);
		renderer.setSeriesPaint(1, Color.black);
		renderer.setSeriesPaint(2, Color.blue);
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		XYItemRenderer renderer2 = plot.getRenderer();
		XYItemLabelGenerator generator = new StandardXYItemLabelGenerator(
		"{2}", new DecimalFormat("0.00"), new DecimalFormat("0.00"));
		renderer2.setItemLabelGenerator(generator);
	}
	
	private void generateDataChart(String chartDir){
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (SPDataset ds : sortedDSs){
			if (ds.getExt().equals(Constants.TURTLE_EXT)) dataset.addValue(ds.getTotalTriples()/Math.pow(10, 9), ds.getDsLabel(), ds.getDsNameForChart());
		}

		JFreeChart barChart = ChartFactory.createBarChart(
				"Total Triples: With vs. Without Reasoning", "Singleton Property Dataset", "Triples in Billion", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		
		formatChart(barChart);
		
		File BarChart = new File(chartDir + "/" + Paths.get(Constants.STAT_FILE_DATA_CHART).getFileName());
		try {
			ChartUtilities.saveChartAsJPEG(BarChart, barChart, width, height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private void generateTimeSizeChart(String chartDir){

		final XYSeries sp = new XYSeries(Constants.DS_TYPE_SP);
		for (SPDataset ds : sortedDSs){
			if (!ds.isInfer()) sp.add(ds.getTotalTriples()/Math.pow(10, 9),ds.getAverageTimeTaken()/60000);
//			if (!ds.isInfer()) sp.add(ds.getAverageTimeTaken()/60000, ds.getTotalTriples()/Math.pow(10, 8));
		}
		
		final XYSeries spr = new XYSeries(Constants.DS_TYPE_SP + "-Reasoning");
		for (SPDataset ds : sortedDSs){
			if (ds.isInfer()) spr.add(ds.getTotalTriples()/Math.pow(10, 9), ds.getAverageTimeTaken()/60000);
		}
		
		final XYSeries inf = new XYSeries("Inferred triples");
		for (SPDataset ds : sortedDSs){
			if (ds.isInfer()) inf.add(ds.getTotalInferredTriples()/Math.pow(10, 9), ds.getTimeTakenInferredTriples()/60000);
		}
		
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(spr);
		dataset.addSeries(sp);
//		dataset.addSeries(inf);

		JFreeChart barChart = ChartFactory.createXYLineChart(
				"Time taken across datasets", "Number of triples in Billion", "Time in minutes", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		
		formatXYLineChart(barChart);
		
		File BarChart = new File(chartDir + "/" + Paths.get(Constants.STAT_FILE_TIME_SIZE_CHART).getFileName());
		try {
			ChartUtilities.saveChartAsJPEG(BarChart, barChart, width, height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private void generateTimeInferredTriplesChart(String chartDir){

		final XYSeries spr = new XYSeries(Constants.DS_TYPE_SP + "-Reasoning");
		for (SPDataset ds : sortedDSs){
			if (ds.isInfer() && ds.getExt().equals(Constants.TURTLE_EXT)) spr.add(ds.getTotalInferredTriples()/Math.pow(10, 9), ds.getTimeTakenInferredTriples()/60000);
		}
		
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(spr);

		JFreeChart barChart = ChartFactory.createXYLineChart(
				"Time taken for inferred triples", "Number of inferred triples in Billion", "Time in minutes", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		
		formatXYLineChart(barChart);
		
		File BarChart = new File(chartDir + "/" + Paths.get(Constants.STAT_FILE_TIME_INFERRED_TRIPLES_CHART).getFileName());
		try {
			ChartUtilities.saveChartAsJPEG(BarChart, barChart, width, height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private void generateDiskInferredTriplesChart(String chartDir){

		final XYSeries spr = new XYSeries(Constants.DS_TYPE_SP + "-Reasoning");
		for (SPDataset ds : sortedDSs){
			if (ds.isInfer() && ds.getExt().equals(Constants.TURTLE_EXT)) spr.add(ds.getTotalInferredTriples()/Math.pow(10, 9), ds.getDiskInferredTriples()/Math.pow(2, 30));
		}
		
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(spr);

		JFreeChart barChart = ChartFactory.createXYLineChart(
				"Disk space for inferred triples", "Number of inferred triples in Billion", "Disk space in Gigabyte", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		
		formatXYLineChart(barChart);
		
		File BarChart = new File(chartDir + "/" + Paths.get(Constants.STAT_FILE_DISK_INFERRED_TRIPLES_CHART).getFileName());
		try {
			ChartUtilities.saveChartAsJPEG(BarChart, barChart, width, height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void generateTimeChart(String chartDir) {
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (SPDataset ds : sortedDSs){
			if (ds.getTimeTaken().size() > 0 && ds.getExt().equals(Constants.TURTLE_EXT)) dataset.addValue(ds.getAverageTimeTaken()/60000, ds.getDsLabel(), ds.getDsNameForChart());
		}

		JFreeChart barChart = ChartFactory.createBarChart(
				"Time Taken: With vs. Without Reasoning", "Singleton Property Dataset", "Time in minutes", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		
		formatChart(barChart);
		
		File BarChart = new File(chartDir + "/" + Paths.get(Constants.STAT_FILE_TIME_CHART).getFileName());
		try {
			ChartUtilities.saveChartAsJPEG(BarChart, barChart, width, height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void generateSingletonChart(String chartDir){
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (SPDataset ds : sortedDSs){
			if (ds.getExt().equals(Constants.TURTLE_EXT)) dataset.addValue(ds.getTotalSingletonProps()/Math.pow(10, 8), ds.getDsLabel(), ds.getDsNameForChart());
		}

		JFreeChart barChart = ChartFactory.createBarChart(
				"Singleton Triples: With vs. Without Reasoning", "Singleton Property Dataset", "Triples in 100 Million", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		
		formatChart(barChart);
		
		File BarChart = new File(chartDir + "/" + Paths.get(Constants.STAT_FILE_SINGLETON_CHART).getFileName());
		try {
			ChartUtilities.saveChartAsJPEG(BarChart, barChart, width, height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void generateDiskChart(String chartDir){
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (SPDataset ds : sortedDSs){
			if (ds.getExt().equals(Constants.TURTLE_EXT)) dataset.addValue(ds.getDisk()/Math.pow(2, 30), ds.getDsLabel(), ds.getDsNameForChart());
		}

		JFreeChart barChart = ChartFactory.createBarChart(
				"Disk Space: With vs. Without Reasoning", "Singleton Property Dataset", "Size in Gigabytes", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		
		formatChart(barChart);
		
		File BarChart = new File(chartDir + "/" + Paths.get(Constants.STAT_FILE_DISK_CHART).getFileName());
		try {
			ChartUtilities.saveChartAsJPEG(BarChart, barChart, width, height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void readTime(String reportDir){
		try {
			List<String> lines = Files.readAllLines(Paths.get(reportDir + "/" + Paths.get(Constants.STAT_FILE_TIME).getFileName()));
			for (String line: lines){
				String[] tmp = line.split("\t");
				Long time = Long.parseLong(tmp[0]);
				String dsType = tmp[1];
				String dsName = tmp[2];
				String ext = tmp[3];
				SPDataset ds;
				String key = dsName + ext + dsType;
				if (allDSs.containsKey(key)){
					ds = allDSs.get(key);
				} else {
					ds = new SPDataset(dsName, ext, (dsType.equals(Constants.DS_TYPE_SPR)));
				}
				ds.getTimeTaken().add(time);
				allDSs.put(key, ds);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void readData(String reportDir){
		try {
			List<String> lines = Files.readAllLines(Paths.get(reportDir + "/" + Paths.get(Constants.STAT_FILE_DATA).getFileName()));
			for (String line: lines){
				String[] tmp = line.split("\t");
				Long totalTriples = Long.parseLong(tmp[0]);
				String dsType = tmp[1];
				String dsName = tmp[2];
				String ext = tmp[3];
				SPDataset ds;
				String key = dsName + ext + dsType;
				if (allDSs.containsKey(key)){
					ds = allDSs.get(key);
				} else {
					ds = new SPDataset(dsName, ext, (dsType.equals(Constants.DS_TYPE_SPR)));
				}
				ds.setTotalTriples(totalTriples);
				allDSs.put(key, ds);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void readDisk(String reportDir){
		try {
			List<String> lines = Files.readAllLines(Paths.get(reportDir + "/" + Paths.get(Constants.STAT_FILE_DISK).getFileName()));
			for (String line: lines){
				String[] tmp = line.split("\t");
				Long totalSize = Long.parseLong(tmp[0]);
				String dsType = tmp[1];
				String dsName = tmp[2];
				String ext = tmp[3];
				SPDataset ds;
				String key = dsName + ext + dsType;
				if (allDSs.containsKey(key)){
					ds = allDSs.get(key);
				} else {
					ds = new SPDataset(dsName, ext, (dsType.equals(Constants.DS_TYPE_SPR)));
				}
				ds.setDisk(totalSize);
				allDSs.put(key, ds);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void readSingletonProp(String reportDir){
		try {
			List<String> lines = Files.readAllLines(Paths.get(reportDir + "/" + Paths.get(Constants.STAT_FILE_SINGLETON).getFileName()));
			for (String line: lines){
				String[] tmp = line.split("\t");
				Long totalSingleton = Long.parseLong(tmp[0]);
				String dsType = tmp[1];
				String dsName = tmp[2];
				String ext = tmp[3];
				SPDataset ds;
				String key = dsName + ext + dsType;
				if (allDSs.containsKey(key)){
					ds = allDSs.get(key);
				} else {
					ds = new SPDataset(dsName, ext, (dsType.equals(Constants.DS_TYPE_SPR)));
				}
				ds.setTotalSingletonProps(totalSingleton);
				allDSs.put(key, ds);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
