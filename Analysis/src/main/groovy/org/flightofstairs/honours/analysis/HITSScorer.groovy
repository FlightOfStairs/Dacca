package org.flightofstairs.honours.analysis

import edu.uci.ics.jung.graph.Graph
import edu.uci.ics.jung.algorithms.scoring.HITS

import org.apache.commons.collections15.Transformer;

import org.flightofstairs.honours.common.CallGraph

import org.gcontracts.annotations.*

public class HITSScorer<V extends Serializable> implements ClassScorer {
	
	Transformer<HITS.Scores, Double> scoreExtractor = { it.authority + it.hub } as Transformer;
	
	public static final double ALPHA = 0;
	
	public String getName() { return "Unweighted HITS"; }
	
	public Map<V, Double> rank(CallGraph<V> callGraph) {
		def results = [:]
		
		Graph<V, ?> graph = callGraph.getGraph();
		
		def hits = new HITS(graph, ALPHA);
		
		hits.evaluate();
		
		callGraph.classes().each {
			results[it] = scoreExtractor.transform(hits.getVertexScore(it));
		}
		
		return results;
	}
}