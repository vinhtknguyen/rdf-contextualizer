package org.knoesis.rdf.sp.parser;

import java.nio.file.Paths;

import org.knoesis.rdf.sp.utils.Constants;

public class ParserElement {

	private String filein;
	private String fileout;
	private long fileSize;
	private FileCategory fileCategory;
	private int nConverters;
	private int nTasksDefault;
	private int finishedTasks = 1;
	private boolean finished;
	private int bufferStream;
	private int bufferWriter;
	private int bufferStreamSubStream;
	private int bufferWriterSubStream;
	
	public ParserElement(String filein, String fileout) {
		super();
		this.filein = filein;
		this.fileout = fileout;
		this.fileCategory = FileCategory.getCategory(filein);
		this.nConverters = this.fileCategory.category;
		this.fileSize = Paths.get(filein).toFile().length();
		this.nTasksDefault = (fileCategory.category==1)?fileCategory.category*3 + 1:fileCategory.category*3 + 2;
		this.finishedTasks = 1; // for itself

		// Setup buffer stream size
//		if (fileCategory.category >= 3) this.bufferStream = Constants.BUFFER_SIZE_LARGE;
		 if (fileCategory.category > 1) this.bufferStream = Constants.BUFFER_SIZE_MEDIUM;
		else  this.bufferStream = Constants.BUFFER_SIZE_SMALL;
		
		if (fileCategory.category >= 3) this.bufferWriter = Constants.BUFFER_SIZE_LARGE;
		else if (fileCategory.category > 1) this.bufferWriter = Constants.BUFFER_SIZE_MEDIUM;
		else  this.bufferWriter = Constants.BUFFER_SIZE_SMALL;
		
		this.bufferStreamSubStream = bufferStream;
		this.bufferWriterSubStream = bufferWriter;
	}
	
	public void updateFinishedTasks(int num){
		finishedTasks += num;
		System.out.println("finished " + finishedTasks + " out of " + nTasksDefault);
		// One is for itself
		if (finishedTasks == nTasksDefault) {
			finished = true;
		}
		else finished = false;
	}
	public String getFilein() {
		return filein;
	}
	
	public void setFilein(String filein) {
		this.filein = filein;
	}
	
	public String getFileout() {
		return fileout;
	}
	
	public void setFileout(String fileout) {
		this.fileout = fileout;
	}

	public FileCategory getFileCategory() {
		return this.fileCategory;
	}

	public void setFileCategory(FileCategory filesize) {
		this.fileCategory = filesize;
	}

	public int getnConverters() {
		return nConverters;
	}

	public void setnConverters(int nConverters) {
		this.nConverters = nConverters;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public int getBufferStream() {
		return bufferStream;
	}

	public void setBufferStream(int bufferStream) {
		this.bufferStream = bufferStream;
	}

	public int getBufferWriter() {
		return bufferWriter;
	}

	public void setBufferWriter(int bufferWriter) {
		this.bufferWriter = bufferWriter;
	}

	public int getBufferStreamSubStream() {
		return bufferStreamSubStream;
	}

	public void setBufferStreamSubStream(int bufferStreamSubStream) {
		this.bufferStreamSubStream = bufferStreamSubStream;
	}

	public int getBufferWriterSubStream() {
		return bufferWriterSubStream;
	}

	public void setBufferWriterSubStream(int bufferWriterSubStream) {
		this.bufferWriterSubStream = bufferWriterSubStream;
	}

	public int getnTasksDefault() {
		return nTasksDefault;
	}

	public void setnTasksDefault(int nTasksDefault) {
		this.nTasksDefault = nTasksDefault;
	}

	public int getFinishedTasks() {
		return finishedTasks;
	}

	public void setFinishedTasks(int finishedTasks) {
		this.finishedTasks = finishedTasks;
	}
	
	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public String toString(){
		return this.filein + " \t " + this.fileout + " \t " + this.fileSize + " vs. " + this.finishedTasks + " vs. " + this.nConverters;
	}
}
