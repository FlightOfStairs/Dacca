package org.flightofstairs.honours.capture.recorder.launchers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PBLauncher implements AspectJLauncher {
	
	private final File jar;
	private final File aspectJar;
	private final int recorderPort;

	public PBLauncher(File jar, File aspectJar, int recorderPort) {
		this.jar = jar;
		this.aspectJar = aspectJar;
		this.recorderPort = recorderPort;
	}
	
	public void run() {
		String javaPath = System.getProperty("java.home") + File.separatorChar + "bin" + File.separatorChar + "java";

		String cp = System.getProperty("java.class.path");

		// Find weaver agent.
		String weaver = "";
		for(String path : cp.split(":"))
			if(path.contains("aspectjweaver")) weaver = path;

		if(weaver.length() == 0) throw new RuntimeException("Can't find AspectJ weaver on cp.");

		String main = "";
		try(JarInputStream jarStream = new JarInputStream(new FileInputStream(jar))) {
			main = jarStream.getManifest().getMainAttributes().getValue("Main-Class");
		} catch (IOException ex) {
			Logger.getLogger(PBLauncher.class.getName()).log(Level.SEVERE, null, ex);
			throw new RuntimeException("Could not find main class in jar.");
		}

		cp = jar.getAbsolutePath() + File.pathSeparatorChar + cp;
		cp = aspectJar.getAbsolutePath() + File.pathSeparatorChar + cp;

		ProcessBuilder processBuilder = new ProcessBuilder(
				javaPath,
				"-Dorg.flightofstairs.honours.capture.port=" + recorderPort,
				"-cp", cp,
				"-javaagent:" + weaver,
				main).inheritIO();

		processBuilder.environment().put("ASPECTPATH", aspectJar.getAbsolutePath());

		try {
			Process process = processBuilder.start();

			process.waitFor();
		} catch (InterruptedException | IOException ex) {
			Logger.getLogger(PBLauncher.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
