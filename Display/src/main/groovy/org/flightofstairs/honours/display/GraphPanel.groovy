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

import org.apache.commons.collections15.Transformer;

import org.flightofstairs.honours.common.CallGraph;
import org.flightofstairs.honours.common.CallGraphListener;

import org.gcontracts.annotations.*

@Invariant({ graphLayout != null })
public class GraphPanel<V extends Serializable> extends JPanel implements CallGraphListener {
	public static final int DEFAULT_X = 800;
	public static final int DEFAULT_Y = 800;
	
	private static final Color CLASS_COLOUR = new Color(255, 255, 255, 220);
	
	public static final Transformer<V, String> FullClassNameTransformer = {
		it.toString()
	} as Transformer;
	
	public static final Transformer<V, String> ShortClassNameTransformer = {
		def parts = it.toString().tokenize('.');
		return parts[parts.size() - 1];
	} as Transformer;
		
	private final CallGraph<V> callGraph;
	
	private AbstractLayout<V, ?> graphLayout;
	private final VisualizationViewer<V, ?> viewer;
	
	public GraphPanel(CallGraph<V> callGraph) {
		this.callGraph = callGraph;
		
		graphLayout = new FRLayout2(callGraph.getGraph());
		
		graphLayout.setSize(new Dimension(DEFAULT_X, DEFAULT_Y));
		
		Relaxer relaxer = new VisRunner((IterativeContext)graphLayout);
		relaxer.stop();
		relaxer.prerelax();

		Layout staticLayout = new StaticLayout(callGraph.getGraph(), graphLayout);

        viewer = new VisualizationViewer(staticLayout, new Dimension(DEFAULT_X, DEFAULT_Y));
		
		viewer.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
		
		viewer.getRenderContext().setEdgeShapeTransformer(new EdgeShape.CubicCurve());
		
		setClassTransformer(ShortClassNameTransformer);
		
		viewer.getRenderContext().setVertexShapeTransformer(new TextFitShape(viewer.getRenderContext()))
		viewer.getRenderContext().setVertexFillPaintTransformer({ CLASS_COLOUR } as Transformer)
		
		setLayout(new BorderLayout());
		add(viewer);
		
		validate();
		
		callGraph.addListener(this);
	}
	
	public void setClassTransformer(Transformer<V, String> transformer) {
		viewer.getRenderContext().setVertexLabelTransformer(transformer);
	}
	
	@Requires({callGraph == this.callGraph})
	public void callGraphChange(CallGraph callGraph) {
		graphLayout.initialize();
		
		Relaxer relaxer = new VisRunner((IterativeContext)graphLayout);
        relaxer.stop();
		
		relaxer.prerelax();
        StaticLayout staticLayout = new StaticLayout(callGraph.getGraph(), graphLayout);
		
		LayoutTransition lt = new LayoutTransition(viewer, viewer.getGraphLayout(), staticLayout);
		
		Animator animator = new Animator(lt);
		animator.start();
		
		viewer.repaint();

		relaxer.resume();
	}
}

