package org.knoesis.rdf.sp.parser;

import java.nio.file.Paths;

import org.knoesis.rdf.sp.utils.Constants;

public enum FileCategory {
	
	FILE_SIZE_SMALL(1),
	FILE_SIZE_MEDIUM(2),
	FILE_SIZE_LARGE(3),
	FILE_SIZE_VERY_LARGE(3),
	FILE_SIZE_HUGE(3);
	
	int category;

	private FileCategory(int cat) {
		this.category = cat;
	}

	public static FileCategory getCategory(String filein) {
		
        boolean zipfile = filein.endsWith(".gz");
		if (zipfile){ 
			if (Paths.get(filein).toFile().length() > Constants.FILE_ZIP_SIZE_HUGE){
				return FILE_SIZE_HUGE;
			} else if (Paths.get(filein).toFile().length() > Constants.FILE_ZIP_SIZE_VERY_LARGE){
				return FILE_SIZE_VERY_LARGE;
			} else if (Paths.get(filein).toFile().length() > Constants.FILE_ZIP_SIZE_LARGE){
				return FILE_SIZE_LARGE;
			} else if (Paths.get(filein).toFile().length() > Constants.FILE_ZIP_SIZE_MEDIUM){
				return FILE_SIZE_MEDIUM;
			}

		} else {
			if (Paths.get(filein).toFile().length() > Constants.FILE_REGULAR_SIZE_VERY_LARGE){
				return FILE_SIZE_VERY_LARGE;
			} else if (Paths.get(filein).toFile().length() > Constants.FILE_REGULAR_SIZE_LARGE){
				return FILE_SIZE_LARGE;
			} else if (Paths.get(filein).toFile().length() > Constants.FILE_REGULAR_SIZE_MEDIUM){
				return FILE_SIZE_MEDIUM;
			} 
		}
		return FILE_SIZE_SMALL;
	}
	
}
