package org.flightofstairs.honours.capture;

import java.io.Serializable;

public class Call implements Serializable {
	public final String caller;
	public final String callee;
	
	public final String method;
	
	public Call(String caller, String callee, String method) {
		this.caller = caller;
		this.callee = callee;
		this.method = method;
	}
}
