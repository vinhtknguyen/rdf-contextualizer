package org.knoesis.semanticweb.rdf.jena.sp.main;

import org.apache.log4j.Logger;
import org.knoesis.semanticweb.rdf.jena.sp.converter.ContextualRepresentationConverter;
import org.knoesis.semanticweb.rdf.jena.sp.converter.NamedGraph2SP;
import org.knoesis.semanticweb.rdf.jena.sp.converter.NanoPub2SP;
import org.knoesis.semanticweb.rdf.jena.sp.converter.Reification2SP;
import org.knoesis.semanticweb.rdf.jena.sp.converter.Triple2SP;
import org.knoesis.semanticweb.rdf.utils.Constants;


public class SPConverter {

	
	final static Logger logger = Logger.getLogger(SPConverter.class);


	protected String rep = null;
	protected String ext = null;
	protected String fileIn = null;
	protected String metaProp = null;
	protected String metaObj =  null;
	protected String spProp = null;
	protected long spInitNum = -1;
	protected String spInitStr = null;
	
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

	
	/**
	 * java -jar Conversion.jar -r NQUAD/TRIPLE/REI -e TTL/NT -f file/folder 
	 * 
	 * @param args
	 * -rep 		NQUAD/NANO/TRIPLE/REI as input file representation
	 * -ext 		TTL/NT as output file extension
	 * -f 			FILE/FOLDER to convert the representation
	 * -base		base URI
	 * -metaProp	meta property of the triple, which could the provenance property of the triple, default value is prov:wasDerivedFrom
	 * -metaObj 	meta object of the triple, which could be the source where the triple was created
	 * -spProp		by default, it is rdf:singletonPropertyOf
	 * 
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
	 * */
	
	public static void main(String[] args) {

		SPConverter conversion = new SPConverter();
		conversion.parseParameters(args);
		System.out.println(conversion.getExt() + "\t" + conversion.getFileIn() + "\t" + conversion.getRep());
		
		conversion.start();
	}
	
	public void start(){
		if (this.getRep() != null){
			ContextualRepresentationConverter converter = null;
			switch (this.getRep().toUpperCase()){
			
			case Constants.NANO_REP:
				converter = new NanoPub2SP();
				break;
			
			case Constants.NG_REP:
				converter = new NamedGraph2SP();
				if (this.getMetaProp() != null) {
					((NamedGraph2SP)converter).setNamedGraphProp(this.getMetaProp());
				}
				break;
			
			case Constants.REI_REP:
				converter = new Reification2SP();
				break;
				
			case Constants.TRIPLE_REP:
				converter = new Triple2SP();
				if (this.getMetaProp() != null) {
					((Triple2SP)converter).setMetaPredicate(this.getMetaProp());
				}
				if (this.getMetaObj() != null) {
					((Triple2SP)converter).setMetaObject(this.getMetaObj());
				}
				break;
			case Constants.NONE_REP:
				converter = new ContextualRepresentationConverter();
				break;
			default:
				converter = new ContextualRepresentationConverter();
				break;
			}
			
			// Initialize the variables
			if (this.getSpProp() != null) {
				converter.setSingletonPropertyOf(this.getSpProp());
			}
			
			if (this.getSpInitNum() != -1 ) converter.setInitUUIDNumber(this.getSpInitNum());
			if (this.getSpInitStr() != null) converter.setInitUUIDPrefix(this.getSpInitStr());

			// Start running the conversion
			converter.convert(this.getFileIn(), this.getExt(), this.getRep());
		}
		
	}
		
	protected void parseParameters(String[] args) {

		for (int i = 0; i < args.length; i++) {

			// Get input file
			if (args[i].equals("-f")) {
//				System.out.println("File in: " + args[i + 1]);
				this.setFileIn(args[i + 1]);
			}

			// Get input file extension
			if (args[i].equals("-rep")) {
				switch (args[i + 1].toUpperCase()) {
					case Constants.REI_REP:
						this.rep = Constants.REI_REP;
						break;
					case Constants.NANO_REP:
						this.rep = Constants.NANO_REP;
						break;
					case Constants.NG_REP:
						this.rep = Constants.NG_REP;
						break;
					case Constants.TRIPLE_REP:
						this.rep = Constants.TRIPLE_REP;
						break;
					default:
						this.rep = Constants.NONE_REP;
						break;
				}
			}
			if (args[i].equals("-ext")) {
				switch (args[i + 1].toLowerCase()) {
					case Constants.NTRIPLE_EXT:
						this.ext = Constants.NTRIPLE_EXT;
						break;
					case Constants.TURTLE_EXT:
						this.ext = Constants.TURTLE_EXT;
						break;
					default:
						this.ext = Constants.TURTLE_EXT;
						break;
				}
			}
						
			if (args[i].equals("-metaProp")) {
				this.metaProp = args[i+1];
			}
			
			if (args[i].equals("-metaObj")) {
				this.metaObj = args[i+1];
			}
			
			if (args[i].equals("-spProp")) {
				this.spProp = args[i+1];
			}
			
			if (args[i].equals("-spInitNum")) {
				this.spInitNum = Long.parseLong(args[i+1]);
			}
			if (args[i].equals("-spInitStr")) {
				this.spInitStr = args[i+1];
			}

		}

		// Check if input file is provided
		if (this.getFileIn() == null) {
			System.out.println("Input file or folder must be provided.");
			return;
		}

		// Check if input file is provided
		if (this.getExt() == null) {
			System.out.println("Input file extension must be provided.");
			return;
		}
		
	}
	
	public long getSpInitNum() {
		return spInitNum;
	}

	public void setSpInitNum(long spInitNum) {
		this.spInitNum = spInitNum;
	}

	public String getSpInitStr() {
		return spInitStr;
	}

	public void setSpInitStr(String spInitStr) {
		this.spInitStr = spInitStr;
	}

	public void setRep(String rep) {
		this.rep = rep;
	}

	public String getRep() {
		return this.rep;
	}


	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getExt() {
		return this.ext;
	}

	public void setFileIn(String filename) {
		this.fileIn = filename;
	}

	public String getFileIn() {
		return this.fileIn;
	}

}
