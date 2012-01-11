package org.flightofstairs.honours.common;


import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import edu.uci.ics.jung.graph.Graph
import edu.uci.ics.jung.graph.util.Graphs

import groovy.transform.Synchronized

import org.gcontracts.annotations.*

public class CallGraph<V extends Serializable> implements Serializable {
	
	private final graphLock = new Serializable() {}
	private final listenersLock = new Serializable() {}
	
	public static final int UPDATE_PERIOD = 1000;
	
	private long lastUpdate = 0;
	private long lastUpdateTest = 0;
	
	private final Graph<V, CallCount> graph = new DirectedSparseMultigraph<V, CallCount>();
	
	private transient Set<CallGraphListener> listeners;
	
	@Requires({ call != null })
	@Ensures({ def callRef = call;
			graph.containsVertex(call.caller) && graph.containsVertex(call.callee) &&
			graph.findEdgeSet(call.caller, call.callee).find({ it.call.equals(callRef) }) != null
		})
	@Synchronized("graphLock")
	public void addCall(final Call call) {
		graph.addVertex(call.caller);
		graph.addVertex(call.callee);
		
		def existing = graph.findEdgeSet(call.caller, call.callee).find({ it.call.equals(call) });
		
		if(existing == null) graph.addEdge(new CallCount(call), call.caller, call.callee);
		else existing.increment();
		
		lastUpdate = System.currentTimeMillis();
	}
	
	@Requires({ other != null })
	@Ensures({ graph.getVertices().containsAll(other.graph.getVertices()) })
	@Synchronized("graphLock")
	public void merge(CallGraph<V> other) {
		for(Call call : other.calls(false)) {
			addCall(call);
		}
	}
	
	@Ensures({ result != null && result.size() == graph.getVertexCount()})
	@Synchronized("graphLock")
	public List<V> classes() {
		def results = [];
		results.addAll graph.getVertices();
		return results
	}
	
	@Ensures({ result != null && result.size() >= graph.getEdgeCount() })
	@Synchronized("graphLock")
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
	@Synchronized("graphLock")
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
	
	@Requires({ listener != null })
	@Ensures({ listeners != null && listeners.contains(listener) })
	@Synchronized("listenersLock")
	public void addListener(CallGraphListener listener) {
		if(listeners == null) initListeners();
		listeners << listener;
	}
	
	@Requires({ listeners == null })
	@Ensures({ listerners != null })
	private void initListeners() {
		listeners = [] as Set;

		// Assume that timer won't be running
		Timer timer = new Timer();
		timer.scheduleAtFixedRate({
			if(lastUpdate >= lastUpdateTest) {
				for(CallGraphListener l in listeners) l.callGraphChange(this);
			}
			lastUpdateTest = System.currentTimeMillis();
		} as TimerTask, 0, UPDATE_PERIOD);
	}
	
	@Requires({ listener != null })
	@Ensures({ listeners == null || ! listeners.contains(listener) })
	@Synchronized("listenersLock")
	public boolean removeListener(CallGraphListener listener) {
		if(listeners == null || ! listener.contains(listener)) return false;
		
		listeners.remove(listener);
		
		return true;
	}
}