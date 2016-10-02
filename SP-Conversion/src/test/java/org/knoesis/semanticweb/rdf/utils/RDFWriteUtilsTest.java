package org.knoesis.semanticweb.rdf.utils;


import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.semanticweb.rdf.jena.sp.converter.NamedGraph2SP;
import org.knoesis.semanticweb.rdf.sp.model.*;

public class RDFWriteUtilsTest {

	List<SPTriple> triples = new ArrayList<SPTriple>();
	NamedGraph2SP con1 = new NamedGraph2SP();
	@Before
	public void setUp() throws Exception {
		triples.add(new SPTriple(new SPNode("http://abc.def/s2"), new SPNode("http://abc.def/p1"), new SPNode("http://abc.def/o1")));
		triples.add(new SPTriple(new SPNode("http://abc.def/s1"), new SPNode("http://abc.def/p1"), new SPNode("http://abc.def/o3")));
		triples.add(new SPTriple(new SPNode("http://abc.def/s1"), new SPNode("http://abc.def/p2"), new SPNode("http://abc.def/o2")));
		triples.add(new SPTriple(new SPNode("http://abc.def/s2"), new SPNode("http://abc.def/p1"), new SPNode("http://abc.def/o4")));
		triples.add(new SPTriple(new SPNode("http://abc.def/s1"), new SPNode("http://abc.def/p2"), new SPNode("http://abc.def/o2")));
		
	}
	
	@Test
	public void testPrintTriples2N3(){
		
		String out = RDFWriteUtils.printTriples2N3(triples);		
		System.out.println(out);
		
		
	}

}
