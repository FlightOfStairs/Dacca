package org.flightofstairs.honours.capture.recorder;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import org.flightofstairs.honours.capture.Producer.AspectBuilder;
import org.flightofstairs.honours.capture.launchers.JPBLauncher;
import org.flightofstairs.honours.capture.launchers.LaunchConfiguration;
import org.flightofstairs.honours.capture.launchers.Launcher;
import org.flightofstairs.honours.common.Call;
import org.flightofstairs.honours.common.CallGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RMIRecorder extends UnicastRemoteObject implements Recorder, RemoteRecorder {
	
	public final LaunchConfiguration launchConfig;
	
	private final Launcher launcher = new JPBLauncher();
	
	private boolean ended = true;
	
	public int port;
	
	private final CallGraph<String> graph = new CallGraph<String>();
	
	public RMIRecorder(LaunchConfiguration launchConfig) throws RemoteException {
		super();
		
		this.launchConfig = launchConfig;
	}
	
	@Override
	public void recordSession() {
		ended = false;
		try {
			
			port = findFreePort();
						
			Registry registry = LocateRegistry.createRegistry(port, RMISocketFactory.getDefaultSocketFactory(), new RMIServerSocketFactory() {
				@Override public ServerSocket createServerSocket(int arg0) throws IOException {
					return new ServerSocket(arg0, 0, InetAddress.getLocalHost());
				}
			});
			
			registry.rebind("Recorder", this);
			
			AspectBuilder builder = new AspectBuilder(launchConfig.packages());
			File aspectClass = builder.compileAspect();
						
			LaunchConfiguration rmiConfig = new RMILaunchConfig(launchConfig, port, aspectClass.getAbsolutePath());
			
			launcher.launch(rmiConfig);
			
		} catch (IOException ex) {
			LoggerFactory.getLogger(RMIRecorder.class).error("Problem preparing or launching traced application", ex);
		}
	}

	@Override
	public void addCalls(List<Call> calls) throws RemoteException {
		if(ended) throw new UnsupportedOperationException("Can't add calls after recording finished.");
		
		for(Call call : calls) graph.addCall(call);
	}

	@Override
	public void end() throws RemoteException {
		ended = true;
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

	private static class RMILaunchConfig implements LaunchConfiguration {
		public final int port;
		
		public final String aspectJar;
		
		public final LaunchConfiguration delegate;
		
		public RMILaunchConfig(LaunchConfiguration delegate, int port, String aspectJar) {
			this.delegate = delegate;
			this.port = port;
			this.aspectJar = aspectJar;
		}
		
		@Override
		public List<String> getJVMArguments() {
			List<String> args = new LinkedList<String>();
			
			String weaver = org.aspectj.weaver.loadtime.Agent.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			
			LoggerFactory.getLogger(RMIRecorder.class).info("Using [{}] for weaver.", weaver);
			
			if(weaver.length() == 0)
				throw new RuntimeException("Can't find AspectJ weaver on cp.");
			
			args.add("-javaagent:" + weaver);
			args.add("-Dorg.flightofstairs.honours.capture.port=" + port);
			args.addAll(delegate.getJVMArguments());
			
			return args;
		}
		
		@Override
		public List<String> additionalClassPaths() {
			List<String> paths = new LinkedList<String>();
			paths.addAll(delegate.additionalClassPaths());
			paths.add(aspectJar);
			
			return paths;
		}
		
		@Override public File getJARFile() { return delegate.getJARFile(); }
		@Override public List<String> getProgramArguments() { return delegate.getProgramArguments(); }
		@Override public List<String> packages() { return delegate.packages(); }
	}
}
