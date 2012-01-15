package org.flightofstairs.honours.analysis

import org.flightofstairs.honours.common.CallGraph;

class RankDecoratorTest extends GroovyTestCase {
	private CallGraph orrery;
	
	private static final List<String> top = [
		"StandardDrawingView", "DrawApplication", "PlanetFigure", "OrreryApp"
	]
	
	void setUp() {
		def file = new File(getClass().getResource("/orrery.callgraph").getFile());
		
		assertTrue(file.exists());
		
		orrery = CallGraph.open(file);
	}
	
	void testBasic() {
		Scorers.scorers.each {
			it.rank(orrery);
		}
	}
	
	void testRankDecorator() {
		Scorers.scorers.each {
			def unScaled = it.rank(orrery);
			def scaled = (new RankDecorator(it)).rank(orrery);
			
			def unScaledClasses = unScaled.keySet().sort ({ a, b ->
					Math.abs(unScaled[a] - unScaled[b]) > 0.000001 ? unScaled[a] <=> unScaled[b] : a <=> b
				})
			def scaledClasses = scaled.keySet().sort ({ a, b ->
					Math.abs(scaled[a] - scaled[b]) > 0.000001 ? scaled[a] <=> scaled[b] : a <=> b
				})
			
			assertEquals(scaledClasses, unScaledClasses);
		}
	}
	
	void testCacheDecorator() {
		Scorers.scorers.each {
			def results = it.rank(orrery);
			
			def cached = new CacheDecorator(orrery, it);
			
			assertEquals(results, cached.rank(orrery));
		}
	}
	
	void testBest() {
		Scorers.scorers.each {
			def scores = it.rank(orrery)
			
			def best = scores.keySet().max { scores[it] }
			def parts = best.tokenize('.')
			
			assertTrue(top.contains(parts[parts.size() - 1]))
			
			scores.remove(best);
			
			best = scores.keySet().max { scores[it] }
			parts = best.tokenize('.')
			assertTrue(top.contains(parts[parts.size() - 1]))
		}
	}
}

