package org.flightofstairs.honours.display.components

import edu.uci.ics.jung.visualization.RenderContext
import org.apache.commons.collections15.Transformer
import org.flightofstairs.honours.display.selection.SelectedClassModel
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires

import java.awt.Shape
import java.awt.geom.RoundRectangle2D

class TextFitShape implements Transformer {
	
	public static final int MARGIN = 2;
	
	private final RenderContext context;
	private final SelectedClassModel selectionModel;
	
	@Requires({ context != null })
	public TextFitShape(RenderContext context) {
		this.context = context;
	}
	
	@Requires({ vertex != null })
	@Ensures({ result != null })
	public Shape transform(Object vertex) {
		def stringTransformer = context.getVertexLabelTransformer();
		
		int width = context.getGraphicsContext().getFontMetrics().stringWidth((String) stringTransformer.transform(vertex)) + MARGIN * 2;
		int height = context.getGraphicsContext().getFontMetrics().getHeight() + MARGIN * 2;
		
		return new RoundRectangle2D.Double((double) (- width / 2), (double) (- height / 2), (double) width, (double) height, 5, 5)
	}
}

