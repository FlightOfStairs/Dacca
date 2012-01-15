package org.flightofstairs.honours.analysis

public class Scorers<V> {
	public static List<ClassScorer> scorers = [new Connectivity(), new HITSWeighted(), new HITSScorer(), new MethodConnectivity()];
}
