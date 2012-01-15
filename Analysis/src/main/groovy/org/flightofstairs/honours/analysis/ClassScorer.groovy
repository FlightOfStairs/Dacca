package org.flightofstairs.honours.analysis

import org.flightofstairs.honours.common.CallGraph;

import org.gcontracts.annotations.*

public interface ClassScorer<V extends Serializable> {
	
	@Requires({ callGraph != null })
	@Ensures({ result != null && result.keySet().containsAll(callGraph.classes()) })
	public Map<V, Double> rank(CallGraph<V> callGraph);
	
	@Ensures({ result != null && result.length != 0 })
	public String getName();
	
}