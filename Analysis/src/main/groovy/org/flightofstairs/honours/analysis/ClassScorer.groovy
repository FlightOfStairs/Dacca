package org.flightofstairs.honours.analysis

import org.gcontracts.annotations.Ensures

public interface ClassScorer {
	@Ensures({ result != null })
	public Map<String, Double> rank();
}