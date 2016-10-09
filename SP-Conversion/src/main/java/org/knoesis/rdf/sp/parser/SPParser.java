package org.knoesis.rdf.sp.parser;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.knoesis.rdf.sp.callable.CallableConverter;
import org.knoesis.rdf.sp.callable.CallableParser;
import org.knoesis.rdf.sp.callable.CallableStreamSplitter;
import org.knoesis.rdf.sp.callable.CallableTransformer;
import org.knoesis.rdf.sp.callable.CallableWriter;
import org.knoesis.rdf.sp.callable.SPProcessor;
import org.knoesis.rdf.sp.concurrent.PipedNodesIterator;
import org.knoesis.rdf.sp.concurrent.PipedNodesStream;
import org.knoesis.rdf.sp.concurrent.PipedQuadTripleIterator;
import org.knoesis.rdf.sp.concurrent.PipedQuadTripleStream;
import org.knoesis.rdf.sp.concurrent.PipedSPTripleIterator;
import org.knoesis.rdf.sp.concurrent.PipedSPTripleStream;
import org.knoesis.rdf.sp.model.SPModel;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;
import org.knoesis.rdf.sp.utils.Reporter;

public class SPParser {
	
	protected boolean infer = false;
	protected boolean zip = false;
	protected String ontoDir;
	protected String rep;
	protected String dsName = null;
	protected String uuidInitStr = null;
	protected long uuidInitNum;
	protected String prefix = null;
	protected String ext = null;
	protected boolean shortenURI = true;
	protected int parallel = 3;
	protected int bufferSizeStream = Constants.BUFFER_SIZE_STREAM;
	protected int bufferSizeWriter = Constants.BUFFER_SIZE_WRITER;

	
	public SPParser(String rep) {
		this.rep = rep;
		uuidInitStr = Constants.SP_UUID_PREFIX;
		uuidInitNum = System.currentTimeMillis();
	}

	public SPParser(String rep, long _uuidInitNum, String _uuidInitStr) {
		this.rep = rep;
		uuidInitStr = _uuidInitStr;
		uuidInitNum = _uuidInitNum;
	}

	public void parseFile(String filein, String ext, String rep, String fileout){
		// Check the condition before submitting new tasks
        while (!canSubmitTask(futureParserList)) {
        	// Wait until the parser and writer of the same task finish, then start a new task
//        	checkFutures(futureConverterList);
        }

        PipedQuadTripleIterator processorIter = new PipedQuadTripleIterator(bufferSizeStream, false);
		final PipedQuadTripleStream processorInputStream = new PipedQuadTripleStream(processorIter);

		final String filename = RDFWriteUtils.getPrettyName(filein);

		Reporter reporter = new Reporter(rep, infer, ext, filename, filein, fileout, dsName, this.shortenURI, zip, bufferSizeWriter, uuidInitNum, uuidInitStr, ontoDir);
		
    	int round = findConverterNum(filein);
    	
    	if (round == 1){
    		// One thread for each task
    		CallableParser<Object> parser = new CallableParser<Object>(processorInputStream, reporter);
        	futureParserList.add(parserExecutor.submit(parser));

        	PipedSPTripleIterator converterIter = new PipedSPTripleIterator(bufferSizeStream, false);
    		PipedSPTripleStream converterInputStream = new PipedSPTripleStream(converterIter);
    		PipedNodesIterator transformerIter = new PipedNodesIterator(bufferSizeStream, false);
    		PipedNodesStream transformerInputStream = new PipedNodesStream(transformerIter);

    		CallableConverter<Object> converter = new CallableConverter<Object>(processorIter, converterInputStream, reporter);
            CallableTransformer transformer = new CallableTransformer(transformerInputStream, converterIter, reporter);
            CallableWriter writer = new CallableWriter(transformerIter, reporter);
            
    		futureConverterList.add(converterExecutor.submit(converter));
    		futureConverterList.add(converterExecutor.submit(transformer));
    		futureConverterList.add(converterExecutor.submit(writer));
   		
    	} else {
    		// Split the input stream into N sub-stream with N = round
        	List<PipedQuadTripleStream> subProcessorStreams = new ArrayList<PipedQuadTripleStream>();
        	List<PipedQuadTripleIterator> subProcessorIters = new ArrayList<PipedQuadTripleIterator>();
        	for (int i = 0; i < round - 1; i++){
        		PipedQuadTripleIterator subProcessorIter = new  PipedQuadTripleIterator(bufferSizeStream, false);
        		PipedQuadTripleStream subProcessorStream = new PipedQuadTripleStream(subProcessorIter);
        		subProcessorStreams.add(subProcessorStream);
        		subProcessorIters.add(subProcessorIter);
        	}
    		// Start the parser
    		CallableParser<Object> parser = new CallableParser<Object>(processorInputStream, reporter);
        	futureParserList.add(parserExecutor.submit(parser));
        	
        	CallableStreamSplitter splitter = new CallableStreamSplitter(processorIter, subProcessorStreams, reporter);
        	futureConverterList.add(converterExecutor.submit(splitter));
        	
        	// For each stream, create a set of tasks with the sub-stream input
        	for (int i = 0; i < round - 1; i++){
        		
                PipedSPTripleIterator converterIter = new PipedSPTripleIterator(bufferSizeStream, false);
        		PipedSPTripleStream converterInputStream = new PipedSPTripleStream(converterIter);
        		PipedNodesIterator transformerIter = new PipedNodesIterator(bufferSizeStream, false);
        		PipedNodesStream transformerInputStream = new PipedNodesStream(transformerIter);

        		CallableConverter<Object> converter = new CallableConverter<Object>(subProcessorIters.get(i), converterInputStream, reporter);
                CallableTransformer transformer = new CallableTransformer(transformerInputStream, converterIter, reporter);
                CallableWriter writer = new CallableWriter(transformerIter, reporter);
                
        		futureConverterList.add(converterExecutor.submit(converter));
        		futureConverterList.add(converterExecutor.submit(transformer));
        		futureConverterList.add(converterExecutor.submit(writer));
        	}
    		
    	}

	}
	public int findConverterNum(String filein){
        boolean zipfile = filein.endsWith(".gz");
        int num = 1;
		if (zipfile){ 
			if (Paths.get(filein).toFile().length() > Constants.FILE_ZIP_SIZE_MEDIUM){
				num = 2;
			}
			if (Paths.get(filein).toFile().length() > Constants.FILE_ZIP_SIZE_LARGE){
				num = 3;
			}
		} else if (Paths.get(filein).toFile().length() > Constants.FILE_REGULAR_SIZE_LARGE){
			num = 3;
		}
		return num;

	}

	public void parseDir(String filein, String ext, String rep, String fileout){

		if (!Files.isDirectory(Paths.get(filein))){
			parseFile(filein, ext, rep, RDFWriteUtils.genFileOut(filein, ext, this.isZip()));
		} else {
			// If the input is a directory
			// Create a new directory for output files
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(filein))) {
				String dirOut;
				if (this.infer){
					dirOut = fileout + (ext.equals(Constants.NTRIPLE_EXT)?Constants.CONVERTED_TO_SP_INF_NT:Constants.CONVERTED_TO_SP_INF_TTL);
				} else {
					dirOut = fileout + (ext.equals(Constants.NTRIPLE_EXT)?Constants.CONVERTED_TO_SP_NT:Constants.CONVERTED_TO_SP_TTL);
				}
				Files.createDirectories(Paths.get(dirOut));
				/* PROCESS EACH INPUT FILE & GENERATE OUTPUT FILE */
				
				for (Path entry : stream) {
					if (!Files.isDirectory(entry)){
						String fileOut = dirOut + "/" + RDFWriteUtils.genFileOut(entry.getFileName().toString(), ext, this.isZip());
						System.out.println("File in: " + entry.toString() + " vs. out " + fileOut);
						parseFile(entry.toString(), ext, rep, fileOut);
					} else {
						if (entry.getFileName().toString().toLowerCase().contains(Constants.DATA_DIR)) 
							parseDir(entry.toString(), ext, rep, dirOut + "/" + RDFWriteUtils.genDirOut(entry.getFileName().toString()));
					}
		        }
		    } catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	ExecutorService converterExecutor;
	ExecutorService parserExecutor;
	List<Future<String>> futureConverterList = new ArrayList<Future<String>>();
	List<Future<String>> futureParserList = new ArrayList<Future<String>>();

	public void parse(String file, String ext, String rep){
		long start = System.currentTimeMillis();
		parserExecutor = Executors.newFixedThreadPool(parallel);
		converterExecutor = Executors.newWorkStealingPool();

		if (this.infer){
			System.out.println("Loading ontologies from " + this.getOntoDir());
			SPModel.loadModel(this.getOntoDir());
			System.out.println("Done loading ontologies.");
		}
		if (this.prefix != null) {
			System.out.println("Loading prefixes ..." + prefix);
			RDFWriteUtils.loadPrefixesToTrie(RDFWriteUtils.trie, this.prefix);
			System.out.println("Done loading prefixes.");
		}
		/*
		 * If input is a file, parse it
		 * If input is a directory, parse its files and sub-directories named DATA recursively
		 * */
		if (!Files.isDirectory(Paths.get(file))){
			parseFile(file, ext, rep, RDFWriteUtils.genFileOut(file, ext, this.isZip()));
		} else {
			System.out.println("Directory in: " + file);
			parseDir(file, ext, rep, RDFWriteUtils.genDirOut(file));
		}
		
		try {

        	boolean isDone = false;
        	while (!isDone){
	        	for (Future<String> future:futureParserList){
	        		if (future.isDone()) {
	        			future.get();
	        		}
	        	}
	        	for (Future<String> future:futureConverterList){
	        		if (future.isDone()) {
	        			future.get();
	        		}
	        	}
	        	isDone = true;
        	}
        	
    		converterExecutor.shutdown();
    		parserExecutor.shutdown();
    		converterExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.MICROSECONDS);
    		parserExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.MICROSECONDS);

    		long end = System.currentTimeMillis();
        	System.out.println("Time processed (ms): " + (end-start) );
        	System.out.println("Done processing." );
        } catch (InterruptedException | ExecutionException ex) {
           ex.getCause().printStackTrace();
        }

	}
	
	public boolean canSubmitTask(List<Future<String>> futures){
		// Guarantee no more tasks submitted after encountering a big file
		String filename = null;
		if (futures.size() < parallel) return true;
		try {
			for (int i = 0; i < futures.size(); i++){
				Future<String> future = futures.get(i);
				if (future.isDone()) {
					futures.remove(i); 
					filename = future.get();
					System.out.println(filename + ": done processing!");
					return true;
				}
			}
			if (futures.size() < parallel) return true;
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			System.out.println("Error while reading file: " + filename + ":" + e.getCause().toString());
			e.getCause().printStackTrace();
		}
		return false;
	}
	
	public void checkFutures(List<Future<String>> futures){
		try {
			for (int i = 0; i < futures.size(); i++){
				Future<String> future = futures.get(i);
					if (future.isDone()) {
						futures.remove(i);
						future.get();
					}
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.getCause().printStackTrace();
		}
		
	}
	
	public void waitForFutureTask(Future<String> future){
		boolean isDone = false;
		while (!isDone){
			if (future.isDone()){
				try {
					isDone = true;
					future.get();
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean isInfer() {
		return infer;
	}

	public void setInfer(boolean infer) {
		this.infer = infer;
	}

	public String getOntoDir() {
		return ontoDir;
	}

	public void setOntoDir(String ontoDir) {
		this.ontoDir = ontoDir;
	}

	public boolean isZip() {
		return zip;
	}

	public void setZip(boolean zip) {
		this.zip = zip;
	}

	public String getRep() {
		return rep;
	}

	public void setRep(String rep) {
		this.rep = rep;
	}

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	public String getUuidInitStr() {
		return uuidInitStr;
	}

	public void setUuidInitStr(String uuidInitStr) {
		this.uuidInitStr = uuidInitStr;
	}

	public long getUuidInitNum() {
		return uuidInitNum;
	}

	public void setUuidInitNum(long uuidInitNum) {
		this.uuidInitNum = uuidInitNum;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public boolean isShortenURI() {
		return shortenURI;
	}

	public void setShortenURI(boolean shortenURI) {
		this.shortenURI = shortenURI;
	}

	public int getParallel() {
		return parallel;
	}

	public void setParallel(int parallel) {
		this.parallel = parallel;
	}

	public int getBufferSizeStream() {
		return bufferSizeStream;
	}

	public void setBufferSizeStream(int bufferSizeStream) {
		this.bufferSizeStream = bufferSizeStream;
	}

	public int getBufferSizeWriter() {
		return bufferSizeWriter;
	}

	public void setBufferSizeWriter(int bufferSizeWriter) {
		this.bufferSizeWriter = bufferSizeWriter;
	}
	
	
}
