package org.flightofstairs.honours.common;

import groovy.transform.Immutable;

import java.io.Serializable;

@Immutable
public class Call implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    String caller;
    String callee;
    String method;

	@Override
	public String toString() {
		return caller + " -> " + callee + "." + method + "()";
	}
}
