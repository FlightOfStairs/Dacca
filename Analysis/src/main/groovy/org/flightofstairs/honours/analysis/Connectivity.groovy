package org.flightofstairs.honours.analysis

import edu.uci.ics.jung.graph.Graph

import org.flightofstairs.honours.common.CallGraph;

import org.gcontracts.annotations.*

public class Connectivity<V extends Serializable> implements ClassScorer {
	
	private final CallGraph callGraph;
	
	@Ensures({ this.callGraph != null })
	public Connectivity(CallGraph<String> callGraph) {
		this.callGraph = callGraph;
	} 

	public Map<V, Double> rank() {
		def results = [:];
		
		Graph graph = callGraph.getGraph();
		
		callGraph.classes().each {
			results[it] = graph.getSuccessorCount(it) + graph.getPredecessorCount(it);
		}
		
		return results;
	}
}

