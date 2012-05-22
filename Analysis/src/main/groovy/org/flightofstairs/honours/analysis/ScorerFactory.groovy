package org.flightofstairs.honours.analysis

import org.apache.commons.collections15.Transformer;

public class ScorerFactory {
	
	public static Map<String, Closure> scorers = (Map<String, Closure>) Collections.unmodifiableMap(
		"Total HITS" : { new HITSScorer(it) },
		"Weighted HITS" : { new HITSWeighted(it) },
		"Service Use" : { 
			def scorer = new HITSScorer(it)
			scorer.@scoreExtractor = { it.hub } as Transformer
			return scorer
		},
		"Service Provision" : { 
			def scorer = new HITSScorer(it)
			scorer.@scoreExtractor = { it.authority } as Transformer
			return scorer
		},
		"Coupling Degree" : {
			def scorer = new SimpleScorer(it)
			scorer.@scoreExtractor = { item, graph ->
				def methodConnections = graph.getOutEdges(item).plus(graph.getInEdges(item))*.callVariety().plus([0]).sum()
				def classConnections = graph.getSuccessorCount(item) + graph.getPredecessorCount(item)
				
				return classConnections == 0 ? 0 : methodConnections / classConnections
			}
			return scorer;
		},
		"Class Connectivity" : {
			def scorer = new SimpleScorer(it)
			scorer.@scoreExtractor = { item, graph -> graph.getSuccessorCount(item) + graph.getPredecessorCount(item) }
			return scorer;
		},
		"Method Connectivity" : {
			def scorer = new SimpleScorer(it)
			scorer.@scoreExtractor = { item, graph ->
				graph.getOutEdges(item).plus(graph.getInEdges(item))*.callVariety().plus([0]).sum()
			}
			return scorer;
		}
	);
}