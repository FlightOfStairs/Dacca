package org.flightofstairs.honours.analysis

import edu.uci.ics.jung.graph.Graph
import org.flightofstairs.honours.common.CallGraph

import org.gcontracts.annotations.Ensures

public class SimpleScorer<V extends Serializable> implements ClassScorer {
	
	Closure scoreExtractor = { 1.0 }
	
	private final CallGraph callGraph;
	
	@Ensures({ this.callGraph != null })
	public SimpleScorer(CallGraph callGraph) {
		this.callGraph = callGraph;
	}

	@Ensures({ result.keySet().containsAll(callGraph.classes()) })
	public Map<V, Double> rank() {
		def results = [:];
		
		Graph graph = callGraph.getGraph();
		
		callGraph.classes().each {
			results[it] = scoreExtractor(it, graph);
		}
		
		return results;
	}

}