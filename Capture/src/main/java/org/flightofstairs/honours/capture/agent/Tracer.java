package org.flightofstairs.honours.capture.agent;

import org.flightofstairs.honours.capture.recorder.RemoteRecorder;
import org.flightofstairs.honours.common.Call;
import org.slf4j.LoggerFactory;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum Tracer {
	INSTANCE;

	public static final long RECONNECT_DELAY = 500;
	public static final int MAX_CONNECT_ATTEMPTS = 10;

	public static final long SUBMIT_DELAY = 50;
	
	public static final int MAX_WAITING = 50000;
	
	public final AtomicInteger waiting = new AtomicInteger(0);
	
	private final Queue<Call> toSend = new ConcurrentLinkedQueue<Call>();
	
	private final RemoteRecorder recorder;
	
	private Tracer() throws ExceptionInInitializerError {

		int connectionAttempts = 0;

		RemoteRecorder recorder = null;

		while(MAX_CONNECT_ATTEMPTS > connectionAttempts++) {
			try {
				int port = Integer.parseInt(System.getProperty("org.flightofstairs.honours.capture.port"));

				Registry registry = LocateRegistry.getRegistry(port);
				recorder = (RemoteRecorder) registry.lookup("Recorder");

				break;
			} catch(Exception e) {
				LoggerFactory.getLogger(getClass()).warn("Connecting to Dacca failed... retrying.", e);

				try { Thread.sleep(RECONNECT_DELAY); }
				catch (InterruptedException ex) { LoggerFactory.getLogger(getClass()).warn("Tracer retry wait interrupted.", ex); }
			}
		}

		if(recorder == null) {
			LoggerFactory.getLogger(getClass()).error("Error initializing tracker.");
			throw new ExceptionInInitializerError("Can't instantiate Tracer.");
		}

		this.recorder = recorder;

		initSubmit();
		initShutdown();
	}
	
	public void traceCall(Call call) {
		toSend.add(call);
		waiting.incrementAndGet();
	}
	
	
	private void initShutdown() {
		Thread thread = new Thread() {
			@Override public void run() {
				submit();
			}
		};

		Runtime.getRuntime().addShutdownHook(thread);
	}
	
	private void submit() {
		List<Call> toSendCopy;

		synchronized(toSend) {
			toSendCopy = new LinkedList<Call>(toSend);
			toSend.clear();
			
			waiting.set(0);
		}
		
		final Map<Call, Integer> callCounts = new HashMap<Call, Integer>();

		for(Call c : toSendCopy) {
			final int count = callCounts.containsKey(c) ? callCounts.get(c) + 1 : 1;
			callCounts.put(c, count);
		}

		try {
			recorder.addCallCounts(callCounts);
			toSend.clear();
		} catch (ConnectException ex) {
			Logger.getLogger(Tracer.class.getName()).log(Level.SEVERE, null, ex);
		} catch (RemoteException ex) {
			Logger.getLogger(Tracer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private void initSubmit() {
		final Timer timer = new Timer();
		final TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				submit();
			}
		};
		timer.schedule(task, SUBMIT_DELAY, SUBMIT_DELAY);
	}
}
