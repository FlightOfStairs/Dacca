package org.flightofstairs.honours.capture.launchers;

public interface LaunchConfiguration {
	public File getJARFile();
	public String getJVMArguments();
	public String getProgramArguments();
	public List<String> packages();
	
	public List<String> additionalClassPaths();
}

