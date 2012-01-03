package org.flightofstairs.honours.common;


import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import edu.uci.ics.jung.graph.Graph
import edu.uci.ics.jung.graph.util.Graphs

import groovy.transform.Synchronized

import org.gcontracts.annotations.*

public class CallGraph<V extends Serializable> implements Serializable {
	
	private final Graph<V, CallCount> graph = new DirectedSparseMultigraph<V, CallCount>();
	
	@Requires({ call != null })
	@Ensures({ def callRef = call;
			graph.containsVertex(call.caller) && graph.containsVertex(call.callee) &&
			graph.findEdgeSet(call.caller, call.callee).find({ it.call.equals(callRef) }) != null
		})
	@Synchronized
	public void addCall(final Call call) {
		graph.addVertex(call.caller);
		graph.addVertex(call.callee);
		
		def existing = graph.findEdgeSet(call.caller, call.callee).find({ it.call.equals(call) });
		
		if(existing == null) graph.addEdge(new CallCount(call), call.caller, call.callee);
		else existing.increment();
	}
	
	@Requires({ other != null })
	@Ensures({ graph.getVertices().containsAll(other.graph.getVertices()) })
	@Synchronized
	public void merge(CallGraph<V> other) {
		for(Call call : other.calls(false)) {
			addCall(call);
		}
	}
	
	@Ensures({ result != null })
	@Synchronized
	public List<V> classes() {
		def results = [];
		results.addAll graph.getVertices();
		return results
	}
	
	@Ensures({ result != null })
	@Synchronized
	public List<Call> calls(boolean onlyUnique = true) {
		def results = [];
		for(CallCount count in graph.getEdges()) {
			for(int i in 1..(onlyUnique ? 1 : count.count )) results << count.call;
		}
		return results
	}
	
	@Ensures({ result != null })
	public Graph<V, CallCount> getGraph() {
		return Graphs.unmodifiableDirectedGraph(graph);
	}
	
	@Requires({ ! file.isDirectory() })
	@Ensures({ file.exists() })
	@Synchronized
	public void save(File file) {
		file.withObjectOutputStream() { outStream ->
			outStream << this
		}
	}
	
	@Requires({ file.exists() && ! file.isDirectory() })
	@Ensures({ result != null })
	public static CallGraph open(File file) {
		file.withObjectInputStream() { instream -> 
			return instream.readObject();
		}
	}
}