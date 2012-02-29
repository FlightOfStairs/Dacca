package org.flightofstairs.honours.capture.agent;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.flightofstairs.honours.capture.recorder.RemoteRecorder;
import org.flightofstairs.honours.common.Call;

public enum Tracer {
	INSTANCE;
	
	/**
	 * Time between trace submits in ms.
	 */ 
	public static final int SUBMIT_DELAY = 200;
	
	private final Queue<Call> toSend = new ConcurrentLinkedQueue<Call>();
	
	private final RemoteRecorder recorder;
	
	private Tracer() throws ExceptionInInitializerError {		
		try {
			int port = Integer.parseInt(System.getProperty("org.flightofstairs.honours.capture.port"));
			
			Registry registry = LocateRegistry.getRegistry(port);

			recorder = (RemoteRecorder) registry.lookup("Recorder");
		} catch (NumberFormatException  ex) {
			throw new ExceptionInInitializerError("Can't instantiate Tracer.");
		} catch (RemoteException ex) {
			throw new ExceptionInInitializerError("Can't instantiate Tracer.");
		} catch (NotBoundException ex) {
			throw new ExceptionInInitializerError("Can't instantiate Tracer.");
		}
		
		initSubmit();
		initShutdown();
	}
	
	public void traceCall(Call call) {
			toSend.add(call);
	}
	
	
	private void initShutdown() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					List<Call> toSendCopy = new LinkedList<Call>();
					
					Call polled;
					
					while((polled = toSend.poll()) != null) {
						toSendCopy.add(polled);
					}
					
					recorder.addCalls(toSendCopy);
					recorder.end();
				} catch (RemoteException ex) {
				}
			}
		};

		Runtime.getRuntime().addShutdownHook(thread);
	}
	
	private void initSubmit() {
		final Timer timer = new Timer();
		final TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				List<Call> toSendCopy;
				
				synchronized(toSend) {
					toSendCopy = new LinkedList<Call>(toSend);
				}
				
				try {
					recorder.addCalls(toSendCopy);
					toSend.clear();
				} catch (ConnectException ex) {
					Logger.getLogger(Tracer.class.getName()).log(Level.SEVERE, null, ex);
				} catch (RemoteException ex) {
					Logger.getLogger(Tracer.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		};
		timer.schedule(task, SUBMIT_DELAY, SUBMIT_DELAY);
	}
}
