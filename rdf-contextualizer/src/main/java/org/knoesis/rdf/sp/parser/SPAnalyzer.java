package org.knoesis.rdf.sp.parser;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.knoesis.rdf.sp.pipeline.PipedQuadTripleIterator;
import org.knoesis.rdf.sp.pipeline.PipedQuadTripleStream;
import org.knoesis.rdf.sp.supplier.SupplierAnalyzer;
import org.knoesis.rdf.sp.supplier.SupplierParser;
import org.knoesis.rdf.sp.supplier.SupplierStreamSplitter;
import org.knoesis.rdf.sp.utils.Constants;

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
		    	num = new Long(pair.getValue().longValue() + num.longValue());
		    } else {
		    	num = new Long(pair.getValue().longValue());
		    }
		    dsGenericCount.put(pair.getKey(),num); 
		}
	}
	
	public static synchronized void putStatElement(String filename, SPDataStatElement element){
		if (stats.get(filename) == null){
			stats.put(filename, element); 
		} else {
			// Merging the results 
			SPDataStatElement existing = stats.get(filename);
			existing.setCountItem(existing.getCountItem() + element.getCountItem());
			existing.setCountSingletonProp(existing.getCountSingletonProp() + element.getCountSingletonProp());
			existing.setDiskspace(existing.getDiskspace() + element.getDiskspace());
			existing.setTotalSingletonInstantiation(existing.getTotalSingletonInstantiation() + element.getTotalSingletonInstantiation());
			existing.setAverageSingsPerGeneric(existing.getAverageSingsPerGeneric() + element.getAverageSingsPerGeneric());
			stats.put(filename, existing);
		}
	}
	
	public void parseDir(String filein, String task){

		if (!Files.isDirectory(Paths.get(filein))){
			parser.manager.put(filein, null, task, reporter.getExt());
		} else {
			// If the input is a directory
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(filein))) {
				for (Path entry : stream) {
					if (!Files.isDirectory(entry)){
						parser.manager.put(entry.toString(), null, task, reporter.getExt());
					} else {
						if (entry.getFileName().toString().toLowerCase().contains(Constants.DATA_DIR)) 
							parseDir(entry.toString(), task);
					}
		        }
		    } catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void analyze(String filein) {
		if (filein == null) return;
		if (!Files.exists(Paths.get(filein))) return;
		long start = System.currentTimeMillis();
		// Regenerate the file list in parser.manager.queues
		parseDir(filein, Constants.PROCESSING_TASK_ANALYZE);
		
		// Already obtained the file names and sizes in the queue, now start processing them
		boolean cont = true;
		boolean doneFiles = false;
		boolean doneTasks = false;
		boolean shutdown = false;
		while (cont){
			if (!parser.manager.hasNext()) doneFiles = true;
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
		    	totalSingInstantiation += pair.getValue().longValue();
			    countGenericProp++;
			    reporter.reportGenericProp(reporter.getDsName(), pair.getKey(), pair.getValue());
		    }
		}

		double average = (countGenericProp > 0)?(double)totalSingInstantiation/countGenericProp:0;
		// Aggregate the results
		reporter.reportDataTotal(start, reporter.getDsName(), dsType, countItem, countSingletonProp, totalSingInstantiation, countGenericProp, average);
		reporter.reportDiskTotal(reporter.getDsName(), dsType,  totalDisk, reporter.getExt());

		reporter.reportFinish(reporter.getDsName(), start);
	}
	
	ExecutorService executor = Executors.newWorkStealingPool();
	
	public synchronized ParserElement updateFinishedTask(ParserElement element1, ParserElement element2){
		if (element1.getFilein().equals(element2.getFilein())){
			element1.updateFinishedTasks(1);
			parser.manager.deregisterNumTasks(1, element1);
			if (element1.isFinished()){
				parser.manager.finishParserElemnet(element1);
				reporter.reportEndStatus(element1);
			}
		}
		return element1;
	}
	
	public void analyzeFile(ParserElement element){
		
		if (element == null) return;
		
	    PipedQuadTripleIterator processorIter = new PipedQuadTripleIterator(element.getBufferStream(), false, 5000, 20);
		final PipedQuadTripleStream processorInputStream = new PipedQuadTripleStream(processorIter);
		
		/* Since we have only one task here to analyze the SP files, 
		 * we increase the number of analyzing tasks 3X
		 * to meet the default number of tasks actually ran for generating the files 
		 * */
		int converters = element.getnConverters();
		
    	CompletableFuture<ParserElement> finished = null;
    	
    	if (converters == 1){
    		CompletableFuture<ParserElement> parser = CompletableFuture.supplyAsync(new SupplierParser(processorInputStream, element, reporter), executor);
			CompletableFuture<ParserElement> analyzer = CompletableFuture.supplyAsync(new SupplierAnalyzer(processorIter, element, reporter), executor);

    		finished = parser.thenCombineAsync(analyzer, this::updateFinishedTask);
    		
    	} else {
        	List<PipedQuadTripleStream> subProcessorStreams = new ArrayList<PipedQuadTripleStream>();
        	List<PipedQuadTripleIterator> subProcessorIters = new ArrayList<PipedQuadTripleIterator>();
        	for (int i = 0; i < converters; i++){
        		PipedQuadTripleIterator subProcessorIter = new  PipedQuadTripleIterator(element.getBufferStream(), false, 5000, 20);
        		PipedQuadTripleStream subProcessorStream = new PipedQuadTripleStream(subProcessorIter);
        		subProcessorStreams.add(subProcessorStream);
        		subProcessorIters.add(subProcessorIter);
        	}
    		// Start the parser
    		CompletableFuture<ParserElement> parser = CompletableFuture.supplyAsync(new SupplierParser(processorInputStream, element, reporter), executor);
    		CompletableFuture<ParserElement> splitter = CompletableFuture.supplyAsync(new SupplierStreamSplitter(processorIter, subProcessorStreams, element, reporter), executor);

    		CompletableFuture<ParserElement> combiner = parser.thenCombineAsync(splitter, this::updateFinishedTask);
    		/* Since we have only one task here to analyze the SP files, 
    		 * we increase the number of analyzing tasks 3X
    		 * to meet the number of tasks actually ran for generating the files 
    		 * */
    		for (int i = 0; i < converters; i++){
        		
    			CompletableFuture<ParserElement> analyzer = CompletableFuture.supplyAsync(new SupplierAnalyzer(subProcessorIters.get(i), element, reporter), executor);

    			finished = combiner.thenCombineAsync(analyzer, this::updateFinishedTask);
        		
        	}
    	}
		
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
