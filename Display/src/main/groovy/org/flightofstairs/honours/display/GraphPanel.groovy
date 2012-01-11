package org.flightofstairs.honours.display;

import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.JPanel;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;

import org.flightofstairs.honours.common.CallGraph;
import org.flightofstairs.honours.common.CallGraphListener;

import org.gcontracts.annotations.*

@Invariant({ layout != null })
public class GraphPanel<V extends Serializable> extends JPanel implements CallGraphListener {
	public static final int DEFAULT_X = 600;
	public static final int DEFAULT_Y = 600;	
	
	private final CallGraph<V> callGraph;
	
	private AbstractLayout<V, ?> layout;
	
	private final VisualizationViewer<V, ?> viewer;
	
	public GraphPanel(CallGraph<V> callGraph) {
		this.callGraph = callGraph;
		
		layout = new FRLayout2(callGraph.getGraph());
		
		viewer = new VisualizationViewer<Number,Number>(layout, new Dimension(DEFAULT_X, DEFAULT_Y));
		vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<V>());
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(viewer);
		
		validate();
		pack();
	}
	
	@Requires({callGraph == this.callGraph})
	public void callGraphChange(CallGraph callGraph) {
		Relaxer relaxer = viewer.getModel().getRelaxer();
        relaxer.pause();
		
		layout.initialize();
		
		relaxer.resume();
	}
}

