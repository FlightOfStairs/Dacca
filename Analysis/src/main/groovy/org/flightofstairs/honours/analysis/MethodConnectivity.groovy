package org.flightofstairs.honours.analysis

import edu.uci.ics.jung.graph.Graph

import org.flightofstairs.honours.common.CallGraph;

public class MethodConnectivity<V extends Serializable> implements ClassScorer {
	private final CallGraph callGraph;
	
	public MethodConnectivity(CallGraph<String> callGraph) {
		this.callGraph = callGraph;
	} 

	public Map<V, Double> rank() {
		def results = [:];
		
		Graph<V, ?> graph = callGraph.getGraph();
		
		callGraph.classes().each {
			int total = 0
			graph.getOutEdges(it).each { e ->
				total += e.callVariety()
			}
			
			graph.getInEdges(it).each { e ->
				total += e.callVariety()
			}
			
			results[it] = total;
		}
		
		return results;
	}
}