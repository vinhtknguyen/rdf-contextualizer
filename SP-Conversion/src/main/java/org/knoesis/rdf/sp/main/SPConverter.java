package org.knoesis.rdf.sp.main;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.parser.Reporter;
import org.knoesis.rdf.sp.parser.SPAnalyzer;
import org.knoesis.rdf.sp.parser.SPParser;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RDFReadUtils;


public class SPConverter {

	
	final static Logger logger = Logger.getLogger(SPConverter.class);

	Reporter reporter;

	protected String metaProp = null;
	protected String metaObj =  null;
	protected String spProp = null;
	protected String url = null;
	
	protected String task = null;

	
	/**	cd /semweb1/datasets
	 * java -jar ~/rdf-context-converter-0.0.1-SNAPSHOT.jar -zip -parallel 4 -f bio2rdf_R3_data -ext TTL -rep NG -shortenURI -spInitNum 1 -spInitStr b2r_r3 > log/bio2rdf_noinfer_results.txt
	 * java -jar ~/rdf-context-converter-0.0.1-SNAPSHOT.jar -zip -parallel 4 -f bio2rdf_R3_data -infer onto -ext TTL -rep NG -shortenURI -spInitNum 1 -spInitStr b2r_r3 > log/bio2rdf_infer_results.txt
	 * 
	 * @param args
	 * -rep 		NQUAD/NANO/TRIPLE/REI as input file representation
	 * -ext 		TTL/NT as output file extension
	 * -f 			FILE/FOLDER to convert the representation
	 * -base		base URI
	 * -metaProp	meta property of the triple, which could the provenance property of the triple, default value is prov:wasDerivedFrom
	 * -metaObj 	meta object of the triple, which could be the source where the triple was created
	 * -spProp		by default, it is rdf:singletonPropertyOf
	 * -zip
	 * -infer 		Ontology file or directory
	 * -url
	 * -prefix		
	 * -shorternURI
	 * -converters
	 */
	
	/** Examples for testing
	 * 
	 * 1)	-f resources/test-nq -ext TTL -rep NG -spInitNum 1 -spInitStr str1
	 * 
	 * 2)	-f resources/test-nq -ext NT -rep NG -spInitNum 1 -spInitStr str2
	 * 
	 * 3) 	-f resources/test-triple -ext TTL -rep Triple -spInitNum 1 -spInitStr str3
	 * 
	 * 4)	-f resources/test-triple -ext NT -rep Triple -spInitNum 1 -spInitStr str4
	 * 
	 * 5)	-f resources/test-default -ext TTL -spInitNum 1 -spInitStr str5
	 * 
	 * 6) 	-f resources/test-default -ext NT -spInitNum 1 -spInitStr str6
	 * 
	 * 7) 	-f resources/test-nano -ext TTL -rep Nano -spInitNum 1 -spInitStr str7
	 * 
	 * 8)	-f resources/test-nano -ext NT -rep Nano -spInitNum 1 -spInitStr str8
	 * 
	 * 9)	-f resources/test-rei -ext TTL -rep REI -spInitNum 1 -spInitStr str9
	 * 
	 * 10)	-f resources/test-rei -ext NT -rep REI -spInitNum 1 -spInitStr str10
	 * 
	 * 11)	-f resources/test-reing -ext TTL -rep ReificationNG -spInitNum 1 -spInitStr str11
	 * 
	 * 12)	-f resources/test-reing -ext NT -rep ReificationNG -spInitNum 1 -spInitStr str12
	 * 
	 * java -jar ~/rdf-context-converter-0.0.1-SNAPSHOT.jar -zip -shortenURI -f ncbo/nq -ext TTL -rep NG -spInitNum 1 -spInitStr ncbo_sp_092016 > ncbo_results.txt 
	 * java -jar ~/rdf-context-converter-0.0.1-SNAPSHOT.jar -zip -shortenURI -f mesh/nq -ext TTL -rep NG -spInitNum 1 -spInitStr mesh_sp_092016 > mesh_results.txt 
	 * java -jar ~/rdf-context-converter-0.0.1-SNAPSHOT.jar -zip -shortenURI -f goa/nq -ext TTL -rep NG -spInitNum 1 -spInitStr goa_sp_092016 > goa_results.txt 
	 * java -jar ~/rdf-context-converter-0.0.1-SNAPSHOT.jar -zip -shortenURI -f pharmkgb/nq -ext TTL -rep NG -spInitNum 1 -spInitStr pharmkgb_sp_092016 > pharmkgb_results.txt 
	 * java -jar ~/rdf-context-converter-0.0.1-SNAPSHOT.jar -zip -shortenURI -f ncbigenes/nq -ext TTL -rep NG -spInitNum 1 -spInitStr ncbigenes_sp_092016 > ncbigenes_results.txt
	 * java -jar ~/rdf-context-converter-0.0.1-SNAPSHOT.jar -zip -shortenURI -f ncbo/nq -ext TTL -rep NG -spInitNum 1 -spInitStr ncbo_sp_092016 > ncbo_results.txt && java -jar ~/rdf-conversion-0.0.1-SNAPSHOT.jar -f mesh/nq -ext TTL -rep NG -spInitNum 1 -spInitStr mesh_sp_092016 > mesh_results.txt && java -jar ~/rdf-conversion-0.0.1-SNAPSHOT.jar -f goa/nq -ext TTL -rep NG -spInitNum 1 -spInitStr goa_sp_092016 > goa_results.txt && java -jar ~/rdf-conversion-0.0.1-SNAPSHOT.jar -f pharmgkb/nq -ext TTL -rep NG -spInitNum 1 -spInitStr pharmgkb_sp_092016 > pharmgkb_results.txt && java -jar ~/rdf-conversion-0.0.1-SNAPSHOT.jar -f ncbigenes/nq -ext TTL -rep NG -spInitNum 1 -spInitStr ncbigenes_sp_092016 > ncbigenes_results.txt	 
	 * */
	
	public static void main(String[] args) {

		SPConverter conversion = new SPConverter();
		conversion.parseParameters(args);
		System.out.println(conversion.getReporter().getExt() + "\t" + conversion.getReporter().getFilein() + "\t" + conversion.getReporter().getRep());
		
		conversion.startParser();
		conversion.startAnalyzer();
	}
	
	
	
	public SPConverter() {
		reporter = new Reporter();
	}

	public void startAnalyzer(){
		
		// Prepare the data from url
		if (this.getUrl() != null && reporter.getDsName() != null){
			RDFReadUtils.fetchLinks(url, reporter.getDsName());
			return;
		}
		
		if (reporter.getRep() != null && (this.task.equals(Constants.PROCESSING_TASK_ANALYZE) || this.task.equals(Constants.PROCESSING_TASK_BOTH))){

			SPAnalyzer analyzer = new SPAnalyzer(reporter);

			analyzer.analyze(reporter.getFileout());
			
		}
		
	}
	public void startParser(){
		
		// Prepare the data from url
		if (this.getUrl() != null && reporter.getDsName() != null){
			RDFReadUtils.fetchLinks(url, reporter.getDsName());
			return;
		}
		
		if (reporter.getRep() != null && (this.task.equals(Constants.PROCESSING_TASK_GENERATE) || this.task.equals(Constants.PROCESSING_TASK_BOTH))){

			SPParser parser = new SPParser(reporter);

			parser.parse(reporter.getFilein(), reporter.getFileout(), reporter.getExt(), reporter.getRep());
		}
		
	}
		
	protected void parseParameters(String[] args) {

		for (int i = 0; i < args.length; i++) {

			// Get input file
			if (args[i].toLowerCase().equals("-f")) {
//				System.out.println("File in: " + args[i + 1]);
				String filename = args[i + 1];
				if (!Files.exists(Paths.get(filename))){
					System.out.println("File " + filename + " does not exist.\n");
					return;
				}
				reporter.setFilein(filename);
			}
			// Get zip para
			if (args[i].toLowerCase().equals("-zip")) {
//				System.out.println("File in: " + args[i + 1]);
				reporter.setZip(true);;
			}
			if (args[i].toLowerCase().equals("-fileout")) {
//				System.out.println("File in: " + args[i + 1]);
				reporter.setFileout(args[i+1]);
			}
			// Get infer para
			if (args[i].toLowerCase().equals("-infer")) {
//				System.out.println("File in: " + args[i + 1]);
				reporter.setInfer(true);
				reporter.setOntoDir(args[i+1]);
			}
			
			if (args[i].toLowerCase().equals("-parallel")) {
//				System.out.println("File in: " + args[i + 1]);
				reporter.setParallel(Integer.parseInt(args[i+1]));
			}
			
			
			// Get infer para
			if (args[i].toLowerCase().equals("-dsname")) {
//				System.out.println("File in: " + args[i + 1]);
				reporter.setDsName(args[i+1]);;
			}
			
			if (args[i].toLowerCase().equals("-task")) {
//				System.out.println("File in: " + args[i + 1]);
				switch (args[i+1].toLowerCase()){
				case Constants.PROCESSING_TASK_ANALYZE:
					this.task = Constants.PROCESSING_TASK_ANALYZE;
					break;
				case Constants.PROCESSING_TASK_GENERATE:
					this.task = Constants.PROCESSING_TASK_GENERATE;
					break;
				default:
					this.task = Constants.PROCESSING_TASK_BOTH;
					break;
					
				}
			}

			// Get prefix para
			if (args[i].toLowerCase().equals("-prefix")) {
//				System.out.println("File in: " + args[i + 1]);
				String filename = args[i + 1];
				if (!Files.exists(Paths.get(filename))){
					System.out.println("File " + filename + " does not exist.\n");
					return;
				}
				reporter.setPrefix(filename);
			}
			// Get shortenURI para
			if (args[i].toLowerCase().equals("-shortenuri")) {
				reporter.setShortenURI(true);
//				System.out.println("Shorten URI commandline: " + this.isShortenURI());
			}
			
			// Get url to start with
			if (args[i].toLowerCase().equals("-url")){
				this.setUrl(args[i+1]);
			}

			// Get url to start with
			if (args[i].toLowerCase().equals("-ratio")){
				reporter.setRatio(Double.parseDouble(args[i+1]));
			}

			// Get input file extension
			if (args[i].toLowerCase().equals("-rep")) {
				switch (args[i + 1].toUpperCase()) {
					case Constants.REI_REP:
						reporter.setRep(Constants.REI_REP);
						break;
					case Constants.NANO_REP:
						reporter.setRep(Constants.NANO_REP);
						break;
					case Constants.NG_REP:
						reporter.setRep(Constants.NG_REP);
						break;
					case Constants.TRIPLE_REP:
						reporter.setRep(Constants.TRIPLE_REP);
						break;
					default:
						reporter.setRep(Constants.NONE_REP);
						break;
				}
			}
			if (args[i].toLowerCase().equals("-ext")) {
				switch (args[i + 1].toLowerCase()) {
					case Constants.NTRIPLE_EXT:
						reporter.setExt(Constants.NTRIPLE_EXT);
						break;
					case Constants.TURTLE_EXT:
						reporter.setExt(Constants.TURTLE_EXT);
						break;
					default:
						reporter.setExt(Constants.TURTLE_EXT);
						break;
				}
			}
						
			if (args[i].toLowerCase().equals("-metaprop")) {
				this.metaProp = args[i+1];
			}
			
			if (args[i].toLowerCase().equals("-metaobj")) {
				this.metaObj = args[i+1];
			}
			
			if (args[i].toLowerCase().equals("-spprop")) {
				this.spProp = args[i+1];
			}
			
			if (args[i].toLowerCase().equals("-spinitnum")) {
				reporter.setUuidInitNum(Long.parseLong(args[i+1]));
			}
			if (args[i].toLowerCase().equals("-spinitstr")) {
				reporter.setUuidInitStr(args[i+1]);
			}

		}

		// Check if input file is provided
		if (reporter.getFilein() == null && this.getUrl() == null) {
			System.out.println("Input file or folder must be provided.");
			return;
		}

		// Check if input file is provided
		if (reporter.getExt() == null && this.getUrl() == null) {
			System.out.println("Input file extension must be provided.");
			return;
		}
		
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMetaProp() {
		return metaProp;
	}

	public void setMetaProp(String metaProp) {
		this.metaProp = metaProp;
	}

	public String getMetaObj() {
		return metaObj;
	}

	public void setMetaObj(String metaObj) {
		this.metaObj = metaObj;
	}

	public String getSpProp() {
		return spProp;
	}

	public void setSpProp(String spProp) {
		this.spProp = spProp;
	}

	public Reporter getReporter() {
		return reporter;
	}

	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}

}
