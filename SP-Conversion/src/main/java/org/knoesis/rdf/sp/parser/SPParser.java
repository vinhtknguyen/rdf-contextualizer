package org.knoesis.rdf.sp.parser;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.knoesis.rdf.sp.model.SPModel;
import org.knoesis.rdf.sp.pipeline.PipedNodesIterator;
import org.knoesis.rdf.sp.pipeline.PipedNodesStream;
import org.knoesis.rdf.sp.pipeline.PipedQuadTripleIterator;
import org.knoesis.rdf.sp.pipeline.PipedQuadTripleStream;
import org.knoesis.rdf.sp.pipeline.PipedSPTripleIterator;
import org.knoesis.rdf.sp.pipeline.PipedSPTripleStream;
import org.knoesis.rdf.sp.supplier.SupplierConverter;
import org.knoesis.rdf.sp.supplier.SupplierParser;
import org.knoesis.rdf.sp.supplier.SupplierStreamSplitter;
import org.knoesis.rdf.sp.supplier.SupplierTransformer;
import org.knoesis.rdf.sp.supplier.SupplierWriter;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;
import org.knoesis.rdf.sp.exception.*;


public class SPParser {
	
	Reporter reporter;
	ResourceManager manager;
	ExecutorService executor;

	public SPParser(Reporter _reporter) {
		reporter = _reporter;
		manager = new ResourceManager(reporter.getRatio(), Constants.PROCESSING_TASK_GENERATE);
		manager.setParallel(reporter.getParallel());
		executor = Executors.newWorkStealingPool();
	}

	public void parseFile(ParserElement element){
		// Check the condition before submitting new tasks
		if (element == null) return;
		
        PipedQuadTripleIterator processorIter = new PipedQuadTripleIterator(element.getBufferStream(), false, 5000, 20);
		final PipedQuadTripleStream processorInputStream = new PipedQuadTripleStream(processorIter);

    	int converters = element.getnConverters();
    	
		CompletableFuture<ParserElement> finishedElement = null;
		if (converters == 1){
    		// One thread for each task
    		CompletableFuture<ParserElement> parserCompletableFuture = CompletableFuture.supplyAsync(new SupplierParser(processorInputStream, element, reporter), executor);
    		
        	PipedSPTripleIterator converterIter = new PipedSPTripleIterator(element.getBufferStream(), false, 5000, 20);
    		PipedSPTripleStream converterInputStream = new PipedSPTripleStream(converterIter);
    		PipedNodesIterator transformerIter = new PipedNodesIterator(element.getBufferStream(), false, 5000, 20);
    		PipedNodesStream transformerInputStream = new PipedNodesStream(transformerIter);

    		CompletableFuture<ParserElement> converter = CompletableFuture.supplyAsync(new SupplierConverter(processorIter, converterInputStream, element, reporter), executor);
    		CompletableFuture<ParserElement> transformer = CompletableFuture.supplyAsync(new SupplierTransformer(transformerInputStream, converterIter, element, reporter), executor);
    		CompletableFuture<ParserElement> writer = CompletableFuture.supplyAsync(new SupplierWriter(transformerIter, element, reporter, -1), executor);
   		
    		finishedElement = parserCompletableFuture.thenCombineAsync(converter, this::updateFinishedTasks)
    								.thenCombineAsync(transformer, this::updateFinishedTasks)
    								.thenCombineAsync(writer, this::updateFinishedTasks);
    	} else {
    		// Split the input stream into N sub-stream with N = round
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

    		CompletableFuture<ParserElement> combiner = parser.thenCombineAsync(splitter, this::updateFinishedTasks);

        	// For each stream, create a set of tasks with the sub-stream input
        	for (int i = 0; i < converters; i++){
        		
                PipedSPTripleIterator converterIter = new PipedSPTripleIterator(element.getBufferStreamSubStream(), false, 5000, 20);
        		PipedSPTripleStream converterInputStream = new PipedSPTripleStream(converterIter);
        		PipedNodesIterator transformerIter = new PipedNodesIterator(element.getBufferStreamSubStream(), false, 5000, 20);
        		PipedNodesStream transformerInputStream = new PipedNodesStream(transformerIter);

        		CompletableFuture<ParserElement> converter = CompletableFuture.supplyAsync(new SupplierConverter(subProcessorIters.get(i), converterInputStream, element, reporter), executor);
        		CompletableFuture<ParserElement> transformer = CompletableFuture.supplyAsync(new SupplierTransformer(transformerInputStream, converterIter, element, reporter), executor);
               	CompletableFuture<ParserElement> writer = CompletableFuture.supplyAsync(new SupplierWriter(transformerIter, element, reporter, i), executor);
               	
        		finishedElement = combiner.thenCombineAsync(converter, this::updateFinishedTasks)
						.thenCombineAsync(transformer, this::updateFinishedTasks)
						.thenCombineAsync(writer, this::updateFinishedTasks);
        		
        	}
    	}
		
		finishedElement.handleAsync((ok, ex) ->{
			Runtime.getRuntime().gc();
			if (ok != null){
				System.out.println("File " + element.getFilein() + ": done processing.");
			} else {
				this.updateCancelledTasks(element);
				throw new SPException(ex);
			}
			return ok;
		});
		
	}

	public void parseDir(String filein, String fileout){

		if (!Files.isDirectory(Paths.get(filein))){
			
			manager.put(filein, fileout);
//			parseFile(filein, RDFWriteUtils.genFileOut(filein, reporter.getExt(), reporter.isZip()));
			
		} else {
			// If the input is a directory
			// Create a new directory for output files
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(filein))) {
				
				Files.createDirectories(Paths.get(fileout));
				/* PROCESS EACH INPUT FILE & GENERATE OUTPUT FILE */
				
				for (Path entry : stream) {
					if (!Files.isDirectory(entry)){
						
						String fileOut = fileout + "/" + RDFWriteUtils.genFileOut(entry.getFileName().toString(), reporter.getExt(), reporter.isZip());
//						System.out.println("File in: " + entry.toString() + " vs. out " + fileOut);
						
						manager.put(entry.toString(), fileOut);
//						parseFile(entry.toString(), fileOut);
						
					} else {
						
						if (entry.getFileName().toString().toLowerCase().contains(Constants.DATA_DIR)) {
							String dirOut;
							if (reporter.isInfer()){
								dirOut = fileout + "/" + entry.getFileName().toString();
							} else {
								dirOut = fileout + "/" + entry.getFileName().toString();
							}
							parseDir(entry.toString(), dirOut );
						}
					}
		        }
		    } catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void genFileList(String filein){
		/*
		 * If input is a file, parse it
		 * If input is a directory, parse its files and sub-directories named DATA recursively
		 * */
		String fileout;
		if (!Files.isDirectory(Paths.get(filein))){
			//parseFile(filein, RDFWriteUtils.genFileOut(filein, ext, reporter.isZip()));
			fileout = RDFWriteUtils.genFileOut(filein, reporter.getExt(), reporter.isZip());
			manager.put(filein, fileout);
		} else {
			System.out.println("Directory in: " + filein);
			fileout = RDFWriteUtils.genDirOut(filein);
			if (reporter.isInfer()){
				fileout += (reporter.getExt().equals(Constants.NTRIPLE_EXT)?Constants.CONVERTED_TO_SP_INF_NT:Constants.CONVERTED_TO_SP_INF_TTL);
			} else {
				fileout += (reporter.getExt().equals(Constants.NTRIPLE_EXT)?Constants.CONVERTED_TO_SP_NT:Constants.CONVERTED_TO_SP_TTL);
			}
			parseDir(filein, fileout);
		}
		reporter.setFilein(filein);
		reporter.setFileout(fileout);
		
	}
	
	public void parse(String filein, String ext, String rep){
		
		reporter.setExt(ext);
		reporter.setRep(rep);

		long start = System.currentTimeMillis();

		if (reporter.isInfer()){
			System.out.println("Loading ontologies from " + reporter.getOntoDir());
			SPModel.loadModel(reporter.getOntoDir());
			System.out.println("Done loading ontologies.");
		}
		if (reporter.getPrefix() != null) {
			System.out.println("Loading prefixes ..." + reporter.getPrefix());
			RDFWriteUtils.loadPrefixesToTrie(RDFWriteUtils.trie, reporter.getPrefix());
			System.out.println("Done loading prefixes.");
		}
		
		genFileList(filein);
		System.out.println("Files to be processed:");
		manager.printParserElements();
		
		// Already obtained the file names and sizes in the queue, now start processing them
		boolean cont = true;
		boolean doneFiles = false;
		boolean doneTasks = false;
		boolean shutdown = false;
		while (cont){
			if (!doneFiles){
				if (manager.canExecuteNextElement()){
					ParserElement element = manager.next();
					manager.startParserElement(element);
					parseFile(element);
					if (!manager.hasNext()) doneFiles = true;
				} 
			}
			if (doneFiles){
				if (manager.freeResources()) doneTasks = true;
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
		reporter.reportSystemTotal(start, reporter.getDsName(), dsType, reporter.getExt());
		reporter.reportFinish(start);
		// Shutdown the executor pool
		return;
	}
	
	public synchronized ParserElement updateFinishedTasks(ParserElement element1, ParserElement element2){
		if (element1.getFilein().equals(element2.getFilein())){
			element1.updateFinishedTasks(1);
			manager.deregisterNumTasks(1, element1);
			if (element1.isFinished()){
				manager.finishParserElemnet(element1);
				reporter.reportEndStatus(element1);
			}
		}
		return element1;
	}

	public synchronized ParserElement updateCancelledTasks(ParserElement element1){
		if (element1 != null){
			manager.deregisterNumTasks(element1.getnTasksDefault()-1, element1);
			manager.finishParserElemnet(element1);
			reporter.reportEndStatus(element1);
		}
		return element1;
	}


	public ResourceManager getManager() {
		return manager;
	}

	public void setManager(ResourceManager manager) {
		this.manager = manager;
	}

	public Reporter getReporter() {
		return reporter;
	}

	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
	
}
