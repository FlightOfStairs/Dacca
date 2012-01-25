package org.flightofstairs.honours.capture.recorder.launchers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import jlibs.core.lang.JavaProcessBuilder;

public class JPBLauncher implements AspectJLauncher {
	
	private final File jar;
	private final File aspectJar;
	private final int recorderPort;

	public JPBLauncher(File jar, File aspectJar, int recorderPort) {
		this.jar = jar;
		this.aspectJar = aspectJar;
		this.recorderPort = recorderPort;
	}
	
	@Override
	public void run() {
		String main = "";
		try {
			JarInputStream jarStream = new JarInputStream(new FileInputStream(jar));
			main = jarStream.getManifest().getMainAttributes().getValue("Main-Class");
			jarStream.close();
		} catch (IOException ex) {
			Logger.getLogger(JPBLauncher.class.getName()).log(Level.SEVERE, null, ex);
			throw new RuntimeException("Could not find main class in jar.");
		}
		
		String cp = System.getProperty("java.class.path");

		// Find weaver agent.
		String weaver = "";
		for(String path : cp.split(File.pathSeparator))
			if(path.contains("aspectjweaver")) weaver = path;
		
		if(weaver.length() == 0) throw new RuntimeException("Can't find AspectJ weaver on cp.");

		
		JavaProcessBuilder jvm = new JavaProcessBuilder();
		
		for(String path : cp.split(File.pathSeparator)) jvm.classpath(path);
				
		jvm.classpath(jar)
				.classpath(aspectJar)
				.systemProperty("org.flightofstairs.honours.capture.port", recorderPort + "")
				.jvmArg("-javaagent:" + weaver)
				.mainClass(main);
		
		try {
			Process p = jvm.launch(System.out, System.err);
			
			p.waitFor();
		} catch (InterruptedException ex) {
			Logger.getLogger(JPBLauncher.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(JPBLauncher.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
