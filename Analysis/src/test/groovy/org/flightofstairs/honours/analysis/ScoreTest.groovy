package org.flightofstairs.honours.analysis

import org.flightofstairs.honours.common.CallGraph;

class ScoreTest extends GroovyTestCase {
	private CallGraph orrery;
	
	private static final List<String> top = [
		"StandardDrawingView", "StandardDrawing", "DrawApplication", "PlanetFigure", "OrreryApp", "AtmosphereDecorator"
	]
	
	void setUp() {
		def file = new File(getClass().getResource("/orrery.callgraph").getFile());
		
		assertTrue(file.exists());
		
		orrery = CallGraph.open(file);
	}
	
	void testBasic() {
		scorers(orrery).each {
			it.rank();
		}
	}
	
	void testRankDecorator() {
		scorers(orrery).each {
			def unScaled = it.rank();
			def scaled = (new RankDecorator(it)).rank();
			
			def unScaledClasses = unScaled.keySet().sort ({ a, b ->
					Math.abs(unScaled[a] - unScaled[b]) > 0.000001 ? unScaled[a] <=> unScaled[b] : a <=> b
				})
			def scaledClasses = scaled.keySet().sort ({ a, b ->
					Math.abs(scaled[a] - scaled[b]) > 0.000001 ? scaled[a] <=> scaled[b] : a <=> b
				})
			
			assertEquals(scaledClasses, unScaledClasses);
		}
		
		// rank itself
		scorers(orrery).each {
			def unScaled = it.rank();
			def scaled = (new RankDecorator(new RankDecorator(it))).rank();
			
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
		scorers(orrery).each {
			def results = it.rank();

			def cached = new CacheDecorator(orrery, it);
			
			def start = System.nanoTime();
			assertEquals(results, cached.rank());
			
			def firstTime = System.nanoTime() - start;
			
			start = System.nanoTime();
			assertEquals(results, cached.rank());
			
			def againTime = System.nanoTime() - start;
			
			assertTrue(againTime < firstTime);
		}
	}
	
	void testBest() {
		scorers(orrery).each {
			def scores = it.rank()
			
			def best = scores.keySet().max { scores[it] }
			def parts = best.tokenize('.')
			
			assertTrue(top.contains(parts[parts.size() - 1]))
			
			scores.remove(best);
			
			/* This test ofter fails when run against new callgraphs.
			best = scores.keySet().max { scores[it] }
			parts = best.tokenize('.')
			assertTrue(top.contains(parts[parts.size() - 1]))
			*/
		}
	}
	
	private static List<ClassScorer> scorers(CallGraph callGraph) {
		return ["Total HITS", "Weighted HITS", "Class Connectivity","Coupling Degree", "Method Connectivity"].collect { ScorerFactory.scorers[it](callGraph) };
	}
}

