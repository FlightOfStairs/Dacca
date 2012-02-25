package org.flightofstairs.honours.display;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.Color;
import javax.swing.JPanel;
import java.awt.BasicStroke;

import org.flightofstairs.honours.display.components.*;

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
import edu.uci.ics.jung.visualization.RenderContext;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.Predicate;

import org.flightofstairs.honours.common.CallGraph;
import org.flightofstairs.honours.common.ExclusiveGraphUser;
import org.flightofstairs.honours.common.CallGraphListener;
import org.flightofstairs.honours.analysis.ClassScorer;
import org.flightofstairs.honours.analysis.CacheDecorator;
import org.flightofstairs.honours.analysis.RankDecorator;
import org.flightofstairs.honours.display.selection.SelectedClassModel;
import org.flightofstairs.honours.display.selection.SelectionChangeListener;

import java.util.logging.Logger;

import org.gcontracts.annotations.*

@Invariant({
		graphLayout != null &&
		scorer != null
	})
public class GraphPanel<V extends Serializable> extends JPanel {
	public static final int DEFAULT_X = 800;
	public static final int DEFAULT_Y = 800;
	
	public static final Color SELECTED_EDGE_COLOUR = Color.red;
	public static final Color NEAR_EDGE_COLOUR = Color.blue;
	public static final Color UNSELECTED_EDGE_COLOUR = Color.black;
	
	public static final float SELECTED_EDGE_WIDTH = 2f;
	public static final float NEAR_EDGE_WIDTH = 1.5f
	
	private final CallGraph<V> callGraph;
	
	private final AbstractLayout<V, ?> graphLayout;
	private final VisualizationViewer<V, ?> viewer;
	
	private ClassScorer scorer;
	
	public final SelectedClassModel selectionModel = new SelectedClassModel();
	
	public GraphPanel(CallGraph<V> callGraph, ClassScorer scorer) {
		super();
		
		this.callGraph = callGraph;
		
		graphLayout = new FRLayout2(callGraph.getGraph());
		
		graphLayout.setSize(new Dimension(DEFAULT_X, DEFAULT_Y));
		
		Relaxer relaxer = new VisRunner((IterativeContext) graphLayout);
		relaxer.stop();
		relaxer.prerelax();

		Layout staticLayout = new StaticLayout(callGraph.getGraph(), graphLayout);

        viewer = new SafeVisualizationViewer(callGraph, staticLayout);
		viewer.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
		
		setScorer(scorer);
	}
	
	@Override
	public void setSize(Dimension d) {
		super.setSize(d);
		graphLayout.setSize(d);
		redraw();
	}
	
	public void initGraphPanel() {
		RenderContext context = viewer.getRenderContext();
		
		context.setEdgeShapeTransformer(new EdgeShape.Line());
		
		context.setVertexDrawPaintTransformer({
				if(selectionModel.isSelected(it)) return SELECTED_EDGE_COLOUR;
				if(isConnectedToSelected(it)) return NEAR_EDGE_COLOUR;
				return UNSELECTED_EDGE_COLOUR;
		} as Transformer)
	
		context.setVertexStrokeTransformer({
				if(selectionModel.isSelected(it)) return new BasicStroke(SELECTED_EDGE_WIDTH);
				if(isConnectedToSelected(it)) return new BasicStroke(NEAR_EDGE_WIDTH);
				return new BasicStroke();
			} as Transformer)
	
		viewer.setVertexToolTipTransformer({ it.toString() } as Transformer);
				
		viewer.setGraphMouse(new DefaultModalGraphMouse());
		
		setLayout(new BorderLayout());
		
		add(viewer, BorderLayout.CENTER);
		
		refreshTransformers();
		
		revalidate();
		
		callGraph.addListener({ redraw() } as CallGraphListener);
		selectionModel.addListener({ redraw() } as SelectionChangeListener)
	}
	
	@Requires({ scorer != null })
	public void setScorer(ClassScorer scorer) {
		scorer = new RankDecorator(scorer);
		scorer = new CacheDecorator(callGraph, scorer);
		this.scorer = scorer;

		refreshTransformers();
	}
	
	private void refreshTransformers() {
		RenderContext context = viewer.getRenderContext();
		
		context.setVertexIncludePredicate(new HidePredicate(callGraph, scorer, viewer.getRenderContext(), 0.20))
		
		context.setVertexFillPaintTransformer({
				try {
					def greenNess = (int) (255 * scorer.rank()[it])
					return new Color(100, 255, 100, greenNess)
				} catch(all) { }
				return Color.white
			} as Transformer)
		
		def hidePredicate = new HidePredicate(callGraph, scorer, context);
		
		context.setVertexShapeTransformer(new HideTransformers(
				hidePredicate,
				new TextFitShape(context),
				{ new Rectangle(-3, -3, 6, 6) } as Transformer
				))
			
		context.setVertexLabelTransformer(new HideTransformers(
				hidePredicate,
				NameTransformers.Short,
				{ "" } as Transformer
			));

		redraw();
	}
	
	private void redraw() {
		callGraph.runExclusively({ graphLayout.initialize() } as ExclusiveGraphUser)
		
		Relaxer relaxer = new VisRunner((IterativeContext)graphLayout);
        relaxer.stop();
		
		relaxer.prerelax();
		
		LayoutTransition lt;
		
		callGraph.runExclusively({
			StaticLayout staticLayout = new StaticLayout(callGraph.getGraph(), graphLayout);
			lt = new LayoutTransition(viewer, viewer.getGraphLayout(), staticLayout);
		 } as ExclusiveGraphUser)
	 
		Animator animator = new Animator(lt);
		animator.start();
		
		relaxer.resume();
	}
	
	private boolean isConnectedToSelected(String source) {
		if(selectionModel.isSelected(source)) return true;
		
		def connections = [] as Set;
		
		callGraph.runExclusively({
				def graph = callGraph.getGraph();
				connections.addAll(graph.getPredecessors(source))
				connections.addAll(graph.getSuccessors(source))				
		} as ExclusiveGraphUser);
	
		return connections.any { selectionModel.isSelected(it) }
	}
}

