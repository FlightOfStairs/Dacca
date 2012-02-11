package org.flightofstairs.honours.capture.launchers

public class BaseLaunchConfiguration implements LaunchConfiguration {
	public final File jarFile;
	
	private final String jvmArguments;
	private final String programArguments;
	
	private final List<String> packages;
	
	public BaseLaunchConfiguration(File jarFile, String jvmArguments, String programArguments, List<String> packages) {
		this.jarFile = jarFile;
		this.jvmArguments = jvmArguments;
		this.programArguments = programArguments;
		
		this.packages = Collections.unmodifiableList(packages);
	}
	
	public File getJARFile() { return jarFile }
	public String getJVMArguments() { return jvmArguments }
	public String getProgramArguments() { return programArguments }
	public List<String> packages() { return packages }
	
	public List<String> additionalClassPaths() { return new LinkedList<String>(); };
}

