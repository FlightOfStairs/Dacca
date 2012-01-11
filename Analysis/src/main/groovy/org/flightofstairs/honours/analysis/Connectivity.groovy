package org.flightofstairs.honours.analysis

import edu.uci.ics.jung.graph.Graph

import org.flightofstairs.honours.common.CallGraph;

public class Connectivity<V extends Serializable> implements ClassScorer {
	public String getName() { return "Connectivity"; }
	
	public Map<V, Double> rank(CallGraph<V> callGraph) {
		def results = [:];
		
		Graph<V, ?> graph = callGraph.getGraph();
		
		callGraph.classes().each {
			results[it] = graph.getSuccessorCount(it) + graph.getPredecessorCount(it);
		}
		
		return results;
	}
}

