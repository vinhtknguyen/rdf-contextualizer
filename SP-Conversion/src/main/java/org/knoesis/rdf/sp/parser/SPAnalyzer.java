package org.knoesis.rdf.sp.parser;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.knoesis.rdf.sp.exception.SPException;
import org.knoesis.rdf.sp.pipeline.PipedQuadTripleIterator;
import org.knoesis.rdf.sp.pipeline.PipedQuadTripleStream;
import org.knoesis.rdf.sp.supplier.SupplierAnalyzer;
import org.knoesis.rdf.sp.supplier.SupplierParser;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;

import com.romix.scala.collection.concurrent.TrieMap;

public class SPAnalyzer {

	Reporter reporter;
	SPParser parser;
	
	public static TrieMap<String,Long> dsGenericCount = new TrieMap<String,Long>();
	
	// <filein, statElement>
	public static TrieMap<String, SPDataStatElement> stats = new TrieMap<String,SPDataStatElement>();
	
	public SPAnalyzer(Reporter _reporter) {
		super();
		reporter = _reporter;
		parser = new SPParser(_reporter);
		parser.getManager().setTask(Constants.PROCESSING_TASK_ANALYZE);
	}
	
	public static synchronized void mergeGenericProp(TrieMap<String,Long>  map){
		Iterator<Entry<String, Long>> it = map.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry<String,Long> pair = (Map.Entry<String,Long>)it.next();
			// Copy generic props to the accumulator
		    Long num = dsGenericCount.get(pair.getKey());
		    if (num != null){
		    	num += pair.getValue();
		    } else {
		    	num = new Long(1);
		    }
		    dsGenericCount.put(pair.getKey(),num); 
		}
	}
	
	public static synchronized void putStatElement(String filename, SPDataStatElement element){
		stats.put(filename, element); 
	}
	
	public void parseDir(String filein){

		if (!Files.isDirectory(Paths.get(filein))){
			parser.manager.put(filein, null);
		} else {
			// If the input is a directory
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(filein))) {
				for (Path entry : stream) {
					if (!Files.isDirectory(entry)){
						parser.manager.put(entry.toString(), null);
					} else {
						if (entry.getFileName().toString().toLowerCase().contains(Constants.DATA_DIR)) 
							parseDir(entry.toString());
					}
		        }
		    } catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void genFileList(String filein, boolean generated){
		/*
		 * If input is a file, parse it
		 * If input is a directory, parse its files and sub-directories named DATA recursively
		 * */
		/*
		 * If input is a file, parse it
		 * If input is a directory, parse its files and sub-directories named DATA recursively
		 * */
		if (generated){
			String fileout;
			if (!Files.isDirectory(Paths.get(filein))){
				//parseFile(filein, RDFWriteUtils.genFileOut(filein, ext, reporter.isZip()));
				fileout = RDFWriteUtils.genFileOut(filein, reporter.getExt(), reporter.isZip());
				parser.manager.put(fileout, null);
			} else {
				fileout = RDFWriteUtils.genDirOut(filein);
				System.out.println("Directory in: " + fileout);
				if (reporter.isInfer()){
					fileout += (reporter.getExt().equals(Constants.NTRIPLE_EXT)?Constants.CONVERTED_TO_SP_INF_NT:Constants.CONVERTED_TO_SP_INF_TTL);
				} else {
					fileout += (reporter.getExt().equals(Constants.NTRIPLE_EXT)?Constants.CONVERTED_TO_SP_NT:Constants.CONVERTED_TO_SP_TTL);
				}
				parseDir(fileout);
			}
			reporter.setFilein(fileout);
			
		} else {
			
			if (!Files.isDirectory(Paths.get(filein))){
				parser.manager.put(filein, null);
			} else {
				parseDir(filein);
			}
			reporter.setFilein(filein);
		}
		
	}


	public void analyze(String filein, boolean generated) {
		long start = System.currentTimeMillis();
		// Regenerate the file list in parser.manager.queues
		genFileList(filein, generated);
		
		// Already obtained the file names and sizes in the queue, now start processing them
		boolean cont = true;
		boolean doneFiles = false;
		boolean doneTasks = false;
		boolean shutdown = false;
		while (cont){
			if (!doneFiles){
				if (parser.manager.canExecuteNextElement()){
					ParserElement element = parser.manager.next();
					if (element!= null) {
						parser.manager.startParserElement(element);
						analyzeFile(element);
					}
					if (!parser.manager.hasNext()) doneFiles = true;
				} 
			}
			if (doneFiles){
				if (parser.manager.freeResources()) doneTasks = true;
				if (!shutdown) {
					shutdown = true;
					executor.shutdown();
					try {
						executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.NANOSECONDS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if (doneFiles && doneTasks){
				cont = false;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String dsType = reporter.isInfer()?Constants.DS_TYPE_SPR:Constants.DS_TYPE_SP;
		long countItem = 0;
		long countSingletonProp = 0;
		long countGenericProp = 0;
		long totalSingInstantiation = 0;
		long totalDisk = 0;
		
		Iterator<Entry<String, SPDataStatElement>> it = stats.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry<String,SPDataStatElement> pair = (Map.Entry<String,SPDataStatElement>)it.next();
		    countSingletonProp += pair.getValue().getCountSingletonProp();
		    System.out.println(pair.getKey() + ": count item is " + pair.getValue().getCountItem());
		    countItem += pair.getValue().getCountItem();
		    totalDisk += pair.getValue().getDiskspace();
		}
	    System.out.println(reporter.getDsName() + ": count item is " + countItem);

		Iterator<Entry<String, Long>> iter = dsGenericCount.entrySet().iterator();
		while (iter.hasNext()) {
		    Map.Entry<String,Long> pair = (Map.Entry<String,Long>)iter.next();
		    if (pair.getValue() != null){
		    	totalSingInstantiation += (long) pair.getValue();
			    countGenericProp++;
			    reporter.reportGenericProp(reporter.getDsName(), pair.getKey(), pair.getValue());
		    }
		}

		double average = (countGenericProp > 0)?(double)totalSingInstantiation/countGenericProp:0;
		// Aggregate the results
		reporter.reportDataTotal(start, reporter.getDsName(), dsType, countItem, countSingletonProp, totalSingInstantiation, countGenericProp, average);
		reporter.reportDiskTotal(reporter.getDsName(), dsType,  totalDisk, reporter.getExt());

		reporter.reportFinish(start);
	}
	
	ExecutorService executor = Executors.newWorkStealingPool();
	
	public synchronized ParserElement updateFinishedTask(ParserElement element1, ParserElement element2){
		if (element1.getFilein().equals(element2.getFilein())){
			parser.manager.deregisterNumTasks(1, element1);
			parser.manager.finishParserElemnet(element1);
			reporter.reportEndStatus(element1);
		}
		return element1;
	}
	
	public void analyzeFile(ParserElement element){
		
	    PipedQuadTripleIterator processorIter = new PipedQuadTripleIterator(element.getBufferStream(), false, 5000, 20);
		final PipedQuadTripleStream processorInputStream = new PipedQuadTripleStream(processorIter);
		CompletableFuture<ParserElement> parserCompletableFuture = CompletableFuture.supplyAsync(new SupplierParser(processorInputStream, element, reporter), executor);
		CompletableFuture<ParserElement> analyzerCompletableFuture = CompletableFuture.supplyAsync(new SupplierAnalyzer(processorIter, element, reporter), executor);
		CompletableFuture<ParserElement> finished = parserCompletableFuture.thenCombineAsync(analyzerCompletableFuture, this::updateFinishedTask);
		
		
		finished.handleAsync((ok, ex) ->{
			Runtime.getRuntime().gc();
			if (ok != null){
				System.out.println("File " + element.getFilein() + ": done processing.");
			} else {
				System.out.println("File " + element.getFilein() + ": getting error during processing.");
				ex.getCause().printStackTrace();
				this.updateCancelledTasks(element);
			}
			return ok;
		});
		

	}
	
	public synchronized ParserElement updateCancelledTasks(ParserElement element1){
		if (element1 != null){
			parser.manager.deregisterNumTasks(1, element1);
			parser.manager.finishParserElemnet(element1);
			reporter.reportEndStatus(element1);
		}
		return element1;
	}

	public Reporter getReporter() {
		return reporter;
	}

	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}

}
