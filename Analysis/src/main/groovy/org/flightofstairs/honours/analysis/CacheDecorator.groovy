package org.flightofstairs.honours.analysis

import org.flightofstairs.honours.common.CallGraph;
import org.flightofstairs.honours.common.ExclusiveGraphUser;
import org.flightofstairs.honours.common.CallGraphListener;

import groovy.transform.Synchronized

import org.gcontracts.annotations.*

public class CacheDecorator implements ClassScorer {
	private final CallGraph callGraph;
	
	private final ClassScorer delegate;
	
	private final Object cacheLock = new Object();
	
	private Map<String, Double> cache;
	
	@Requires({ callGraph != null && delegate != null && ! (delegate instanceof CacheDecorator) })
	public CacheDecorator(CallGraph callGraph, ClassScorer delegate) {
		this.callGraph = callGraph;
		this.delegate = delegate;
		
		callGraph.addListener({ synchronized(cacheLock) { cache = null } } as CallGraphListener)
	}

	@Ensures({ result.keySet().containsAll(callGraph.classes()) })
	@Synchronized("cacheLock")
	public Map<String, Double> rank() {
		if(cache != null) return cache;
		
		if(! delegate instanceof CacheDecorator) {
			callGraph.runExclusively({
				cache = delegate.rank();
			} as ExclusiveGraphUser);
		} else {
			cache = delegate.rank();
		}
		return cache;
	}
}

