package org.flightofstairs.honours.capture.recorder;

import org.flightofstairs.honours.common.CallGraph;

public interface Recorder {
	public void recordSession();
	
	public CallGraph getResults();
}
