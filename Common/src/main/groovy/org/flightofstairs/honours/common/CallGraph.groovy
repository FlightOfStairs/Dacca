package org.flightofstairs.honours.common;


import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import edu.uci.ics.jung.graph.Graph
import edu.uci.ics.jung.graph.util.Graphs
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires
import org.slf4j.LoggerFactory

import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import groovy.transform.WithReadLock
import groovy.transform.WithWriteLock

public class CallGraph implements Serializable {
	
	private static final long serialVersionUID = 2L;
	
	private final ReadWriteLock graphLock = new ReentrantReadWriteLock();

	public static final int UPDATE_PERIOD = 1000;
	
	private long lastUpdate = 0;
	private long lastUpdateTest = 0;
	
	private final Graph<String, ClassRelation> graph = new DirectedSparseMultigraph<String, ClassRelation>();

	// Using a cached result for our unmodifiableGraph, we can safely share it while still allowing clients to use the
	// same instance.
	private transient Graph<String, ClassRelation> unmodifiableGraph;

	private transient Set<CallGraphListener> listeners;
		
	@Requires({ call != null && call.callee != null && call.caller != null && call.method != null})
	@WithWriteLock("graphLock")
	public void addCall(final Call call) {
		graph.addVertex(call.caller);
		graph.addVertex(call.callee);

		def existing = graph.findEdge(call.caller, call.callee);

		if(existing == null) {
			existing = new ClassRelation();
			graph.addEdge(existing, call.caller, call.callee);
		}
		existing.addCall(call);

		lastUpdate = System.currentTimeMillis();
	}
	
	@Requires({ callCounts != null })
	@WithWriteLock("graphLock")
	public void addCallCounts(final Map<Call, Integer> callCounts) {
		callCounts.each { call, count ->
			addCall(call);

			def edge = graph.findEdge(call.caller, call.callee);

			edge.addCall(call, count - 1);
		}
	}
	
	@Requires({ other != null })
	@Ensures({ graph.getVertices().containsAll(other.graph.getVertices()) })
	@WithWriteLock("graphLock")
	public void merge(CallGraph other) {
		for(Call call : other.calls(false))	addCall(call);
	}
	
	@Ensures({ result != null && result.size() == graph.getVertexCount()})
	@WithReadLock("graphLock")
	public List<String> classes() {
		def results = []
		results.addAll graph.getVertices()
		return results
	}
	
	@Ensures({ result != null && result.size() >= graph.getEdgeCount() })
	@WithReadLock("graphLock")
	public List<Call> calls(boolean onlyUnique = true) {
		def results = [];

		for(ClassRelation relation in graph.getEdges()) {
			results.addAll relation.getCalls(onlyUnique);
		}
		return results
	}

	/**
	 * Execute ExclusiveGraphUser's run method while blocking writes to graph.
	 * User MUST NOT make changes to graph.
	 */
	@Requires({ user != null })
	@WithReadLock("graphLock")
	public void runExclusively(ExclusiveGraphUser user) {
		user.run(this);
	}

	@Ensures({ result != null })
	public Graph<String, ClassRelation> getGraph() {
		if(unmodifiableGraph == null)
			unmodifiableGraph = Graphs.unmodifiableDirectedGraph(graph);
			
		return unmodifiableGraph;
	}
	
	@Requires({ ! file.isDirectory() })
	@Ensures({ file.exists() })
	@WithReadLock("graphLock")
	public void save(File file) {
        LoggerFactory.getLogger(CallGraph.class).debug("Saving callgraph: {}", file);

		file.withObjectOutputStream() { outStream ->
			outStream << this
		}
	}
	
	@Requires({ file.exists() && ! file.isDirectory() })
	@Ensures({ result != null })
	public static CallGraph open(File file) {
        LoggerFactory.getLogger(CallGraph.class).debug("Opening callgraph: {}", file);

		file.withObjectInputStream() { instream -> 
			return (CallGraph) instream.readObject();
		}
	}
	
	@Requires({ listener != null })
	@Ensures({ listeners != null && listeners.contains(listener) })
	public synchronized void addListener(CallGraphListener listener) {
		if(listeners == null) initListeners();
		listeners << listener;
	}
	
	@Requires({ listeners == null })
	@Ensures({ listeners != null })
	private void initListeners() {
		listeners = new CopyOnWriteArraySet<CallGraphListener>()

		// Assume that timer won't be running
		Timer timer = new Timer();
		timer.scheduleAtFixedRate({
				if(graphLock.readLock().tryLock()) {
					try {
						if(lastUpdate >= lastUpdateTest) {
							for(CallGraphListener l in listeners) l.callGraphChange(this);
						}
						lastUpdateTest = System.currentTimeMillis();
						
					}
					catch(all) {
						LoggerFactory.getLogger(CallGraph.class).warn("A callgraph listener threw an exception.", all);
					}
					finally { graphLock.readLock().unlock(); }
				}
		} as TimerTask, 0, UPDATE_PERIOD);
	}
	
	@Requires({ listener != null })
	@Ensures({ listeners == null || ! listeners.contains(listener) })
	public boolean removeListener(CallGraphListener listener) {
		if(listeners == null || ! listener.contains(listener)) return false;
		
		listeners.remove(listener);
		
		return true;
	}

	public int numberOfClasses() { return graph.getVertexCount(); }

	public int numberOfRelations() { return graph.getEdgeCount(); }

	@WithReadLock("graphLock")
	public int numberOfMethods() {
		return graph.getEdges().inject(0) { total, item -> total + item.callVariety() }
	}

	@WithReadLock("graphLock")
	public int numberOfCalls() {
		return graph.getEdges().inject(0) { total, item -> total + item.countAll() }
	}
}