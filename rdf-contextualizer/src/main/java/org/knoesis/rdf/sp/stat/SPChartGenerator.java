package org.knoesis.rdf.sp.stat;

import java.awt.Color;
import java.awt.Font;
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
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.knoesis.rdf.sp.utils.Constants;

public class SPChartGenerator {

	private HashMap<String, SPDataset> allDSs;
	private List<SPDataset> sortedDSs;
	int width = 480;
	int height = 300;
	boolean nodup;
	
	public SPChartGenerator(boolean nodup) {
		super();
		this.nodup = nodup;
		if (nodup){
			this.width = 700;
		}
	}

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
		    if (pair.getValue().getTotalTriples() > 1.5*Math.pow(10,8)) sortedDSs.add(pair.getValue());
		}

		Collections.sort(sortedDSs, new Comparator<SPDataset>(){

			@Override
			public int compare(SPDataset o1, SPDataset o2) {
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
					spr.setTotalInferredSingletonTriples(spr.getTotalSingletonTriples() - sp.getTotalSingletonTriples());
					spr.setTimeTakenInferredTriples(spr.getAverageTimeTaken() - sp.getAverageTimeTaken());
					spr.setDiskInferredTriples(spr.getDisk() - sp.getDisk());
					
					spr.setInferredTriplePercentage((float)spr.getTotalInferredTriples()/sp.getTotalTriples());
					spr.setInferredSingletonTriplePercentage((float)spr.getTotalInferredSingletonTriples()/sp.getTotalSingletonTriples());
					spr.setTimePercentage((float)spr.getTimeTakenInferredTriples()/sp.getAverageTimeTaken());
					spr.setDiskPercentage((float)spr.getDiskInferredTriples()/sp.getDisk());
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
			generateSizeTable();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void generateSizeTable() {
		System.out.println("Table");
		DecimalFormat time_formatter = new DecimalFormat("#,###");
		for (SPDataset ds : sortedDSs){
			if (!ds.isInfer() &&ds.getExt().equals(Constants.TURTLE_EXT) && !ds.getDsName().startsWith(Constants.DS_TYPE_NoDup)) {
				System.out.print(ds.getDsNameForChart() + "\t" + time_formatter.format(ds.getTotalSingletonTriples()));
				SPDataset nd = allDSs.get(Constants.DS_TYPE_NoDup + ds.getDsName() + ds.getExt() + (ds.isInfer()?Constants.DS_TYPE_SPR:Constants.DS_TYPE_SP));
				if (nd != null) System.out.print("\t" + time_formatter.format(nd.getTotalSingletonTriples()));
				System.out.print("\n");
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void formatChart(JFreeChart chart){
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinePaint(Color.black);		
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setBarPainter(new StandardBarPainter());
//		renderer.setSeriesPaint(0, Color.lightGray);
//		renderer.setSeriesPaint(1, Color.blue);
//		renderer.setSeriesPaint(2, Color.magenta);
//		renderer.setSeriesPaint(3, Color.cyan);
		renderer.setSeriesPaint(0, new Color(161, 44, 33));
		renderer.setSeriesPaint(1, new Color(231, 77, 36));
		renderer.setSeriesPaint(2, new Color(243, 113, 33));
		renderer.setSeriesPaint(3, new Color(248, 151, 40));
		renderer.setSeriesOutlineStroke(0, null);
		renderer.setDrawBarOutline(false);
		renderer.setItemMargin(0.0);

		CategoryItemRenderer renderer2 = plot.getRenderer();
		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator(
		"{2}", new DecimalFormat("0.00"));
		renderer2.setItemLabelGenerator(generator);
		Font font = new Font("Dialog", Font.BOLD, 15); 
		if (chart.getTitle() != null) chart.getTitle().setFont(font);
//		plot.getRangeAxis().setLabelFont(font3);
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
//		renderer.setSeriesPaint(0, Color.BLACK);
//		renderer.setSeriesPaint(1, Color.blue);
//		renderer.setSeriesPaint(2, Color.magenta);
//		renderer.setSeriesPaint(3, Color.RED);
		renderer.setSeriesPaint(0, new Color(161, 44, 33));
		renderer.setSeriesPaint(1, new Color(231, 77, 36));
		renderer.setSeriesPaint(2, new Color(243, 113, 33));
		renderer.setSeriesPaint(3, new Color(248, 151, 40));
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		XYItemRenderer renderer2 = plot.getRenderer();
		XYItemLabelGenerator generator = new StandardXYItemLabelGenerator(
		"{2}", new DecimalFormat("0.00"), new DecimalFormat("0.00"));
		renderer2.setItemLabelGenerator(generator);

		Font font = new Font("Dialog", Font.BOLD, 15); 
		if (chart.getTitle() != null) chart.getTitle().setFont(font);
//		plot.getRangeAxis().setLabelFont(font3);
	}
	
	private void generateDataChart(String chartDir){
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (SPDataset ds : sortedDSs){
			if (ds.getExt().equals(Constants.TURTLE_EXT) && !ds.getDsName().startsWith(Constants.DS_TYPE_NoDup)) {
				dataset.addValue(ds.getTotalTriples()/Math.pow(10, 9), ds.getDsLabel() + "-" + Constants.DS_TYPE_Dup_Full, ds.getDsNameForChart());
				SPDataset nd = allDSs.get(Constants.DS_TYPE_NoDup + ds.getDsName() + ds.getExt() + (ds.isInfer()?Constants.DS_TYPE_SPR:Constants.DS_TYPE_SP));
				if (nd != null) dataset.addValue(nd.getTotalTriples()/Math.pow(10, 9), ds.getDsLabel() + "-" + Constants.DS_TYPE_NoDup_Full, ds.getDsNameForChart());
			}
		}
		
		JFreeChart barChart;
		if (!nodup){
			barChart = ChartFactory.createBarChart(
					"Total Triples: With vs. Without Reasoning", null, "Number of Triples in Billion", dataset,
					PlotOrientation.VERTICAL, false, true, false);
		} else {
			barChart = ChartFactory.createBarChart(
					null , "Singleton Property Dataset", "Number of Triples in Billion", dataset,
					PlotOrientation.VERTICAL, true, true, false);
		}
		
		formatChart(barChart);
		
		File BarChart = new File(chartDir + "/" + Paths.get(Constants.STAT_FILE_DATA_CHART).getFileName());
		try {
			ChartUtilities.saveChartAsJPEG(BarChart, barChart, width, height);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	private void generateTimeSizeChart(String chartDir){

		final XYSeries sp = new XYSeries("No-Reasoning");
		for (SPDataset ds : sortedDSs){
			if (!ds.isInfer()) sp.add(ds.getTotalTriples()/Math.pow(10, 9),ds.getAverageTimeTaken()/60000);
//			if (!ds.isInfer()) sp.add(ds.getAverageTimeTaken()/60000, ds.getTotalTriples()/Math.pow(10, 8));
		}
		
		final XYSeries spr = new XYSeries("With-Reasoning");
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

		JFreeChart barChart;
		if (!nodup){
			barChart = ChartFactory.createXYLineChart(
					"Run time across datasets", null, "Time in minutes", dataset,
					PlotOrientation.VERTICAL, false, true, false);
		} else {
			barChart = ChartFactory.createXYLineChart(
					null, "Number of triples in Billion", "Time in minutes", dataset,
					PlotOrientation.VERTICAL, true, true, false);
		}
		
		formatXYLineChart(barChart);
		
		File BarChart = new File(chartDir + "/" + Paths.get(Constants.STAT_FILE_TIME_SIZE_CHART).getFileName());
		try {
			ChartUtilities.saveChartAsJPEG(BarChart, barChart, 480, height);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	private void generateTimeInferredTriplesChart(String chartDir){

		final XYSeries spr = new XYSeries("With-Reasoning");
		for (SPDataset ds : sortedDSs){
			if (ds.isInfer() && ds.getExt().equals(Constants.TURTLE_EXT)) spr.add(ds.getTotalInferredTriples()/Math.pow(10, 9), ds.getTimeTakenInferredTriples()/60000);
		}
		
//		final XYSeries spr_nodup = new XYSeries(Constants.DS_TYPE_SP + "-Reasoning-NoDup");
//		for (SPDataset ds : sortedDSs){
//			if (ds.getDsNameForChart().endsWith(Constants.DS_TYPE_NoDup) && ds.isInfer() && ds.getExt().equals(Constants.TURTLE_EXT)) spr_nodup.add(ds.getTotalInferredTriples()/Math.pow(10, 9), ds.getTimeTakenInferredTriples()/60000);
//		}

		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(spr);
//		dataset.addSeries(spr_nodup);

		JFreeChart barChart;
		if (!nodup){
			barChart = ChartFactory.createXYLineChart(
					"Run time for inferred triples", null, "Time in minutes", dataset,
					PlotOrientation.VERTICAL, false, true, false);
		} else {
			barChart = ChartFactory.createXYLineChart(
					null, "Number of inferred triples in Billion", "Time in minutes", dataset,
					PlotOrientation.VERTICAL, true, true, false);
		}
		
		formatXYLineChart(barChart);
		
		File BarChart = new File(chartDir + "/" + Paths.get(Constants.STAT_FILE_TIME_INFERRED_TRIPLES_CHART).getFileName());
		try {
			ChartUtilities.saveChartAsJPEG(BarChart, barChart, 480, height);
		} catch (IOException e) {
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

		JFreeChart barChart;
		if (!nodup){
			barChart = ChartFactory.createXYLineChart(
					"Disk space for inferred triples", null, "Disk space in Gigabyte", dataset,
					PlotOrientation.VERTICAL, false, true, false);
		} else {
			barChart = ChartFactory.createXYLineChart(
					null, "Number of inferred triples in Billion", "Disk space in Gigabyte", dataset,
					PlotOrientation.VERTICAL, true, true, false);
		}
		
		formatXYLineChart(barChart);
		
		File BarChart = new File(chartDir + "/" + Paths.get(Constants.STAT_FILE_DISK_INFERRED_TRIPLES_CHART).getFileName());
		try {
			ChartUtilities.saveChartAsJPEG(BarChart, barChart, width, height);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private void generateTimeChart(String chartDir) {
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (SPDataset ds : sortedDSs){
			if (ds.getTimeTaken().size() > 0 && ds.getExt().equals(Constants.TURTLE_EXT) && !ds.getDsName().startsWith(Constants.DS_TYPE_NoDup)) {
				dataset.addValue(ds.getAverageTimeTaken()/60000, ds.getDsLabel() + "-" + Constants.DS_TYPE_Dup_Full, ds.getDsNameForChart());
				SPDataset nd = allDSs.get(Constants.DS_TYPE_NoDup + ds.getDsName() + ds.getExt() + (ds.isInfer()?Constants.DS_TYPE_SPR:Constants.DS_TYPE_SP));
				if (nd != null) dataset.addValue(nd.getAverageTimeTaken()/60000, ds.getDsLabel() + "-" + Constants.DS_TYPE_NoDup_Full, ds.getDsNameForChart());
			}
		}

		JFreeChart barChart;
		if (!nodup){
			barChart = ChartFactory.createBarChart(
					"Run time: With vs. Without Reasoning", null, "Time in minutes", dataset,
					PlotOrientation.VERTICAL, false, true, false);
		} else {
			barChart = ChartFactory.createBarChart(
					null, "Singleton Property Dataset", "Time in minutes", dataset,
					PlotOrientation.VERTICAL, true, true, false);
		}
		
		formatChart(barChart);
		
		File BarChart = new File(chartDir + "/" + Paths.get(Constants.STAT_FILE_TIME_CHART).getFileName());
		try {
			ChartUtilities.saveChartAsJPEG(BarChart, barChart, width, height);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void generateSingletonChart(String chartDir){
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (SPDataset ds : sortedDSs){
			if (ds.getTimeTaken().size() > 0 && ds.getExt().equals(Constants.TURTLE_EXT) && !ds.getDsName().startsWith(Constants.DS_TYPE_NoDup)) {
				dataset.addValue(ds.getTotalSingletonTriples()/Math.pow(10, 9), ds.getDsLabel() + "-" + Constants.DS_TYPE_Dup_Full, ds.getDsNameForChart());
				SPDataset nd = allDSs.get(Constants.DS_TYPE_NoDup + ds.getDsName() + ds.getExt() + (ds.isInfer()?Constants.DS_TYPE_SPR:Constants.DS_TYPE_SP));
				if (nd != null) dataset.addValue(nd.getTotalSingletonTriples()/Math.pow(10, 9), ds.getDsLabel() + "-" + Constants.DS_TYPE_NoDup_Full, ds.getDsNameForChart());
			}
		}

		JFreeChart barChart;
		if (!nodup){
			barChart = ChartFactory.createBarChart(
					"Singleton Triples: With vs. Without Reasoning", null, "Number of Triples in Billion", dataset,
					PlotOrientation.VERTICAL, false, true, false);
		} else {
			barChart = ChartFactory.createBarChart(
					null, "Singleton Property Dataset", "Number of Triples in Billion", dataset,
					PlotOrientation.VERTICAL, true, true, false);
		}
		
		formatChart(barChart);
		
		File BarChart = new File(chartDir + "/" + Paths.get(Constants.STAT_FILE_SINGLETON_CHART).getFileName());
		try {
			ChartUtilities.saveChartAsJPEG(BarChart, barChart, width, height);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private void generateDiskChart(String chartDir){
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (SPDataset ds : sortedDSs){
			if (ds.getExt().equals(Constants.TURTLE_EXT) && !ds.getDsName().startsWith(Constants.DS_TYPE_NoDup)) {
				dataset.addValue(ds.getDisk()/Math.pow(2, 30), ds.getDsLabel() + "-" + Constants.DS_TYPE_Dup_Full, ds.getDsNameForChart());
				SPDataset nd = allDSs.get(Constants.DS_TYPE_NoDup + ds.getDsName() + ds.getExt() + (ds.isInfer()?Constants.DS_TYPE_SPR:Constants.DS_TYPE_SP));
				if (nd != null) dataset.addValue(nd.getDisk()/Math.pow(2, 30), ds.getDsLabel()+ "-" + Constants.DS_TYPE_NoDup_Full, ds.getDsNameForChart());
			}
		}

		JFreeChart barChart;
		if (!nodup){
			barChart = ChartFactory.createBarChart(
					"Disk Space: With vs. Without Reasoning", null, "Size in Gigabytes", dataset,
					PlotOrientation.VERTICAL, false, true, false);
		} else {
			barChart = ChartFactory.createBarChart(
					null, "Singleton Property Dataset", "Size in Gigabytes", dataset,
					PlotOrientation.VERTICAL, true, true, false);
		}
		
		formatChart(barChart);
		
		File BarChart = new File(chartDir + "/" + Paths.get(Constants.STAT_FILE_DISK_CHART).getFileName());
		try {
			ChartUtilities.saveChartAsJPEG(BarChart, barChart, width, height);
		} catch (IOException e) {
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
				ds.setTotalSingletonTriples(totalSingleton);
				allDSs.put(key, ds);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
