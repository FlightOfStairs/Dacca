package org.flightofstairs.honours.display

import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.Layer;

import edu.uci.ics.jung.graph.util.Context;

import org.apache.commons.collections15.Predicate;

import org.flightofstairs.honours.analysis.ClassScorer;
import org.flightofstairs.honours.common.CallGraph;

class HidePredicate implements Predicate {
	public static final double OUTER_ZOOM = 1;
	
	public final double offset;
	
	public final double maxScoreNeeded;
	public final double zoomForAll;
	
	private final CallGraph callGraph;
	private final ClassScorer scorer;
	private final RenderContext context;
	
	public HidePredicate(CallGraph callGraph, ClassScorer scorer, RenderContext context,
				double offset = 0.0, double maxScoreNeeded = 0.9, double zoomForAll = 5) {
		this.callGraph = callGraph;
		this.scorer = scorer;
		this.context = context;
		
		this.offset = offset;
		this.maxScoreNeeded = maxScoreNeeded;
		this.zoomForAll = zoomForAll;
	}
	
	boolean evaluate(input) {
		double scale = context.getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getScale()

		double div = zoomForAll - OUTER_ZOOM;
		double minScore = (maxScoreNeeded * zoomForAll) / div -(maxScoreNeeded * scale) / div;
		
		def vertex = input instanceof Context ? input.element : input;
		
		return scorer.rank(callGraph)[vertex] + offset > minScore 
	}
}

