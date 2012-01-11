package org.flightofstairs.honours.analysis

public class Scorers<V> {
	public static List<ClassScorer<V>> scorers = [new Connectivity(), new HITSWeighted(), new HITSScorer(), new MethodConnectivity()];
}
