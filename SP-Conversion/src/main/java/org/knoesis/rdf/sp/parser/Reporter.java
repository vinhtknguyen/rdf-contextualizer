package org.knoesis.rdf.sp.parser;

import java.text.DecimalFormat;

import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;
import org.knoesis.rdf.sp.utils.SPStats;

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
	boolean zip = false;
	String uuidInitStr = Constants.SP_UUID_PREFIX;
	long uuidInitNum = System.currentTimeMillis();
	String ontoDir;
	String prefix;
	int parallel = Constants.PARALLEL_LEVEL;
	double ratio = Constants.CPU_UTILIZATION_RATIO;
	
	public Reporter() {
		// TODO Auto-generated constructor stub
	}

	public synchronized void reportSystem(long start, ParserElement element, String step){
		this.step = step;
		long end = System.currentTimeMillis();
		String filename = RDFWriteUtils.getPrettyName(element.getFilein());
	    System.out.println(filename + ": pthread " + Thread.currentThread().getId() + " " + step + " in " + (end-start) + " ms.");
		SPStats.reportSystem(start, end, rep, (infer?Constants.DS_TYPE_SPR:Constants.DS_TYPE_SP), ext, filename, element.getFileout(), dsName, step);
	}
	public synchronized void reportSystemTotal(long start, String dsName, String dsType, String ext){
		long end = System.currentTimeMillis();
	    System.out.println(dsName + ": pthread " + Thread.currentThread().getId() + " finished in " + (end-start) + " ms.");
		SPStats.reportSystemTotal(dsName, dsType, end-start, ext);
	}
	
	public synchronized void reportData(long start, String dsName, String dsType, String filein, long countItem, long countSingletonProp, long totalSingInstantiation, long countGenericProp, double average) {
		long end = System.currentTimeMillis();
		String filename = RDFWriteUtils.getPrettyName(filein);
	    System.out.println(filename + ": pthread " + Thread.currentThread().getId() + " " + step + " in " + (end-start) + " ms.");
		SPStats.reportData(dsName, dsType, filename, countItem, countSingletonProp, totalSingInstantiation, countGenericProp, average, ext);
	}
	
	public synchronized void reportDataTotal(long start, String dsName, String dsType, long countItem, long countSingletonProp, long totalSingInstantiation, long countGenericProp, double average) {
		long end = System.currentTimeMillis();
	    System.out.println(dsName + ": pthread " + Thread.currentThread().getId() + " " + step + " in " + (end-start) + " ms.");
		SPStats.reportDataTotal(dsName, dsType,  countItem, countSingletonProp, totalSingInstantiation, countGenericProp, average, ext);
	}
	
	public void reportGenericProp(String dsName, String genericprop, long num){
		SPStats.reportGenericProp(dsName, genericprop, num);
	}
	public synchronized void reportDiskTotal(String dsName, String dsType,  long size, String ext ){
		SPStats.reportDiskTotal(dsName, dsType, size, ext );

	}

	public synchronized void reportStartStatus(ParserElement element, String step){
		String filename = RDFWriteUtils.getPrettyName(element.getFilein());
	    System.out.println(filename + ": pthread " + Thread.currentThread().getId() + " started " + step + " with buffer stream " + element.getBufferStream() + " buffer writer " + element.getBufferWriter());
	}

	public synchronized void reportEndStatus(ParserElement element){
		String filename = RDFWriteUtils.getPrettyName(element.getFilein());
	    System.out.println(filename + ": done processing. ");
	}
	public synchronized void reportCancelledStatus(ParserElement element){
		String filename = RDFWriteUtils.getPrettyName(element.getFilein());
	    System.out.println(filename + ": is cancelled processing. ");
	}
	
	public synchronized void reportFinish(long start){
		long end = System.currentTimeMillis();
		DecimalFormat time_formatter = new DecimalFormat("#,###.00");
		String filename = RDFWriteUtils.getPrettyName(filein);
	    System.out.println("Time: " + time_formatter.format(end-start) + " in ms.");
	    System.out.println(filename + ": done processing. ");
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

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public int getParallel() {
		return parallel;
	}

	public void setParallel(int parallel) {
		this.parallel = parallel;
	}

	public double getRatio() {
		return ratio;
	}

	public void setRatio(double ratio) {
		this.ratio = ratio;
	}

}
