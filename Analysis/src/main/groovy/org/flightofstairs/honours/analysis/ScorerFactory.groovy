package org.flightofstairs.honours.analysis

import org.apache.commons.collections15.Transformer;

public class ScorerFactory {
	
	public static Map<String, Closure> scorers = Collections.unmodifiableMap(
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
		"Class Connectivity" : { new Connectivity(it) },
		"Method Connectivity" : { new MethodConnectivity(it) }
	);
}