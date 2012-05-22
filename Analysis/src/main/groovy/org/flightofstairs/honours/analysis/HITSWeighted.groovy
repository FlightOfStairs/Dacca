package org.flightofstairs.honours.analysis

import edu.uci.ics.jung.algorithms.scoring.HITS
import edu.uci.ics.jung.graph.Graph
import org.apache.commons.collections15.Transformer
import org.flightofstairs.honours.common.CallGraph
import org.gcontracts.annotations.Ensures

public class HITSWeighted<V extends Serializable> implements ClassScorer {
	
	Transformer<HITS.Scores, Double> scoreExtractor = { it.authority + it.hub } as Transformer;
	
	public static final double ALPHA = 0;
	
	private final CallGraph callGraph;
	
	public HITSWeighted(CallGraph callGraph) {
		this.callGraph = callGraph;
	}

	@Ensures({ result.keySet().containsAll(callGraph.classes()) })
	public Map<V, Double> rank() {
		def results = [:]
		
		Graph<V, ?> graph = callGraph.getGraph();
		
		def hits = new HITS(graph, { it.callVariety() } as Transformer , ALPHA);
		
		hits.evaluate();
		
		callGraph.classes().each {
			results[it] = scoreExtractor.transform((HITS.Scores) hits.getVertexScore(it))
		}
		
		return results;
	}
}