package org.flightofstairs.honours.common

class TestMain {
	public static void main(String... args) {
		CallGraph<String> g = new CallGraph<String>();
		
		[
			new Call("a", "b", "1"),
			new Call("a", "c", "2"),
			new Call("b", "c", "2"),
			new Call("c", "d", "3"),
			new Call("d", "b", "1"),
			new Call("a", "b", "1"),
			new Call("a", "c", "2"),
			new Call("b", "c", "2"),
		].each { g.addCall(it) }
					
		println g.calls(false);
	}
}

