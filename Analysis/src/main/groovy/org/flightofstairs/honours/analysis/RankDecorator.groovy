package org.flightofstairs.honours.analysis

import org.flightofstairs.honours.common.CallGraph;

import org.gcontracts.annotations.*

public class RankDecorator<V extends Serializable> implements ClassScorer {
	public static final double EPSILON = 0.000001;
	
	private final ClassScorer scorer;
	
	@Requires({ scorer != null })
	public RankDecorator(ClassScorer scorer) {
		this.scorer = scorer;
	}
	
	@Ensures({ result.values().every { it >= 0 && it <= 1 } })
	public Map<V, Double> rank(CallGraph<V> callGraph) {
		Map<V, Double> scores = scorer.rank(callGraph);
		
		def sortedClasses = scores.keySet().sort { a, b ->
			Math.abs(scores[a] - scores[b]) > EPSILON ?
				scores[a] <=> scores[b] :
				a <=> b
		}
		
		double scale = 1.0/(scores.size() + 1);
		
		for(i in 0..<scores.size())
			scores[sortedClasses[i]] = i * scale;
			
		return scores;
	}
	
	public String getName() {
		return "Ranked " + scorer.getName();
	}
}

