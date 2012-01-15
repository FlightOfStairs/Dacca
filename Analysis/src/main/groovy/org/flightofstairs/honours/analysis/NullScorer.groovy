package org.flightofstairs.honours.analysis

import org.flightofstairs.honours.common.CallGraph;

class NullScorer<V extends Serializable> implements ClassScorer {
	public String getName() { return "Null Scorer"; }
	
	public Map<V, Double> rank(CallGraph<V> callGraph) {
		def results = [:]
		
		callGraph.classes().each {
			results[it] = 1.0;
		}
		
		return results;
	}
}

