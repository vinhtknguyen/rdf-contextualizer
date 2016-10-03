package org.knoesis.rdf.sp.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.model.PrefixTrie;
import org.knoesis.rdf.sp.model.SPNode;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;

import com.romix.scala.collection.concurrent.TrieMap;

public class SPNodeTest {

	
	@Before
	public void setUp() throws Exception {
		RDFWriteUtils.trie = new TrieMap<String,String>();
		RDFWriteUtils.trie.put("http://xmlns.com/foaf/0.1/", "foaf");
		RDFWriteUtils.trie.put("http://xmlns.com/foaf/0.1/gender_sp/", "foafg");
		RDFWriteUtils.trie.put("http://www.w3.org/2003/01/geo/wgs84_pos#", "geo");
		RDFWriteUtils.trie.put("http://www.w3.org/2003/01/geo/wgs84_pos#lat_sp/", "geol");
		RDFWriteUtils.trie.put("http://dbpedia.org/resource/", "dbr");
		RDFWriteUtils.trie.put("http://yago-knowledge.org/", "yago");
		RDFWriteUtils.trie.put("http://yago-knowledge.org/resource/", "yagoresource");
		RDFWriteUtils.trie.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");
		RDFWriteUtils.trie.put("http://www.w3.org/ns/prov#", "prov");
	}

	@Test
	public void testToN3() {
		assertEquals(new SPNode("http://www.w3.org/2003/01/geo/wgs84_pos#lat_sp/123").toN3().getShorten(), "geol:123");
		assertTrue(new SPNode("http://www.w3.org/2003/01/geo/wgs84_pos#long.sp.123").toN3().getShorten().contains("sp%2E123"));
	}


	@Test
	public void testToNT() {
	}

}
