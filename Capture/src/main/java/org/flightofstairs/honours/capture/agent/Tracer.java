package org.flightofstairs.honours.capture.agent;

import org.flightofstairs.honours.capture.recorder.RemoteRecorder;
import org.flightofstairs.honours.common.Call;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum Tracer {
	INSTANCE;
	
	/**
	 * Time between trace submits in ms.
	 */ 
	public static final int SUBMIT_DELAY = 200;
	
	private final List<Call> toSend = new LinkedList<>();
	
	private final RemoteRecorder recorder;
	
	private Tracer() throws ExceptionInInitializerError {		
		try {
			int port = Integer.parseInt(System.getProperty("org.flightofstairs.honours.capture.port"));
			
			Registry registry = LocateRegistry.getRegistry(port);

			recorder = (RemoteRecorder) registry.lookup("Recorder");
		} catch (Exception ex) {
			Logger.getLogger(Tracer.class.getName()).log(Level.SEVERE, null, ex);
			throw new ExceptionInInitializerError("Can't instantiate Tracer.");
		}
		
		initSubmit();
		initShutdown();
	}
	
	public void traceCall(Call call) {
		synchronized(toSend) {
			toSend.add(call);
		}
	}
	
	
	private void initShutdown() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				Logger.getLogger(Tracer.class.getName()).log(Level.FINE, "Ending trace.");
				try {
					recorder.addCalls(toSend);
					recorder.end();
				} catch (RemoteException ex) {
					Logger.getLogger(Tracer.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		};

		Runtime.getRuntime().addShutdownHook(thread);
	}
	
	private void initSubmit() {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Logger.getLogger(Tracer.class.getName()).log(Level.FINE, "Submitting calls.");
				synchronized(toSend) {
					try {
						recorder.addCalls(toSend);
						toSend.clear();
					} catch (RemoteException ex) {
						Logger.getLogger(Tracer.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
		};
		timer.schedule(task, SUBMIT_DELAY, SUBMIT_DELAY);
	}
}
