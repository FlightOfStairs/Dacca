package org.flightofstairs.honours.display

import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;

import edu.uci.ics.jung.visualization.RenderContext;

import org.apache.commons.collections15.Transformer

import org.gcontracts.annotations.*

class TextFitShape<V> implements Transformer {
	
	public static final int MARGIN = 2;
	
	private final RenderContext context;
	
	@Requires({ context != null })
	public TextFitShape(RenderContext context) {
		this.context = context;
	}
	
	public Shape transform(V vertex) {
		def stringTransformer = context.getVertexLabelTransformer();
		
		int width = context.getGraphicsContext().getFontMetrics().stringWidth(stringTransformer.transform(vertex)) + MARGIN * 2;
		int height = context.getGraphicsContext().getFontMetrics().getHeight() + MARGIN * 2;
		
		return new RoundRectangle2D.Double((double) (- width / 2), (double) (- height / 2), (double) width, (double) height, 5, 5)
	}
}

