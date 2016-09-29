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
		trie.insert("http://dbpedia.org/resource/", "dbr");
		trie.insert("http://yago-knowledge.org/resource/", "yago");
		trie.insert("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");
		trie.insert("http://www.w3.org/ns/prov#", "prov");
	}

	@Test
	public void testInsert() {
	}

	@Test
	public void testSearchPrefix() {
		if (trie == null) return;
		assertEquals(trie.searchPrefix("http://yago-knowledge.org/resource/"),"yago");
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
		assertEquals(trie.shortenURIWithPrefix(NodeFactory.createURI("http://yago-knowledge.org/resource/BarackObama"))[0],"yago:BarackObama");
		assertEquals(trie.shortenURIWithPrefix(NodeFactory.createURI("http://www.w3.org/ns/prov#wasDerivedFrom"))[0],"prov:wasDerivedFrom");
		assertEquals(trie.shortenURIWithPrefix(NodeFactory.createURI("http://www.w3.org/ns/prov#something"))[0],"prov:something");
		assertNotEquals(trie.shortenURIWithPrefix(NodeFactory.createURI("http://www.w3.org/ns/pro#something"))[0],"prov:something");
		assertEquals(trie.shortenURIWithPrefix(NodeFactory.createURI("http://www.me.org/ns/prov#something"))[0],"<http://www.me.org/ns/prov#something>");

	}

}
