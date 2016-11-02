package org.knoesis.rdf.sp.utils;

public class Constants {
	
	public static final String QUAD_EXT = "nq";
	public static final String NTRIPLE_EXT = "nt";
	public static final String TURTLE_EXT = "ttl";
	public static final String DEFAULT_EXT = "ttl";

	public static final String REI_REP = "REI";
	public static final String SP_REP = "SP";
	public static final String NG_REP = "NG";
	public static final String NANO_REP = "NANO";
	public static final String TRIPLE_REP = "TRIPLE";
	public static final String NONE_REP = "NONE";

	public static final String SP_ID = "sp";
	public static final String SP_SUFFIX = "_sp";
	public static final String SP_UUID_PREFIX = "sp";
	public static final String NS_STR = "ns";
	public static final String SP_START_DELIMITER = "_";
	public static final String SP_MID_DELIMITER = ":";
	public static final String SP_END_DELIMITER = "";

	
	public static final String SINGLETON_PROPERTY_OF_URI = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#singletonPropertyOf>";
	public static final String SINGLETON_PROPERTY_OF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#singletonPropertyOf";
	public static final String WAS_DERIVED_FROM_URI = "<http://www.w3.org/ns/prov#wasDerivedFrom>";
	public static final String WAS_DERIVED_FROM = "http://www.w3.org/ns/prov#wasDerivedFrom";
	public static final String RDF_SUBJECT = "http://www.w3.org/1999/02/22-rdf-syntax-ns#subject";
	public static final String RDF_PREDICATE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate";
	public static final String RDF_OBJECT = "http://www.w3.org/1999/02/22-rdf-syntax-ns#object";
	public static final String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	public static final String RDF_STATEMENT = "http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement";
	public static final String RDF_SINGLETON_PROPERTY_CLASS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#SingletonProperty";
	public static final String RDF_GENERIC_PROPERTY_CLASS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#GenericProperty";
	public static final String RDFS_DOMAIN_PROPERTY = "http://www.w3.org/2000/01/rdf-schema#domain";
	public static final String RDFS_RANGE_PROPERTY = "http://www.w3.org/2000/01/rdf-schema#range";
	public static final String RDFS_SUBPROPERTYOF_PROPERTY = "http://www.w3.org/2000/01/rdf-schema#subPropertyOf";
	public static final String OWL_EQUIVALENTPROPERTY_PROPERTY = "http://www.w3.org/2002/07/owl#equivalentProperty";

	/**
	 * For contextual inference
	 * */
	
	public static final int RULE_ALL_SP = 0;		// infer all the rules
	public static final int RULE_RDFS_SP_3 = 1; 	// infer singleton property from hierarchy
	public static final int RULE_OWL_SP_1 = 2;		//			*				
	public static final int RULE_RDF_SP_3 = 3; 		// infer generic triple
	public static final int RULE_RDF_SP_1 = 4;		// infer SingletonProperty class instance
	public static final int RULE_OWL_SP_3 = 5;		// infer GenericProperty class instance
	public static final int RULE_RDFS_SP_4 = 6;		//			*				
	public static final int RULE_RDFS_SP_5 = 7;		//			*				
	public static final int RULE_RDF_SP_2 = 8;		//			*				
	public static final int RULE_OWL_SP_2 = 9;		//			*				
	public static final int RULE_RDFS_SP_1 = 10;	// infer domain
	public static final int RULE_RDFS_SP_2 = 11;	// infer range

	
	/**
	 * File management
	 */
	public static final String WRITE_FILE_PREFIX = "# This file is generated by rdf-contextualizer.\n";
	public static final String CONVERTED_TO_SP_NT = "_nt";
	public static final String CONVERTED_TO_SP_TTL = "_ttl";
	public static final String CONVERTED_TO_SP_INF_NT = "_inf_nt";
	public static final String CONVERTED_TO_SP_INF_TTL = "_inf_ttl";
	public static final String ORIGINAL_DIRECTORY = "ori";
	public static final String REPORTS_DIRECTORY = "reports";
	public static final String CHARTS_DIRECTORY = "charts";
	public static final String STAT_FILE_TIME = REPORTS_DIRECTORY + "/sp_stat_time.txt";
	public static final String STAT_FILE_DATA = REPORTS_DIRECTORY + "/sp_stat_data.txt";
	public static final String STAT_FILE_DISK = REPORTS_DIRECTORY + "/sp_stat_disk.txt";
	public static final String STAT_FILE_SINGLETON = REPORTS_DIRECTORY + "/sp_stat_singleton.txt";
	public static final String STAT_FILE_SINGLETON_INSTANTIATION = REPORTS_DIRECTORY + "/sp_stat_singleton_instantiation.txt";
	public static final String STAT_FILE_TIME_STEP = REPORTS_DIRECTORY + "/sp_stat_time_step.txt";
	public static final String STAT_FILE_DATA_STEP = REPORTS_DIRECTORY + "/sp_stat_data_step.txt";
	public static final String STAT_FILE_DISK_STEP = REPORTS_DIRECTORY + "/sp_stat_disk_step.txt";

	public static final String STAT_FILE_DISK_CHART = CHARTS_DIRECTORY + "/sp_stat_disk_chart.jpeg";
	public static final String STAT_FILE_DATA_CHART = CHARTS_DIRECTORY + "/sp_stat_data_chart.jpeg";
	public static final String STAT_FILE_TIME_CHART = CHARTS_DIRECTORY + "/sp_stat_time_chart.jpeg";
	public static final String STAT_FILE_TIME_SIZE_CHART = CHARTS_DIRECTORY + "/sp_stat_time_size_chart.jpeg";
	public static final String STAT_FILE_TIME_INFERRED_TRIPLES_CHART = CHARTS_DIRECTORY + "/sp_stat_time_inferred_triples_chart.jpeg";
	public static final String STAT_FILE_DISK_INFERRED_TRIPLES_CHART = CHARTS_DIRECTORY + "/sp_stat_disk_inferred_triples_chart.jpeg";
	public static final String STAT_FILE_SINGLETON_CHART = CHARTS_DIRECTORY + "/sp_stat_singleton_chart.jpeg";

	
	public static final String DS_TYPE_NQ = "NQ";
	public static final String DS_TYPE_SP = "SP";
	public static final String DS_TYPE_SPR = "SPR";
	public static final String DS_TYPE_NoDup = "ND";
	public static final String DS_TYPE_NoDup_Full = "Unique";
	public static final String DS_TYPE_Dup_Full = "Dup";
	

	public static final String PROCESSING_STEP_PARSE = "parsing";
	public static final String PROCESSING_STEP_CONVERT = "converting";
	public static final String PROCESSING_STEP_TRANFORM = "transforming";
	public static final String PROCESSING_STEP_SPLITTING = "splitting";
	public static final String PROCESSING_STEP_WRITE = "writing";
	public static final String PROCESSING_STEP_ALL = "all";

	public static final String OPTIONS_INFER = "infer";
	public static final String OPTIONS_NO_INFER = "no_infer";
	public static final String DATA_DIR = "data";
	
	public static final String PROCESSING_TASK_GENERATE = "generating";
	public static final String PROCESSING_TASK_ANALYZE = "analyzing";
	public static final String PROCESSING_TASK_BOTH = "both";

	/**
	 * Performance tuning
	 * 
	 * */
	
	public static double CPU_UTILIZATION_RATIO = 1;
	public static int PARALLEL_LEVEL = 2; 
	public static int BUFFER_SIZE_SMALL = 10485760; // 10MB
	public static int BUFFER_SIZE_MEDIUM = 104857600; // 100MB
	public static int BUFFER_SIZE_LARGE = 104857600; // 200MB
	
	public static long FILE_ZIP_SIZE_MEDIUM = 10737418; // 10MB
	public static long FILE_ZIP_SIZE_LARGE = 107374182; // 100MB
	public static long FILE_ZIP_SIZE_VERY_LARGE = 1073741820; // 1GB
	public static long FILE_ZIP_SIZE_HUGE = 5368709120L; // 5GB
	public static long FILE_REGULAR_SIZE_MEDIUM = 1073741824L; // 1GB
	public static long FILE_REGULAR_SIZE_LARGE = 10737418240L; // 10GB
	public static long FILE_REGULAR_SIZE_VERY_LARGE = 107374182400L; // 100GB

	public static int FILE_ZIP_SIZE_MEDIUM_CONVERTERS = 2; // 10MB
	public static int FILE_ZIP_SIZE_LARGE_CONVERTERS = 3; // 100MB
	public static int FILE_ZIP_SIZE_VERY_LARGE_CONVERTERS = 4; // 1GB
	public static int FILE_ZIP_SIZE_HUGE_CONVERTERS = 5; // 5GB
	public static int FILE_REGULAR_SIZE_MEDIUM_CONVERTERS = 2; // 10GB
	public static int FILE_REGULAR_SIZE_LARGE_CONVERTERS = 3; // 10GB
	public static int FILE_REGULAR_SIZE_VERY_LARGE_CONVERTERS = 4; // 10GB
}

