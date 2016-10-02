package org.knoesis.rdf.sp.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.model.PrefixTrie;
import org.knoesis.rdf.sp.model.SPNode;

public class PrefixTrieTest {

	PrefixTrie trie;
	@Before
	public void setUp() throws Exception {
		trie = new PrefixTrie();
		trie.insert("http://xmlns.com/foaf/0.1/", "foaf");
		trie.insert("http://xmlns.com/foaf/0.1/gender_sp/", "foafg");
		trie.insert("http://www.w3.org/2003/01/geo/wgs84_pos#", "geo");
		trie.insert("http://www.w3.org/2003/01/geo/wgs84_pos#lat_sp/", "geol");
		trie.insert("http://dbpedia.org/resource/", "dbr");
		trie.insert("http://yago-knowledge.org/", "yago");
		trie.insert("http://yago-knowledge.org/resource/", "yagoresource");
		trie.insert("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");
		trie.insert("http://www.w3.org/ns/prov#", "prov");
	}

	@Test
	public void testInsert() {
		
	}

	@Test
	public void testSearchPrefix() {
		if (trie == null) return;
		assertEquals(trie.searchPrefix("http://xmlns.com/foaf/0.1/"),"foaf");
		assertEquals(trie.searchPrefix("http://xmlns.com/foaf/0.1/gender_sp/"),"foafg");
		assertEquals(trie.searchPrefix("http://www.w3.org/2003/01/geo/wgs84_pos#"),"geo");
		assertNotEquals(trie.searchPrefix("http://www.w3.org/2003/01/geo/wgs84_pos#long_sp/"),"geol");
		assertEquals(trie.searchPrefix("http://yago-knowledge.org/"),"yago");
		assertEquals(trie.searchPrefix("http://yago-knowledge.org/resource/"),"yagoresource");
		assertEquals(trie.searchPrefix("http://www.w3.org/ns/prov#"),"prov");
		assertNotEquals(trie.searchPrefix("http://www.w3.org/ns/prov#something"),"prov");
		assertNotEquals(trie.searchPrefix("http://www.w3.org/ns/pro#"),"prov");
	}

	@Test
	public void testMatchingPrefix() {
	}

	@Test
	public void testSearchNode() {
	}
	
	@Test
	public void testShortenURIWithPrefix() {
		assertEquals(trie.shortenURIWithPrefix(new SPNode("http://xmlns.com/foaf/0.1/gender_sp/123")).getShorten(),"foafg:123");
		assertEquals(trie.shortenURIWithPrefix(new SPNode("http://xmlns.com/foaf/0.1/gender")).getShorten(),"foaf:gender");
		assertEquals(trie.shortenURIWithPrefix(new SPNode("http://xmlns.com/foaf/0.1/gender_sp/123")).getShorten(),"foafg:123");
		assertEquals(trie.shortenURIWithPrefix(new SPNode("http://www.w3.org/2003/01/geo/wgs84_pos#asdf")).getShorten(),"geo:asdf");
		assertEquals(trie.shortenURIWithPrefix(new SPNode("http://www.w3.org/2003/01/geo/wgs84_pos#lat_sp/123")).getShorten(),"geol:123");
		assertEquals(trie.shortenURIWithPrefix(new SPNode("http://yago-knowledge.org/resource/BarackObama")).getShorten(),"yagoresource:BarackObama");
		assertEquals(trie.shortenURIWithPrefix(new SPNode("http://yago-knowledge.org/class")).getShorten(),"yago:class");
		assertEquals(trie.shortenURIWithPrefix(new SPNode("http://www.w3.org/ns/prov#wasDerivedFrom")).getShorten(),"prov:wasDerivedFrom");
		assertEquals(trie.shortenURIWithPrefix(new SPNode("http://www.w3.org/ns/prov#something")).getShorten(),"prov:something");
		assertTrue(trie.shortenURIWithPrefix(new SPNode("http://www.w3.org/ns/pro#something")).getShorten().contains(":"));
		System.out.println(trie.shortenURIWithPrefix(new SPNode("http://www.w3.org/2003/01/geo/wgs84_pos#long_sp/123")).getShorten());
		
		assertTrue(trie.shortenURIWithPrefix(new SPNode("http://www.w3.org/2003/01/geo/wgs84_pos#long_sp.123")).getShorten().contains("123"));
		assertTrue(trie.shortenURIWithPrefix(new SPNode("http://www.w3.org/2003/01/geo/wgs84_pos#long_sp:123")).getShorten().contains("123"));
		assertTrue(trie.shortenURIWithPrefix(new SPNode("http://www.w3.org/2003/01/geo/wgs84_pos#long_sp#123")).getShorten().contains("123"));

		System.out.println("http://xmlns.com/foaf/0.1/gender.sp.123 to " + trie.shortenURIWithPrefix(new SPNode("http://xmlns.com/foaf/0.1/gender.sp.123")).getShorten());
		System.out.println("http://www.w3.org/2003/01/geo/wgs84_pos#long_sp.123 to " + trie.shortenURIWithPrefix(new SPNode("http://www.w3.org/2003/01/geo/wgs84_pos#long_sp.123")).getShorten());
		System.out.println("http://www.w3.org/2003/01/geo/wgs84_pos#long_sp:123 to " + trie.shortenURIWithPrefix(new SPNode("http://www.w3.org/2003/01/geo/wgs84_pos#long_sp:123")).getShorten());
		System.out.println("http://www.w3.org/2003/01/geo/wgs84_pos#long_sp#123 to " + trie.shortenURIWithPrefix(new SPNode("http://www.w3.org/2003/01/geo/wgs84_pos#long_sp#123")).getShorten());

	}

	@Test
	public void testNormalizeN3(){
		System.out.println(trie.normalizeN3("123:.456+789-(){}[],.=%$#!asdf"));
	}
	@Test
	public void testGetLastIndexOfDelimiter(){
		assertEquals(trie.getLastIndexOfDelimiter("http://xmlns.com/foaf/0.1/gender.sp.123"),35);
		assertEquals(trie.getLastIndexOfDelimiter("http://xmlns.com/foaf/0.1/gender/sp.123"),35);
		assertEquals(trie.getLastIndexOfDelimiter("http://xmlns.com/foaf/0.1/gender.sp#123"),35);
		assertEquals(trie.getLastIndexOfDelimiter("http://xmlns.com/foaf/0.1/gender.sp/123"),35);
		assertEquals(trie.getLastIndexOfDelimiter("http://xmlns.com/foaf/0.1/gender.sp:123"),35);
	}
	
}
