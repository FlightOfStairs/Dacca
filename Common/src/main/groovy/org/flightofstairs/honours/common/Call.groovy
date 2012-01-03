package org.flightofstairs.honours.common;


@Immutable
final class Call implements Serializable {
	String caller, callee, method;
	
	@Override
	public String toString() { return "$caller -> $callee.$method()"; }
}
