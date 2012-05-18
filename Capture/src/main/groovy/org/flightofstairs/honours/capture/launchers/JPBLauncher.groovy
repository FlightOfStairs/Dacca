package org.flightofstairs.honours.capture.launchers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.flightofstairs.honours.capture.recorder.RMIRecorder;
import org.flightofstairs.honours.capture.recorder.UNCCompatibleJavaProcessBuilder
import org.gcontracts.annotations.Requires;

public class JPBLauncher implements Launcher {
	@Override
	public void launch(LaunchConfiguration launchConfig) {
		
		String main = "";
		try {
			JarInputStream jarStream = new JarInputStream(new FileInputStream(launchConfig.getJARFile()));
			
			main = jarStream.getManifest().getMainAttributes().getValue("Main-Class");
			jarStream.close();
		} catch (IOException ex) {
			Logger.getLogger(JPBLauncher.class.getName()).log(Level.SEVERE, null, ex);
			throw new RuntimeException("Could not find main class in jar.");
		}
		
		UNCCompatibleJavaProcessBuilder jvm = new UNCCompatibleJavaProcessBuilder();

		String cp = System.getProperty("java.class.path");

		for(String path : cp.split(File.pathSeparator)) jvm.classpath(path);
		for(String path : launchConfig.additionalClassPaths()) jvm.classpath(path);
		
		for(String arg : launchConfig.getJVMArguments()) jvm.jvmArg(arg);
		for(String arg : launchConfig.getProgramArguments()) jvm.arg(arg);
		
		jvm.classpath(launchConfig.getJARFile()).mainClass(main);

		LoggerFactory.getLogger(JPBLauncher.class).info("Launching traced application: {}", jvm.command().join(" "));
		
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
