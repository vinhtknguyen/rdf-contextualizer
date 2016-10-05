package org.knoesis.rdf.sp.parser;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.knoesis.rdf.sp.model.SPModel;
import org.knoesis.rdf.sp.utils.Constants;

public abstract class SPParser {
	
	protected boolean infer = false;
	protected boolean zip = false;
	protected String ontoDir;
	protected String rep;
	protected String dsName = null;
	public void init(){
		if (this.infer){
			SPModel.loadModel(this.getOntoDir());
		}

	}
	
	public SPParser() {
		
	}

	public void parseFile(String file, String ext, String rep, String dir){
		
	}
	
	public void parse(String file, String ext, String rep){
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
					parseFile(entry.toString(), ext, rep, dirOut);
		        }
				
		    } catch (IOException e1) {
				e1.printStackTrace();
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
}
