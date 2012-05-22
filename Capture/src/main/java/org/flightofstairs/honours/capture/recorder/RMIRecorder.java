package org.flightofstairs.honours.capture.recorder;

import org.flightofstairs.honours.capture.sources.Source;
import org.flightofstairs.honours.common.Call;
import org.flightofstairs.honours.common.CallGraph;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class RMIRecorder extends UnicastRemoteObject implements Recorder, RemoteRecorder {

	private final Source source;

	private final CallGraph graph = new CallGraph();
		
	public RMIRecorder(Source source) throws RemoteException {
		super();

		this.source = source;
	}
	
	@Override
	public void run() {
		try {

			final int port = RMIRecorder.findFreePort();

			final RMISocketFactory clientSocketFactory = RMISocketFactory.getDefaultSocketFactory();

			Registry registry = LocateRegistry.createRegistry(port, clientSocketFactory, new RMIServerSocketFactory() {
				@Override public ServerSocket createServerSocket(int arg0) throws IOException {
					return new ServerSocket(arg0, 0, InetAddress.getLocalHost());
				}
			});
			
			registry.rebind("Recorder", this);
			
			source.startSource(port);
			
		} catch (IOException ex) {
			LoggerFactory.getLogger(RMIRecorder.class).error("Problem preparing or launching traced application", ex);
		}
	}

	@Override
	public void addCallCounts(final Map<Call, Integer> callCounts) throws RemoteException {
		Thread t = new Thread(new Runnable() {
			@Override public void run() {
				graph.addCallCounts(callCounts);
			}
		});
		
		t.start();
	}

	@Override
	public CallGraph getResults() { return graph; }
	
	private static int findFreePort() throws IOException {
		int port;
		ServerSocket server = new ServerSocket(0);
		port = server.getLocalPort();
		server.close();
		return port;
	}
}
