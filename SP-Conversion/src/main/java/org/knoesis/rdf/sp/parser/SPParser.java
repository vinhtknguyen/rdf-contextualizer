package org.knoesis.rdf.sp.parser;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
	protected boolean shortenURI = false;

	
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

	public void parseFile(String file, String ext, String rep, String dir){
		
	}
	
	ExecutorService producerExecutor;
	
	public void parse(String file, String ext, String rep){
		producerExecutor = Executors.newWorkStealingPool();
		// If the input is a file
		if (!Files.isDirectory(Paths.get(file))){
			parseFile(file, ext, rep, null);
			
		} else {
			// If the input is a directory
			// Create a new directory for output files
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(file))) {
				String dirOut = file + (ext.equals(Constants.NTRIPLE_EXT)?Constants.CONVERTED_TO_SP_NT:Constants.CONVERTED_TO_SP_TTL);
				Files.createDirectories(Paths.get(dirOut));
				
				/* PROCESS EACH INPUT FILE & GENERATE OUTPUT FILE */
				
				for (Path entry : stream) {
					String fileOut = dirOut + "/" + RDFWriteUtils.genFileOut(entry.getFileName().toString(), ext, this.isZip());
					System.out.println("File in: " + entry.toString() + " vs. out " + fileOut);
					parseFile(entry.toString(), ext, rep, fileOut);
		        }
				
				// Close the pool
				producerExecutor.shutdown();
				producerExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
				System.out.println("Done processing.");
				
		    } catch (IOException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	
	
}
