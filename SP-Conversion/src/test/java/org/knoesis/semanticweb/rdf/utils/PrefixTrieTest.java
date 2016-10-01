package org.knoesis.semanticweb.rdf.utils;

import static org.junit.Assert.*;

import org.apache.jena.graph.NodeFactory;
import org.junit.Before;
import org.junit.Test;

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
		assertEquals(trie.shortenURIWithPrefix(NodeFactory.createURI("http://xmlns.com/foaf/0.1/gender_sp/123")).getShortenURI(),"foafg:123");
		assertEquals(trie.shortenURIWithPrefix(NodeFactory.createURI("http://xmlns.com/foaf/0.1/gender")).getShortenURI(),"foaf:gender");
		assertEquals(trie.shortenURIWithPrefix(NodeFactory.createURI("http://xmlns.com/foaf/0.1/gender_sp/123")).getShortenURI(),"foafg:123");
		assertEquals(trie.shortenURIWithPrefix(NodeFactory.createURI("http://www.w3.org/2003/01/geo/wgs84_pos#asdf")).getShortenURI(),"geo:asdf");
		assertEquals(trie.shortenURIWithPrefix(NodeFactory.createURI("http://www.w3.org/2003/01/geo/wgs84_pos#lat_sp/123")).getShortenURI(),"geol:123");
		assertEquals(trie.shortenURIWithPrefix(NodeFactory.createURI("http://yago-knowledge.org/resource/BarackObama")).getShortenURI(),"yagoresource:BarackObama");
		assertEquals(trie.shortenURIWithPrefix(NodeFactory.createURI("http://yago-knowledge.org/class")).getShortenURI(),"yago:class");
		assertEquals(trie.shortenURIWithPrefix(NodeFactory.createURI("http://www.w3.org/ns/prov#wasDerivedFrom")).getShortenURI(),"prov:wasDerivedFrom");
		assertEquals(trie.shortenURIWithPrefix(NodeFactory.createURI("http://www.w3.org/ns/prov#something")).getShortenURI(),"prov:something");
		assertTrue(trie.shortenURIWithPrefix(NodeFactory.createURI("http://www.w3.org/ns/pro#something")).getShortenURI().contains(":"));
		System.out.println(trie.shortenURIWithPrefix(NodeFactory.createURI("http://www.w3.org/2003/01/geo/wgs84_pos#long_sp/123")).toString());
		assertTrue(trie.shortenURIWithPrefix(NodeFactory.createURI("http://www.w3.org/2003/01/geo/wgs84_pos#long_sp/123")).getShortenURI().contains("123"));

		
	}

	@Test
	public void testNormalizeN3(){
		System.out.println(trie.normalizeN3("123:.456+789-(){}[],.=%$#!asdf"));
	}
}
