package org.knoesis.rdf.sp.stat;

import java.util.ArrayList;
import java.util.List;

public class SPDataset {

	private String dsName;
	private String dsLabel; // The first 3-4 chars of dsName
	private String ext;
	private boolean isInfer;
	private String rep;
	private long totalTriples = 0;
	private long totalSingletonProps = 0;
	private long totalSingletonTriples = 0;
	private List<Long> timeTaken;
	private long disk = 0;
	
	public SPDataset(String dsName, String ext, boolean isInfer) {
		super();
		this.dsName = dsName;
		this.ext = ext;
		this.isInfer = isInfer;
		this.dsLabel = (isInfer?"SPR":"SP") + "_" + ext;
		this.timeTaken = new ArrayList<Long>();
	}
	
	public long getAverageTimeTaken(){
		long total = 0;
		long num = 0;
		for (long time: timeTaken){
			num++;
			total += time;
		}
		return total/num;
	}
	
	@Override
	public String toString(){
		return this.dsName + "\tLabel:" + this.dsLabel + 
				"\tTotal:" + this.totalTriples +
				"\tSingletonProp:" + this.totalSingletonProps + 
				"\tAverageTime:" + this.getAverageTimeTaken() +
				"\tDisk:" + this.disk;
	}
	
	@Override
	public boolean equals(Object obj){
        boolean sameSame = false;

        if (obj != null && obj instanceof SPDataset) {
            sameSame = this.dsLabel == ((SPDataset) obj).dsLabel;
        }

        return sameSame;
 	}
	
	public String getDsName() {
		return dsName;
	}
	public void setDsName(String dsName) {
		this.dsName = dsName;
	}
	public String getDsLabel() {
		return dsLabel;
	}
	public void setDsLabel(String dsLabel) {
		this.dsLabel = dsLabel;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public boolean isInfer() {
		return isInfer;
	}
	public void setInfer(boolean isInfer) {
		this.isInfer = isInfer;
	}
	public String getRep() {
		return rep;
	}
	public void setRep(String rep) {
		this.rep = rep;
	}
	public long getTotalTriples() {
		return totalTriples;
	}
	public void setTotalTriples(long totalTriples) {
		this.totalTriples = totalTriples;
	}
	public long getTotalSingletonProps() {
		return totalSingletonProps;
	}
	public void setTotalSingletonProps(long totalSingletonProps) {
		this.totalSingletonProps = totalSingletonProps;
	}
	public long getTotalSingletonTriples() {
		return totalSingletonTriples;
	}
	public void setTotalSingletonTriples(long totalSingletonTriples) {
		this.totalSingletonTriples = totalSingletonTriples;
	}
	public List<Long> getTimeTaken() {
		return timeTaken;
	}
	public void setTimeTaken(List<Long> timeTaken) {
		this.timeTaken = timeTaken;
	}
	public long getDisk() {
		return disk;
	}
	public void setDisk(long disk) {
		this.disk = disk;
	}
	
	
	
	
}
