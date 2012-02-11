package org.flightofstairs.honours.capture.launchers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import jlibs.core.lang.JavaProcessBuilder;

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
		
		JavaProcessBuilder jvm = new JavaProcessBuilder();
		
		String cp = System.getProperty("java.class.path");
		for(String path : cp.split(File.pathSeparator)) jvm.classpath(path);
		for(String path : launchConfig.additionalClassPaths()) jvm.classpath(path);
	
		jvm.jvmArg(launchConfig.getJVMArguments());
		jvm.arg(launchConfig.getProgramArguments());
				
		jvm.classpath(launchConfig.getJARFile()).mainClass(main);
				
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
