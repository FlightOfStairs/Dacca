package org.flightofstairs.honours.common

import edu.uci.ics.jung.graph.Graph
import edu.uci.ics.jung.graph.util.Graphs

class GraphTest extends GroovyTestCase {
	
	void testAdd() {
		CallGraph g = new CallGraph();
		
		for(caller in 'a'..'d')
			for(callee in 'a'..'d')
				for(method in 1..10)
					g.addCall(new Call(caller + "", callee + "", method + ""));
	}
	
	void testLoadSave() {
		CallGraph<String> g = new CallGraph<String>();
		
		[
			new Call("a", "b", "1"),
			new Call("a", "c", "2"),
			new Call("b", "c", "2"),
			new Call("c", "d", "3"),
			new Call("d", "b", "1"),
		].each { g.addCall(it) }
				
		def file = File.createTempFile("test-", ".callgraph");
		
		g.save(file);
		
		CallGraph<String> loaded = CallGraph.open(file);
		
		assertEquals(5, loaded.calls().size());
		assertEquals(4, loaded.classes().size());
	}
	
	void testEdges() {
		CallGraph<String> g = new CallGraph<String>();
		
		[
			new Call("a", "b", "1"),
			new Call("a", "c", "2"),
			new Call("b", "c", "2"),
			new Call("c", "d", "3"),
			new Call("d", "b", "1"),
			new Call("a", "b", "1"),
			new Call("a", "c", "2"),
			new Call("a", "c", "4"),
			new Call("b", "c", "2"),
		].each { g.addCall(it) }
		
		assertEquals(6, g.calls().size());
		assertEquals(6, g.calls(true).size());
		assertEquals(9, g.calls(false).size());
		
		def expected = [
			new Call("a", "b", "1"),
			new Call("a", "c", "2"),
			new Call("b", "c", "2"),
			new Call("c", "d", "3"),
			new Call("d", "b", "1"),
			new Call("a", "c", "4"),
		]
		
		assertTrue(expected.containsAll(g.calls(true)))
		assertTrue(expected.containsAll(g.calls(false)))
	}
	
	void testGraph() {
		CallGraph<String> g = new CallGraph<String>();
		
		[
			new Call("a", "b", "1"),
			new Call("a", "c", "2"),
			new Call("b", "c", "2"),
			new Call("c", "d", "3"),
			new Call("d", "b", "1"),
		].each { g.addCall(it) }
		
		Graph actual = g.getGraph();
		
		try { actual.addVertex("omgwtfbbq") }
		catch (Exception e) { };
		
		assertEquals(4, g.classes().size());
	}
	
	void testMerge() {
		CallGraph<String> a = new CallGraph<String>();
		CallGraph<String> b = new CallGraph<String>();
		
		[
			new Call("a", "b", "1"),
			new Call("a", "c", "2"),
			new Call("b", "c", "2"),
			new Call("c", "d", "3"),
			new Call("d", "b", "1"),
		].each { a.addCall(it) }
		
		[
			new Call("a", "b", "1"),
			new Call("a", "b", "4"),
			new Call("b", "e", "4"),
			new Call("d", "e", "4"),
		].each { b.addCall(it) }
		
		a.merge(b);
		
		assertEquals(5, a.classes().size());
		assertEquals(8, a.calls().size());
		assertEquals(9, a.calls(false).size());
	}
	
	void testExclusive() {
		CallGraph g = new CallGraph();
		
		Thread t = new Thread({
				g.runExclusively({ sleep(500); it.addCall(new Call("a", "b", "c"));
				} as ExclusiveGraphUser)
			} as Runnable);
		
		assertEquals(0, g.classes().size());
		
		t.start();
		
		sleep(10);
		
		assertEquals(2, g.classes().size());
	}
}