package org.flightofstairs.honours.capture.launchers

import org.gcontracts.annotations.*

public class BaseLaunchConfiguration implements LaunchConfiguration {
	public final File jarFile;
	
	private final List<String> jvmArguments;
	private final List<String> programArguments;
	
	private final List<String> packages;
	
	public BaseLaunchConfiguration(File jarFile, String jvmArguments, String programArguments, List<String> packages) {
		this(jarFile, splitArgLine(jvmArguments), splitArgLine(programArguments), packages)
	}
	
	public BaseLaunchConfiguration(File jarFile, List<String> jvmArguments, List<String> programArguments, List<String> packages) {
		this.jarFile = jarFile;
		
		this.jvmArguments = Collections.unmodifiableList(jvmArguments);
		this.programArguments = Collections.unmodifiableList(programArguments);
		
		this.packages = Collections.unmodifiableList(packages);
	}
	
	public File getJARFile() { return jarFile }
	public List<String> getJVMArguments() { return jvmArguments }
	public List<String> getProgramArguments() { return programArguments }
	public List<String> packages() { return packages }
	
	public List<String> additionalClassPaths() { return new LinkedList<String>(); };
	
	
	@Requires({ line != null })
	@Ensures({ result != null && result.all({ line.contains(it) })})
	private static List<String> splitArgLine(String line) {
		
		return line.split(" ").collect({ it.trim() }).findAll({ it.length() != 0 });
		
	}
}

