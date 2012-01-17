package org.flightofstairs.honours.common

class CallTest extends GroovyTestCase {
	void testEquals() {
		def a = new Call("Class1", "Class2", "method1");
		def b = new Call("Class1", "Class2", "method1");
		
		assertNotSame(a, b);
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		
		[
			new Call("Class2", "Class2", "method1"),
			new Call("Class1", "Class1", "method1"),
			new Call("Class1", "Class2", "method2"),
			new Call("Class2", "Class1", "method1"),
		].each { assertFalse(a.equals("it")) }
	}
	
	void testBasic() {
		def a = new Call("Class1", "Class2", "method1");
		
		assertEquals(a.caller, "Class1");
		assertEquals(a.callee, "Class2");
		assertEquals(a.method, "method1");
	}
	
	void testToString() {
		def a = new Call("Class1", "Class2", "method1");
		assertEquals("Class1 -> Class2.method1()", a.toString());
	}
}

