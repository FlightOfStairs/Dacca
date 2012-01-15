package org.flightofstairs.honours.analysis

import org.flightofstairs.honours.common.CallGraph;
import org.flightofstairs.honours.common.CallGraphListener;

import groovy.transform.Synchronized

import org.gcontracts.annotations.*

public class CacheDecorator<V extends Serializable> implements ClassScorer {
	private final CallGraph<V> callGraph;
	
	private final ClassScorer<V> delegate;
	
	private Object cacheLock = new Object();
	private Map<V, Double> cache;
	
	@Requires({ callGraph != null && delegate != null && ! (delegate instanceof CacheDecorator) })
	public CacheDecorator(CallGraph<V> callGraph, ClassScorer<V> delegate) {
		this.callGraph = callGraph;
		this.delegate = delegate;
		
		callGraph.addListener({ synchronized(cacheLock) { cache = null } } as CallGraphListener)
	}
	
	@Requires({ this.callGraph == callGraph })
	@Ensures({ result != null })
	@Synchronized("cacheLock")
	public Map<V, Double> rank(CallGraph<V> callGraph) {
		if(cache == null) cache = delegate.rank(callGraph);
		return cache;
	}
	
	public String getName() {
		return delegate.getName();
	}
}

