package org.knoesis.rdf.sp.model;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.model.SPNode;

import com.romix.scala.collection.concurrent.TrieMap;

public class SPNodeTest {

	Map<String,String> trie = new TrieMap<String,String>();
	@Before
	public void setUp() throws Exception {
		trie = new TrieMap<String,String>();
		trie.put("http://xmlns.com/foaf/0.1/", "foaf");
		trie.put("http://xmlns.com/foaf/0.1/gender_sp/", "foafg");
		trie.put("http://www.w3.org/2003/01/geo/wgs84_pos#", "geo");
		trie.put("http://www.w3.org/2003/01/geo/wgs84_pos#lat_sp/", "geol");
		trie.put("http://dbpedia.org/resource/", "dbr");
		trie.put("http://yago-knowledge.org/", "yago");
		trie.put("http://yago-knowledge.org/resource/", "yagoresource");
		trie.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");
		trie.put("http://www.w3.org/ns/prov#", "prov");
	}

	@Test
	public void testToN3() {
		assertEquals(new SPNode("http://www.w3.org/2003/01/geo/wgs84_pos#lat_sp/123").toN3(new TrieMap<String,String>(), trie).getShorten(), "geol:123");
		assertTrue(new SPNode("http://www.w3.org/2003/01/geo/wgs84_pos#long.sp.123").toN3(new TrieMap<String,String>(), trie).getShorten().contains("sp%2E123"));
	}


	@Test
	public void testToNT() {
	}

}