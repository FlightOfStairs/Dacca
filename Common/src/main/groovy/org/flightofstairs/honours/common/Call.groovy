package org.flightofstairs.honours.common;

import java.io.Serializable;

import org.gcontracts.annotations.*


// This might hurt performance too much to be wise.
@Invariant({ caller.length() != 0 && callee.length() != 0 && method.length() != 0 })
public class Call implements Serializable {
	public final String caller;
	public final String callee;
	
	public final String method;
	
	public Call(String caller, String callee, String method) {
		this.caller = caller;
		this.callee = callee;
		this.method = method;
	}
	
	@Override
	public String toString() { return "$caller -> $callee.$method()"; }
}
