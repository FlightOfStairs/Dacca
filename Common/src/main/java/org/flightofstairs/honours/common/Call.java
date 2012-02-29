package org.flightofstairs.honours.common;

import java.io.Serializable;

public final class Call implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    final String caller;
    final String callee;
    final String method;
    
    public Call(final String caller, final String callee, final String method) {
		this.caller = caller;
		this.callee = callee;
		this.method = method;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Call other = (Call) obj;
		if ((this.caller == null) ? (other.caller != null) : !this.caller.equals(other.caller)) {
			return false;
		}
		if ((this.callee == null) ? (other.callee != null) : !this.callee.equals(other.callee)) {
			return false;
		}
		if ((this.method == null) ? (other.method != null) : !this.method.equals(other.method)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + (this.caller != null ? this.caller.hashCode() : 0);
		hash = 83 * hash + (this.callee != null ? this.callee.hashCode() : 0);
		hash = 83 * hash + (this.method != null ? this.method.hashCode() : 0);
		return hash;
	}
	
	@Override
	public String toString() {
		return caller + " -> " + callee + "." + method + "()";
	}
}
