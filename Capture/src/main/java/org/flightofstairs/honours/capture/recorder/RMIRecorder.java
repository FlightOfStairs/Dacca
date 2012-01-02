package org.flightofstairs.honours.capture.recorder;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.flightofstairs.honours.capture.Producer.AspectBuilder;
import org.flightofstairs.honours.capture.Call;
import org.flightofstairs.honours.capture.CallGraph;
import org.flightofstairs.honours.capture.recorder.launchers.AspectJLauncher;
import org.flightofstairs.honours.capture.recorder.launchers.PBLauncher;

public class RMIRecorder extends UnicastRemoteObject implements Recorder, RemoteRecorder {
	
	/**
	 * Will consider recording finished when it's been TIMEOUT ms since the last batch of calls
	 */
	public static final int TIMEOUT = 1000;
	public static final int TEST_PERIOD = 100;
	
	private long lastRecieved;
	
	private final String pattern;
	private final File jarFile;
	
	private final CallGraph graph = new CallGraph();
	
	public RMIRecorder(File jarFile, String pattern) throws RemoteException {
		super();
		
		this.pattern = pattern;
		this.jarFile = jarFile;
	}
	
	public void recordSession() {
		try {
			
			int port = findFreePort();
			
			Registry registry = LocateRegistry.createRegistry(port);
			registry.rebind("Recorder", this);
			
			AspectBuilder builder = new AspectBuilder(pattern);
			File aspectClass = builder.compileAspect();
			
			AspectJLauncher launcher = new PBLauncher(jarFile, aspectClass, port);
			
			launcher.run();
			
			// Sleep while we're getting data.
			while(lastRecieved + TIMEOUT >= System.currentTimeMillis())
				;
			
		} catch (RemoteException ex) {
			Logger.getLogger(RMIRecorder.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(RMIRecorder.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void addCalls(List<Call> calls) throws RemoteException {
		lastRecieved = System.currentTimeMillis();
		
		for(Call call : calls) graph.addCall(call);
	}

	public void end() throws RemoteException {
		lastRecieved = 0; // End loop
	}

	public CallGraph getResults() { return graph; }
	
	private int findFreePort() throws IOException {
		  ServerSocket server = new ServerSocket(0);
		  int port = server.getLocalPort();
		  server.close();
		  return port;
	}

}
