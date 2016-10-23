package org.knoesis.rdf.sp.parser;

public class SPDataStatElement {
	
	String dsName;
	String dsType;
	long countItem; 
	long countSingletonProp;
	long countGenericProp;
	long totalSingletonInstantiation;
	double averageSingsPerGeneric;
	String filename;
	long diskspace;
	
	public SPDataStatElement(String dsName, String dsType) {
		super();
		this.dsName = dsName;
		this.dsType = dsType;
	}
	
	public SPDataStatElement(String dsName, String dsType, String filename) {
		super();
		this.dsName = dsName;
		this.dsType = dsType;
		this.filename = filename;
	}
	public String getDsName() {
		return dsName;
	}
	public void setDsName(String dsName) {
		this.dsName = dsName;
	}
	public String getDsType() {
		return dsType;
	}
	public void setDsType(String dsType) {
		this.dsType = dsType;
	}
	public long getCountItem() {
		return countItem;
	}
	public void setCountItem(long countItem) {
		this.countItem = countItem;
	}
	public long getCountSingletonProp() {
		return countSingletonProp;
	}
	public void setCountSingletonProp(long countSingletonProp) {
		this.countSingletonProp = countSingletonProp;
	}
	public long getTotalSingletonInstantiation() {
		return totalSingletonInstantiation;
	}

	public void setTotalSingletonInstantiation(long totalSingletonInstantiation) {
		this.totalSingletonInstantiation = totalSingletonInstantiation;
	}

	public long getCountGenericProp() {
		return countGenericProp;
	}
	public void setCountGenericProp(long countGenericProp) {
		this.countGenericProp = countGenericProp;
	}
	public double getAverageSingsPerGeneric() {
		return averageSingsPerGeneric;
	}
	public void setAverageSingsPerGeneric(double averageSingsPerGeneric) {
		this.averageSingsPerGeneric = averageSingsPerGeneric;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public long getDiskspace() {
		return diskspace;
	}
	public void setDiskspace(long diskspace) {
		this.diskspace = diskspace;
	}
}
