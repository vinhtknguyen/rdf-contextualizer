package org.knoesis.rdf.sp.stat;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.knoesis.rdf.sp.utils.Constants;

public class SPChartGenerator {

	private HashMap<String, SPDataset> allDSs;
	private List<SPDataset> sortedDSs;
	
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
		    sortedDSs.add(pair.getValue());
		    System.out.println(pair.getValue().toString());
		}

		Collections.sort(sortedDSs, new Comparator<SPDataset>(){

			@Override
			public int compare(SPDataset o1, SPDataset o2) {
				// TODO Auto-generated method stub
				return Long.compare(o1.getTotalTriples(), o2.getTotalTriples());
			}
			
		});
		
		try {
			Files.createDirectories(Paths.get(chartDir));
			// Generate charts
			generateDataChart(chartDir);
			generateTimeChart(chartDir);
			generateSingletonChart(chartDir);
			generateDiskChart(chartDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void generateDataChart(String chartDir){
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (SPDataset ds : sortedDSs){
			dataset.addValue(ds.getTotalTriples(), ds.getDsLabel(), ds.getDsName());
		}

		JFreeChart barChart = ChartFactory.createBarChart(
				"Total Triples", "Dataset", "Number of Triples", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		barChart.getPlot().setBackgroundPaint(Color.WHITE);
		barChart.getPlot().setBackgroundImageAlpha((float) 0.5);

		int width = 640; /* Width of the image */
		int height = 480; /* Height of the image */
		File BarChart = new File(chartDir + "/" + Paths.get(Constants.STAT_FILE_DATA_CHART).getFileName());
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
			if (ds.getTimeTaken().size() > 0) dataset.addValue(ds.getAverageTimeTaken()/1000, ds.getDsLabel(), ds.getDsName());
		}

		JFreeChart barChart = ChartFactory.createBarChart(
				"Total Time Taken", "Dataset", "ms", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		barChart.getPlot().setBackgroundPaint(Color.WHITE);
		barChart.getPlot().setBackgroundImageAlpha((float) 0.5);

		int width = 640; /* Width of the image */
		int height = 480; /* Height of the image */
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
			dataset.addValue(ds.getTotalSingletonProps(), ds.getDsLabel(), ds.getDsName());
		}

		JFreeChart barChart = ChartFactory.createBarChart(
				"Total Singleton Triples", "Dataset", "Number of Triples", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		barChart.getPlot().setBackgroundPaint(Color.WHITE);
		barChart.getPlot().setBackgroundImageAlpha((float) 0.5);

		int width = 640; /* Width of the image */
		int height = 480; /* Height of the image */
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
			dataset.addValue(ds.getDisk(), ds.getDsLabel(), ds.getDsName());
		}

		JFreeChart barChart = ChartFactory.createBarChart(
				"Total Space", "Dataset", "Size in bytes", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		barChart.getPlot().setBackgroundPaint(Color.WHITE);
		barChart.getPlot().setBackgroundImageAlpha((float) 0.5);

		int width = 640; /* Width of the image */
		int height = 480; /* Height of the image */
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
