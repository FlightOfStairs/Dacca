package org.flightofstairs.honours.analysis

import edu.uci.ics.jung.graph.Graph
import edu.uci.ics.jung.algorithms.scoring.HITS

import org.apache.commons.collections15.Transformer;

import org.flightofstairs.honours.common.CallGraph;
import org.flightofstairs.honours.common.ClassRelation;

import org.gcontracts.annotations.*

public class HITSWeighted<V extends Serializable> implements ClassScorer {
	
	Transformer<HITS.Scores, Double> scoreExtractor = { it.authority + it.hub } as Transformer;
	
	public static final double ALPHA = 0;
	
	private final CallGraph callGraph;
	
	public HITSWeighted(CallGraph<String> callGraph) {
		this.callGraph = callGraph;
	}

	@Ensures({ result.keySet().containsAll(callGraph.classes()) })
	public Map<V, Double> rank() {
		def results = [:]
		
		Graph<V, ?> graph = callGraph.getGraph();
		
		def hits = new HITS(graph, { it.callVariety() } as Transformer , ALPHA);
		
		hits.evaluate();
		
		callGraph.classes().each {
			results[it] = scoreExtractor.transform(hits.getVertexScore(it));
		}
		
		return results;
	}
}