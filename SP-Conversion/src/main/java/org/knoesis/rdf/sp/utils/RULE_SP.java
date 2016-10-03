package org.knoesis.rdf.sp.utils;

public enum RULE_SP{
   
	RULE_RDF_SP_1(4),
	RULE_RDF_SP_2(8),
	RULE_RDF_SP_3(3),
	RULE_RDFS_SP_1(10),
	RULE_RDFS_SP_2(11),
	RULE_RDFS_SP_3(1),
	RULE_RDFS_SP_4(6),
	RULE_RDFS_SP_5(7),
	RULE_OWL_SP_1(2),
	RULE_OWL_SP_2(9),
	RULE_OWL_SP_3(5),
	RULE_ALL_SP(-1);
	
    private int num;

	RULE_SP() {
	    }

	    RULE_SP(int num) {
	      this.num = num;
	    }

	    public int getNum() {
	      return num;
	    }
}
