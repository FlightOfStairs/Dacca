package org.flightofstairs.honours.capture.recorder;

import org.flightofstairs.honours.common.CallGraph;

public interface Recorder extends Runnable {
	public CallGraph getResults();
}
