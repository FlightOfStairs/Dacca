package org.flightofstairs.honours.display;

import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.JPanel;

import 

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

import org.flightofstairs.honours.common.CallGraph;
import org.flightofstairs.honours.common.CallGraphListener;

import org.gcontracts.annotations.*

@Invariant({ layout != null })
public class GraphPanel<V extends Serializable> extends JPanel implements CallGraphListener {
	public static final int DEFAULT_X = 800;
	public static final int DEFAULT_Y = 800;
	
	public static final Transformer
	
	private final CallGraph<V> callGraph;
	
	private AbstractLayout<V, ?> layout;
	
	private final VisualizationViewer<V, ?> viewer;
	
	public GraphPanel(CallGraph<V> callGraph) {
		this.callGraph = callGraph;
		
		layout = new FRLayout2(callGraph.getGraph());
		
		layout.setSize(new Dimension(DEFAULT_X, DEFAULT_Y));
		
		Relaxer relaxer = new VisRunner((IterativeContext)layout);
		relaxer.stop();
		relaxer.prerelax();

		Layout staticLayout = new StaticLayout(callGraph.getGraph(), layout);

        viewer = new VisualizationViewer(staticLayout, new Dimension(DEFAULT_X, DEFAULT_Y));
		
		viewer.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
		viewer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<V>());
		
		setLayout(new BorderLayout());
		add(viewer);
		
		validate();
		
		callGraph.addListener(this);
	}
	
	@Requires({callGraph == this.callGraph})
	public void callGraphChange(CallGraph callGraph) {
		layout.initialize();
		
		Relaxer relaxer = new VisRunner((IterativeContext)layout);
        relaxer.stop();
		
		relaxer.prerelax();
        StaticLayout staticLayout = new StaticLayout(callGraph.getGraph(), layout);
		
		LayoutTransition lt = new LayoutTransition(viewer, viewer.getGraphLayout(), staticLayout);
		
		Animator animator = new Animator(lt);
		animator.start();
		
		viewer.repaint();

		relaxer.resume();
	}
}

