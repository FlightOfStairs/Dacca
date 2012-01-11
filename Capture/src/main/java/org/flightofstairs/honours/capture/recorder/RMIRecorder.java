package org.flightofstairs.honours.capture.recorder;

import org.flightofstairs.honours.capture.Producer.AspectBuilder;
import org.flightofstairs.honours.capture.recorder.launchers.AspectJLauncher;
import org.flightofstairs.honours.capture.recorder.launchers.PBLauncher;
import org.flightofstairs.honours.common.Call;
import org.flightofstairs.honours.common.CallGraph;

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

public class RMIRecorder extends UnicastRemoteObject implements Recorder, RemoteRecorder {
	
	private final String pattern;
	private final File jarFile;
	
	private boolean ended = true;
	
	private final CallGraph<String> graph = new CallGraph<>();
	
	public RMIRecorder(File jarFile, String pattern) throws RemoteException {
		super();
		
		this.pattern = pattern;
		this.jarFile = jarFile;
	}
	
	public void recordSession() {
		ended = false;
		try {
			
			int port = findFreePort();
			
			Registry registry = LocateRegistry.createRegistry(port);
			registry.rebind("Recorder", this);
			
			AspectBuilder builder = new AspectBuilder(pattern);
			File aspectClass = builder.compileAspect();
			
			AspectJLauncher launcher = new PBLauncher(jarFile, aspectClass, port);
			
			launcher.run();
			
		} catch (IOException ex) {
			Logger.getLogger(RMIRecorder.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void addCalls(List<Call> calls) throws RemoteException {
		if(ended) throw new UnsupportedOperationException("Can't add calls after recording finished.");
		
		for(Call call : calls) graph.addCall(call);
	}

	public void end() throws RemoteException {
		ended = true;
	}

	public CallGraph getResults() { return graph; }
	
	private int findFreePort() throws IOException {
		int port;
		try (ServerSocket server = new ServerSocket(0)) {
			port = server.getLocalPort();
		}
		return port;
	}

}
