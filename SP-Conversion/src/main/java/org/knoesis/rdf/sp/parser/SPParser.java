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

import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;

public abstract class SPParser {
	
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
	protected int parallel = 1;

	
	public void init(){
		
	}
	
	public SPParser() {
		uuidInitStr = Constants.SP_UUID_PREFIX;
		uuidInitNum = System.currentTimeMillis();
	}

	public SPParser(long _uuidInitNum, String _uuidInitStr) {
		uuidInitStr = _uuidInitStr;
		uuidInitNum = _uuidInitNum;
	}

	public void parseFile(String file, String ext, String rep, String fileout){
		
	}
	
	public void parseDir(String file, String ext, String rep, String fileout){
		if (!Files.isDirectory(Paths.get(file))){
			parseFile(file, ext, rep, RDFWriteUtils.genFileOut(file, ext, this.isZip()));
			
		} else {
			// If the input is a directory
			// Create a new directory for output files
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(file))) {
				String dirOut;
				if (this.infer){
					dirOut = file + (ext.equals(Constants.NTRIPLE_EXT)?Constants.CONVERTED_TO_SP_INF_NT:Constants.CONVERTED_TO_SP_INF_TTL);
				} else {
					dirOut = file + (ext.equals(Constants.NTRIPLE_EXT)?Constants.CONVERTED_TO_SP_NT:Constants.CONVERTED_TO_SP_TTL);
				}
				Files.createDirectories(Paths.get(dirOut));
				
				/* PROCESS EACH INPUT FILE & GENERATE OUTPUT FILE */
				
				for (Path entry : stream) {
					if (!Files.isDirectory(entry.getFileName())){
						String fileOut = dirOut + "/" + RDFWriteUtils.genFileOut(entry.getFileName().toString(), ext, this.isZip());
						System.out.println("File in: " + entry.toString() + " vs. out " + fileOut);
						parseFile(entry.toString(), ext, rep, fileOut);
					} else {
						if (entry.getFileName().toString().toLowerCase().contains(Constants.DATA_DIR)) 
							parseDir(entry.toString(), ext, rep, entry.toString());
					}
		        }
		    } catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	ExecutorService converterExecutor;
	ExecutorService parserExecutor;
	ExecutorService writerExecutor;
	List<Future<Long>> futureList = new ArrayList<Future<Long>>();
	
	public void parse(String file, String ext, String rep){
		long start = System.currentTimeMillis();
		converterExecutor = Executors.newFixedThreadPool(parallel);
		parserExecutor = Executors.newFixedThreadPool(parallel);
		writerExecutor = Executors.newFixedThreadPool(parallel);
		
		/*
		 * If input is a file, parse it
		 * If input is a directory, parse its files and sub-directories named DATA recursively
		 * */
		if (!Files.isDirectory(Paths.get(file))){
			parseFile(file, ext, rep, RDFWriteUtils.genFileOut(file, ext, this.isZip()));
		} else {
			parseDir(file, ext, rep, RDFWriteUtils.genDirOut(file));
		}
		
		try {
    		converterExecutor.shutdown();
    		parserExecutor.shutdown();
    		writerExecutor.shutdown();
    		converterExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.MICROSECONDS);
    		parserExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.MICROSECONDS);
    		writerExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.MICROSECONDS);

    		int size = futureList.size();
        	int count = 0;
        	while (count < size){
	        	for (Future<Long> future:futureList){
        			future.get();
	        		if (future.isDone()) {
	        			count++;
	        		}
	        	}
        	}
        	long end = System.currentTimeMillis();
        	System.out.println("Time processed (ms): " + (end-start) );
        	System.out.println("Done processing." );
        } catch (ExecutionException | InterruptedException ex) {
           ex.getCause().printStackTrace();
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
	
	
}
