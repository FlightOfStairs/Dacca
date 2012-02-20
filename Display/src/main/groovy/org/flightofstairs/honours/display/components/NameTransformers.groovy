package org.flightofstairs.honours.display.components

import org.apache.commons.collections15.Transformer;

class NameTransformers {
	public static Transformer<String, String> Full = {
		it.toString()
	} as Transformer;
	
	public static final Transformer<String, String> Short = {
		def parts = it.toString().tokenize('.');
		return parts[parts.size() - 1];
	} as Transformer;
}

