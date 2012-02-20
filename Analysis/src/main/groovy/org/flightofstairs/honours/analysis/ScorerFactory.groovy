package org.flightofstairs.honours.analysis

public class ScorerFactory {
	public static Map<String, Closure> scorers = Collections.unmodifiableMap(
		"HITS" : { new HITSScorer(it) },
		"Weighted HITS" : { new HITSWeighted(it) },
		"Connectivity" : { new Connectivity(it) },
		"Method Connectivity" : { new MethodConnectivity(it) }
	);
}