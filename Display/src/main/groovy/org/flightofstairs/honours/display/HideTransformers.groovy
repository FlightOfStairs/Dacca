package org.flightofstairs.honours.display

import java.awt.Shape;

import org.apache.commons.collections15.Transformer
import org.apache.commons.collections15.Predicate;

import org.gcontracts.annotations.*

class HideTransformers<V, R> implements Transformer {
	private final Predicate<V> predicate;
	private final Transformer<V, R> visible;
	private final Transformer<V, R> hidden;	
	
	@Requires({ predicate != null && visible != null && hidden != null })
	public HideTransformers(Predicate predicate, Transformer visible, Transformer hidden) {
		this.predicate = predicate;
		this.visible = visible;
		this.hidden = hidden;
	}
	
	@Requires({ vertex != null })
	@Ensures({ result != null })
	public R transform(V vertex) {
		return predicate.evaluate(vertex) ? visible.transform(vertex) : hidden.transform(vertex);
	}
}

