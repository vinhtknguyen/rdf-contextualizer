package org.knoesis.rdf.sp.parser;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.ConstantsTest;

public class ResourceManagerTest {

	ResourceManager manager;
	String task = Constants.PROCESSING_TASK_GENERATE;
	String ext = Constants.NTRIPLE_EXT;
	@Before
	public void setUp() throws Exception {
		manager = new ResourceManager(1, Constants.PROCESSING_TASK_GENERATE);
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
		manager.put(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test1.nq", ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test1_sp.nt", task, ext);
		manager.put(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test2.nq", ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test2_sp.nt", task, ext);
		System.out.println("has next: " + manager.hasNext());
	}

	@Test
	public void testCanExecuteNextElement() {
		manager.put(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test1.nq", ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test1_sp.nt", task, ext);
		manager.put(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test2.nq", ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test2_sp.nt", task, ext);
		System.out.println("can execute next element: " + manager.canExecuteNextElement());
	}

	@Test
	public void testPut() {
		manager.put(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test1.nq", ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test1_sp.nt", task, ext);
		manager.put(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test2.nq", ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test2_sp.nt", task, ext);
		manager.put(ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test3.nq", ConstantsTest.test_data_dir + "/" + ConstantsTest.test_ng + "/test1_sp.nt", task, ext);
		assertEquals(manager.size(), 2);
	}

}
