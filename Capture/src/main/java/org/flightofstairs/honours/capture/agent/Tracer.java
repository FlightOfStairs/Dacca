package org.flightofstairs.honours.capture.agent;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.flightofstairs.honours.capture.Call;
import org.flightofstairs.honours.capture.recorder.RemoteRecorder;

public enum Tracer implements RemoteTracer {
	INSTANCE;
	
	/**
	 * Time between trace submits in ms.
	 */ 
	public static final int SUBMIT_DELAY = 200;
	
	private final List<Call> toSend = new LinkedList<Call>();
	
	private final RemoteRecorder recorder;
	
	private Tracer() throws ExceptionInInitializerError {		
		try {
			int port = Integer.parseInt(System.getProperty("org.flightofstairs.honours.capture.port"));
			
			Registry registry = LocateRegistry.getRegistry(port);

			recorder = (RemoteRecorder) registry.lookup("Recorder");

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

			timer.schedule(task, SUBMIT_DELAY, SUBMIT_DELAY);
		}
		catch (Exception ex) {
			Logger.getLogger(Tracer.class.getName()).log(Level.SEVERE, null, ex);
			throw new ExceptionInInitializerError("Can't instantiate Tracer.");
		}
	}
	
	public void traceCall(Call call) {
		synchronized(toSend) {
			toSend.add(call);
		}
	}
	
	public boolean existanceTest() throws RemoteException { return true; }
	
}
