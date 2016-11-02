package org.knoesis.rdf.sp.stat;

import java.util.ArrayList;
import java.util.List;

import org.knoesis.rdf.sp.utils.Constants;

public class SPDataset {

	private String dsName;
	private String dsLabel;
	private String dsNameForChart;// The first 3-4 chars of dsName
	private String ext;
	private boolean isInfer;
	private String rep;
	private long totalTriples = 0;
	private long totalSingletonProps = 0;
	private long totalSingletonTriples = 0;
	private List<Long> timeTaken;
	private long disk = 0;
	private long totalInferredTriples = 0;
	private long totalInferredSingletonTriples = 0;
	private long timeTakenInferredTriples = 0;
	private long diskInferredTriples = 0;
	
	private float inferredTriplePercentage = 0;
	private float inferredSingletonTriplePercentage = 0;
	private float timePercentage = 0;
	private float diskPercentage = 0;

	
	public SPDataset(String dsName, String ext, boolean isInfer) {
		super();
		this.dsName = dsName;
		this.ext = ext;
		this.isInfer = isInfer;
		this.dsLabel = (isInfer?"Reasoning":"No-Reasoning");
		this.timeTaken = new ArrayList<Long>();
		if (dsName.startsWith(Constants.DS_TYPE_NoDup)){
			this.dsNameForChart = dsName.replace(Constants.DS_TYPE_NoDup,"").substring(0, 3).toUpperCase() +  "-SP" + "-" + Constants.DS_TYPE_NoDup_Full;
		} else {
			this.dsNameForChart = dsName.substring(0, 3).toUpperCase() + "-SP";
		}
	}
	
	public long getAverageTimeTaken(){
		long total = 0;
		long num = 0;
		for (long time: timeTaken){
			num++;
			total += time;
		}
		if (num > 0) return total/num;
		else return 0;
	}
	
	@Override
	public String toString(){
		return this.dsName + "\tLabel:" + this.dsLabel + 
				"\tTotal:" + this.totalTriples +
				"\tTotal percentage:" + this.inferredTriplePercentage +
				"\n\tSingletonProp:" + this.totalSingletonTriples + 
				"\tAverage SingletonProp:" + this.inferredSingletonTriplePercentage + 
				"\n\tAverageTime:" + this.getAverageTimeTaken() +
				"\tAverageTime percentage:" + this.timePercentage +
				"\n\tDisk:" + this.disk +
				"\tDisk percentage:" + this.diskPercentage;
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

	public String getDsNameForChart() {
		return dsNameForChart;
	}

	public void setDsNameForChart(String dsNameForChart) {
		this.dsNameForChart = dsNameForChart;
	}

	public long getTotalInferredTriples() {
		return totalInferredTriples;
	}

	public void setTotalInferredTriples(long totalInferredTriples) {
		this.totalInferredTriples = totalInferredTriples;
	}

	public long getTimeTakenInferredTriples() {
		return timeTakenInferredTriples;
	}

	public void setTimeTakenInferredTriples(long timeTakenInferredTriples) {
		this.timeTakenInferredTriples = timeTakenInferredTriples;
	}

	public long getDiskInferredTriples() {
		return diskInferredTriples;
	}

	public void setDiskInferredTriples(long diskInferredTriples) {
		this.diskInferredTriples = diskInferredTriples;
	}

	public float getInferredTriplePercentage() {
		return inferredTriplePercentage;
	}

	public void setInferredTriplePercentage(float inferredTriplePercentage) {
		this.inferredTriplePercentage = inferredTriplePercentage;
	}

	public float getTimePercentage() {
		return timePercentage;
	}

	public void setTimePercentage(float timePercentage) {
		this.timePercentage = timePercentage;
	}

	public float getDiskPercentage() {
		return diskPercentage;
	}

	public void setDiskPercentage(float diskPercentage) {
		this.diskPercentage = diskPercentage;
	}

	public float getInferredSingletonTriplePercentage() {
		return inferredSingletonTriplePercentage;
	}

	public void setInferredSingletonTriplePercentage(
			float inferredSingletonTriplePercentage) {
		this.inferredSingletonTriplePercentage = inferredSingletonTriplePercentage;
	}

	public long getTotalInferredSingletonTriples() {
		return totalInferredSingletonTriples;
	}

	public void setTotalInferredSingletonTriples(long totalInferredSingletonTriples) {
		this.totalInferredSingletonTriples = totalInferredSingletonTriples;
	}
	
	
	
	
}
