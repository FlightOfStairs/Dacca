package org.flightofstairs.honours.display;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.Color;
import javax.swing.JPanel;

import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.util.Animator;
import edu.uci.ics.jung.algorithms.layout.util.VisRunner;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.Predicate;

import org.flightofstairs.honours.common.CallGraph;
import org.flightofstairs.honours.common.CallGraphListener;
import org.flightofstairs.honours.analysis.ClassScorer;
import org.flightofstairs.honours.analysis.NullScorer;
import org.flightofstairs.honours.analysis.CacheDecorator;
import org.flightofstairs.honours.analysis.RankDecorator;

import java.util.logging.Logger;

import org.gcontracts.annotations.*

@Invariant({
		graphLayout != null &&
		scorer != null
	})
public class GraphPanel<V extends Serializable> extends JPanel {
	public static final int DEFAULT_X = 800;
	public static final int DEFAULT_Y = 800;
	
	private final CallGraph<V> callGraph;
	
	private final AbstractLayout<V, ?> graphLayout;
	private final VisualizationViewer<V, ?> viewer;
	
	private ClassScorer scorer = new NullScorer();
	
	public GraphPanel(CallGraph<V> callGraph) {
		this.callGraph = callGraph;
		
		graphLayout = new FRLayout(callGraph.getGraph());
		
		graphLayout.setSize(new Dimension(DEFAULT_X, DEFAULT_Y));
		
		Relaxer relaxer = new VisRunner((IterativeContext) graphLayout);
		relaxer.stop();
		relaxer.prerelax();

		Layout staticLayout = new StaticLayout(callGraph.getGraph(), graphLayout);

        viewer = new VisualizationViewer(staticLayout, new Dimension(DEFAULT_X, DEFAULT_Y));
		viewer.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
		
		viewer.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
				
		viewer.getRenderContext().setVertexFillPaintTransformer({
				new Color(100, 255, 100, (int) (255 * scorer.rank(callGraph)[it])) 
			} as Transformer)
				
		viewer.setGraphMouse(new DefaultModalGraphMouse());
		
		setLayout(new BorderLayout());
		add(viewer);
		
		refreshTransformers();
		
		validate();
		
		callGraph.addListener({ redraw() } as CallGraphListener);
	}
	
	@Requires({ scorer != null })
	public void setScorer(ClassScorer scorer) {
		scorer = new RankDecorator(scorer);
		scorer = new CacheDecorator(callGraph, scorer);
		this.scorer = scorer;

		refreshTransformers();
	}
	
	private void refreshTransformers() {
		viewer.getRenderContext().setVertexIncludePredicate(new HidePredicate(callGraph, scorer, viewer.getRenderContext(), 0.20))
		
		def hidePredicate = new HidePredicate(callGraph, scorer, viewer.getRenderContext());
		
		viewer.getRenderContext().setVertexShapeTransformer(new HideTransformers(
				hidePredicate,
				new TextFitShape(viewer.getRenderContext()),
				{ new Rectangle(-3, -3, 6, 6) } as Transformer
				))
			
		viewer.getRenderContext().setVertexLabelTransformer(new HideTransformers(
			hidePredicate,
			NameTransformers.Short,
			{ "" } as Transformer
			));

		redraw();
	}
	
	private void redraw() {
		graphLayout.initialize();
		
		Relaxer relaxer = new VisRunner((IterativeContext)graphLayout);
        relaxer.stop();
		
		relaxer.prerelax();
        StaticLayout staticLayout = new StaticLayout(callGraph.getGraph(), graphLayout);
		
		LayoutTransition lt = new LayoutTransition(viewer, viewer.getGraphLayout(), staticLayout);
		
		Animator animator = new Animator(lt);
		animator.start();
		
		relaxer.resume();
	}
}

