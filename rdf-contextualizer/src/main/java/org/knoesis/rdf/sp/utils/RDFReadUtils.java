package org.knoesis.rdf.sp.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RDFReadUtils {
	public static void processUrl(String url_in, String dir){
		System.out.println("Fetching url: " + url_in);
		List<String> urls = readUrl(url_in);
		
		// Create the folder dir if not exist
		try {
			Files.createDirectories(Paths.get(dir));
			for (String url: urls){
				String[] tmp = url.split("/");
				String file = dir + "/" + Constants.ORIGINAL_DIRECTORY + "/" + tmp[tmp.length-1];
				System.out.println("Downloading file: " + url);
				downloadFile(url, file);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void fetchLinks(String url_in, String name){
		List<String> urls = readUrl(url_in);
	    BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(name + "_links.txt"));
			for (String url: urls){
				System.out.println(url);
				out.write(url + "\n");
			}
			out.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	public static void downloadFile(String url, String target){
		try {
			URL website = new URL(url);
			InputStream in = website.openStream();
			Files.copy(in, Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public static List<String> readUrl(String url) {
    	List<String> urls = new ArrayList<String>();

        try {
	        Document doc = Jsoup.connect(url).get();
	        Elements links = doc.select("a[href]");
	
	        for (Element link : links) {
	            if (link.attr("abs:href").contains(".nq") || link.attr("abs:href").contains(".owl") || link.attr("abs:href").contains(".nt") || link.attr("abs:href").contains(".ttl")) {

	            	urls.add(link.attr("abs:href").toString());
	            	System.out.println(link.attr("abs:href").toString());
	            }
	        }
        } catch (IOException e){
        	e.printStackTrace();
        }
        return urls;
    }

}
