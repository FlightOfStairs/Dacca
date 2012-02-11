package org.flightofstairs.honours.capture.launchers;

import org.gcontracts.annotations.*

public interface LaunchConfiguration {
	
	@Ensures({ result != null && result.exists() })
	public File getJARFile();
	
	@Ensures({ result != null })
	public List<String> getJVMArguments();
	
	@Ensures({ result != null })
	public List<String> getProgramArguments();
	
	@Ensures({ result != null })
	public List<String> packages();
	
	@Ensures({ result != null })
	public List<String> additionalClassPaths();
}