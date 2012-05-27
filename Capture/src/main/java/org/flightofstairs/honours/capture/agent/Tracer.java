package org.flightofstairs.honours.capture.agent;

import org.flightofstairs.honours.capture.recorder.RemoteRecorder;
import org.flightofstairs.honours.common.Call;
import org.slf4j.LoggerFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SuppressWarnings("UnusedDeclaration")
public enum Tracer {
	INSTANCE;

	public static final long SUBMIT_DELAY = 50;
	public static final int MAX_WAITING = 50000;
	
	public final AtomicInteger waiting = new AtomicInteger(0);

	public final Probes probes = new Probes();

	private final RemoteRecorder recorder;

	private final ConcurrentMap<InternalCall, AtomicInteger> simpleTraceMap = new ConcurrentHashMap<InternalCall, AtomicInteger>();
	private final ConcurrentMap<CallPair, AtomicInteger> complexTraceMap = new ConcurrentHashMap<CallPair, AtomicInteger>();

	// This lock is used in a very deviant way.
	// The probe() methods are readers, even though they change maps. Submit() is a writer.
	// This allows threadsafe probe() methods to exclude submit(), while still co-operating with each other.
	private final ReadWriteLock sendLock = new ReentrantReadWriteLock();

	private Tracer() throws ExceptionInInitializerError {
		try {
			int port = Integer.parseInt(System.getProperty("org.flightofstairs.honours.capture.port"));

			Registry registry = LocateRegistry.getRegistry(port);
			RemoteRecorder recorder = (RemoteRecorder) registry.lookup("Recorder");

			this.recorder = recorder;

			initSubmit();
			initShutdown();

		} catch(Exception e) {
			LoggerFactory.getLogger(getClass()).warn("Connecting to Dacca failed.", e);
			throw new ExceptionInInitializerError(e);
		}
	}

	public void probe(final int probeID) {
		sendLock.readLock().lock();
		try {
			final InternalCall call = probes.getCallFromID(probeID);
			simpleTraceMap.putIfAbsent(call, new AtomicInteger(0));

			simpleTraceMap.get(call).incrementAndGet();
		} finally {
			sendLock.readLock().unlock();
		}
	}

	public void probe(final Class callee, final int probeID) {
		sendLock.readLock().lock();
		try {
			final CallPair pair = new CallPair(probes.getCallFromID(probeID), callee);
			complexTraceMap.putIfAbsent(pair, new AtomicInteger(0));

			complexTraceMap.get(pair).incrementAndGet();
		} finally {
			sendLock.readLock().unlock();
		}
	}
	

	
	private void submit() {
		List<Call> toSendCopy;

		final Map<InternalCall, Integer> simpleTraceMap = new HashMap<InternalCall, Integer>(this.simpleTraceMap.size());
		final Map<CallPair, Integer> complexTraceMap = new HashMap<CallPair, Integer>(this.complexTraceMap.size());

		sendLock.writeLock().lock();
		try {
			for(final Map.Entry<InternalCall, AtomicInteger> entry : this.simpleTraceMap.entrySet())
				simpleTraceMap.put(entry.getKey(), entry.getValue().getAndSet(0));

			for(final Map.Entry<CallPair, AtomicInteger> entry : this.complexTraceMap.entrySet())
				complexTraceMap.put(entry.getKey(), entry.getValue().getAndSet(0));
		} finally {
			sendLock.writeLock().unlock();
		}

		final Map<Call, Integer> callCounts = new HashMap<Call, Integer>(complexTraceMap.size());

		for(final Map.Entry<InternalCall, Integer> entry : simpleTraceMap.entrySet()) {
			if(entry.getValue() == 0) continue;

			final Call call = entry.getKey().getCall();

			if(call == null) continue;

			final int currentCount = callCounts.containsKey(call) ? callCounts.get(call) : 0;

			callCounts.put(call, currentCount + entry.getValue());
		}

		int simpleCount = callCounts.size();

		for(final Map.Entry<CallPair, Integer> entry : complexTraceMap.entrySet()) {
			if(entry.getValue() == 0) continue;

			try {
				final Call call = entry.getKey().call.getCallOnClass(entry.getKey().instanceClass);

				if(call == null) continue;

				final int currentCount = callCounts.containsKey(call) ? callCounts.get(call) : 0;

				callCounts.put(call, currentCount + entry.getValue());
			} catch (Exception e) {
				LoggerFactory.getLogger(getClass()).warn("Error finding called class.", e);
			}
		}

		if(callCounts.isEmpty()) return;

		try {
			recorder.addCallCounts(callCounts);
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error submitting calls.", e);
		}
	}
	
	private void initSubmit() {
		final Timer timer = new Timer();
		final TimerTask task = new TimerTask() {
			@Override public void run() {
				submit();
			}
		};
		timer.schedule(task, SUBMIT_DELAY, SUBMIT_DELAY);
	}

	private void initShutdown() {
		Thread thread = new Thread() {
			@Override public void run() {
				submit();
			}
		};
		Runtime.getRuntime().addShutdownHook(thread);
	}

	private static class CallPair {
		public final InternalCall call;
		public final Class instanceClass;


		private CallPair(final InternalCall call, final Class instanceClass) {
			this.call = call;
			this.instanceClass = instanceClass;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) return true;
			if (obj == null || getClass() != obj.getClass()) return false;

			final CallPair callPair = (CallPair) obj;

			if (!call.equals(callPair.call)) return false;
			if (!instanceClass.equals(callPair.instanceClass)) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = call.hashCode();
			result = 31 * result + instanceClass.hashCode();
			return result;
		}
	}
}
