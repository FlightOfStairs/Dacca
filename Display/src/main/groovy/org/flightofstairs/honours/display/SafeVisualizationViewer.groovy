package org.flightofstairs.honours.display

import org.flightofstairs.honours.common.CallGraph;
import org.flightofstairs.honours.common.ExclusiveGraphUser;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.algorithms.layout.Layout;

import java.awt.Graphics;

class SafeVisualizationViewer extends VisualizationViewer {
	private final CallGraph callGraph;
	
	public SafeVisualizationViewer(CallGraph callGraph, Layout layout) {
		super(layout);
		
		this.callGraph = callGraph;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		callGraph.runExclusively({
				super.paintComponent(g);
		} as ExclusiveGraphUser);
	}
}

