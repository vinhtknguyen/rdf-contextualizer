package org.knoesis.rdf.sp.parser;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.utils.ConstantsTest;

public class ResourceManagerTest {

	ResourceManager manager;
	@Before
	public void setUp() throws Exception {
		manager = new ResourceManager(1);
	}

	@Test
	public void testStartParserElement() {
	}

	@Test
	public void testFinishParserElemnet() {
	}

	@Test
	public void testNext() {
	}

	@Test
	public void testHasNext() {
		manager.put(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test1.nq", ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test1_sp.nt");
		manager.put(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test2.nq", ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test2_sp.nt");
		System.out.println("has next: " + manager.hasNext());
	}

	@Test
	public void testCanExecuteNextElement() {
		manager.put(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test1.nq", ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test1_sp.nt");
		manager.put(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test2.nq", ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test2_sp.nt");
		System.out.println("can execute next element: " + manager.canExecuteNextElement());
	}

	@Test
	public void testPut() {
		manager.put(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test1.nq", ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test1_sp.nt");
		manager.put(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test2.nq", ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test2_sp.nt");
		manager.put(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test3.nq", ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test1_sp.nt");
		assertEquals(manager.size(), 2);
	}

}
