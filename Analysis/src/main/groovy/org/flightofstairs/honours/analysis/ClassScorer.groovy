package org.flightofstairs.honours.analysis

import org.flightofstairs.honours.common.CallGraph;

import org.gcontracts.annotations.*

public interface ClassScorer<V extends Serializable> {
	@Ensures({ result != null })
	public Map<V, Double> rank();
}