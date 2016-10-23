package org.knoesis.rdf.sp.converter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestFileType {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String filein = "src/main/resources/dbpedia_2015-10.nt.gz";
		
		try {
			System.out.println("Filename " + filein + " has Filetype: " + Files.probeContentType(Paths.get(filein)) + " of " + filein );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
