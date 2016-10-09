package org.knoesis.rdf.sp.utils;

import java.nio.file.Paths;

public class Reporter {
	long start;
	String rep;
	boolean infer;
	String ext;
	String filename;
	String fileout;
	String filein;
	String dsName;
	String step;
	boolean shortenURI;
	boolean zip;
	int bufferSizeWriter;
	long uuidInitNum;
	String uuidInitStr;
	String ontoDir;
	
	public Reporter(String rep, boolean infer, String ext,
			String filename, String filein ,String fileout,
			String dsName, boolean shortenURI, boolean zip, int bufferSizeWriter, long uuidInitNum, String uuidInitStr, String ontoDir) {
		super();
		this.rep = rep;
		this.infer = infer;
		this.ext = ext;
		this.filename = filename;
		this.filein = filein;
		this.fileout = fileout;
		this.dsName = dsName;
		this.shortenURI = shortenURI;
		this.zip = zip;
		this.bufferSizeWriter = bufferSizeWriter;
		this.ontoDir = ontoDir;
		this.uuidInitNum = uuidInitNum;
		this.uuidInitStr = uuidInitStr;
	}
	
	public void reportSystem(long start, String step){
		this.step = step;
		long end = System.currentTimeMillis();
	    System.out.println(filename + ": pthread " + Thread.currentThread().getId() + " " + step + " in " + (end-start) + " ms.");
		SPStats.reportSystem(start, end, rep, (infer?Constants.OPTIONS_INFER:Constants.OPTIONS_NO_INFER), ext, filename, Paths.get(filein).toFile().length(), Paths.get(fileout).toFile().length(), fileout, dsName, step);
	}
	
	public void reportStartStatus(String step){
	    System.out.println(filename + ": pthread " + Thread.currentThread().getId() + " started " + step);
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public String getRep() {
		return rep;
	}

	public void setRep(String rep) {
		this.rep = rep;
	}

	public boolean isInfer() {
		return infer;
	}

	public void setInfer(boolean infer) {
		this.infer = infer;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFileout() {
		return fileout;
	}

	public void setFileout(String fileout) {
		this.fileout = fileout;
	}

	public String getFilein() {
		return filein;
	}

	public void setFilein(String filein) {
		this.filein = filein;
	}

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public boolean isShortenURI() {
		return shortenURI;
	}

	public void setShortenURI(boolean shortenURI) {
		this.shortenURI = shortenURI;
	}

	public boolean isZip() {
		return zip;
	}

	public void setZip(boolean zip) {
		this.zip = zip;
	}

	public int getBufferSizeWriter() {
		return bufferSizeWriter;
	}

	public void setBufferSizeWriter(int bufferSizeWriter) {
		this.bufferSizeWriter = bufferSizeWriter;
	}

	public long getUuidInitNum() {
		return uuidInitNum;
	}

	public void setUuidInitNum(long uuidInitNum) {
		this.uuidInitNum = uuidInitNum;
	}

	public String getUuidInitStr() {
		return uuidInitStr;
	}

	public void setUuidInitStr(String uuidInitStr) {
		this.uuidInitStr = uuidInitStr;
	}

	public String getOntoDir() {
		return ontoDir;
	}

	public void setOntoDir(String ontoDir) {
		this.ontoDir = ontoDir;
	}

}
