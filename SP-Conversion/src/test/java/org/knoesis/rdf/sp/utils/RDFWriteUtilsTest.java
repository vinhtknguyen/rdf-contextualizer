package org.knoesis.rdf.sp.utils;


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.converter.NamedGraph2SP;
import org.knoesis.rdf.sp.model.*;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;

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
		
		String out = RDFWriteUtils.printTriples2N3(triples, new ConcurrentHashMap<String,String>(), new ConcurrentHashMap<String,String>(), true);		
		System.out.println(out);
		
		
	}
	@Test
	public void testNormalizeN3(){
		System.out.println(RDFWriteUtils.normalizeN3("123:.456+789-(){}[],.=%$#!asdf"));
	}
	@Test
	public void testGetLastIndexOfDelimiter(){
		assertEquals(RDFWriteUtils.getLastIndexOfDelimiter("http://xmlns.com/foaf/0.1/gender.sp.123"),35);
		assertEquals(RDFWriteUtils.getLastIndexOfDelimiter("http://xmlns.com/foaf/0.1/gender/sp.123"),35);
		assertEquals(RDFWriteUtils.getLastIndexOfDelimiter("http://xmlns.com/foaf/0.1/gender.sp#123"),35);
		assertEquals(RDFWriteUtils.getLastIndexOfDelimiter("http://xmlns.com/foaf/0.1/gender.sp/123"),35);
		assertEquals(RDFWriteUtils.getLastIndexOfDelimiter("http://xmlns.com/foaf/0.1/gender.sp:123"),35);
	}

}
