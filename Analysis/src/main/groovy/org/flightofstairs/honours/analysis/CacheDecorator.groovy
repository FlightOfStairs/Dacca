package org.flightofstairs.honours.analysis

import org.flightofstairs.honours.common.CallGraph;
import org.flightofstairs.honours.common.ExclusiveGraphUser;
import org.flightofstairs.honours.common.CallGraphListener;

import groovy.transform.Synchronized

import org.gcontracts.annotations.*

public class CacheDecorator<V extends Serializable> implements ClassScorer {
	private final CallGraph<V> callGraph;
	
	private final ClassScorer<V> delegate;
	
	private final Object cacheLock = new Object();
	
	private Map<V, Double> cache;
	
	@Requires({ callGraph != null && delegate != null && ! (delegate instanceof CacheDecorator) })
	public CacheDecorator(CallGraph<V> callGraph, ClassScorer<V> delegate) {
		this.callGraph = callGraph;
		this.delegate = delegate;
		
		callGraph.addListener({ synchronized(cacheLock) { cache = null } } as CallGraphListener)
	}
	
	@Synchronized("cacheLock")
	public Map<V, Double> rank() {
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

