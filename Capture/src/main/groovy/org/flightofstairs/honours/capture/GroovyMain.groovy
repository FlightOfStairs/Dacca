package org.flightofstairs.honours.capture

import org.flightofstairs.honours.capture.recorder.Recorder;
import org.flightofstairs.honours.capture.recorder.RMIRecorder;

public class GroovyMain {
	public static void main(String... args) {
		
		def inJar = new File("/home/alistair/Projects2011/JHotDraw/dist/JHotDraw.jar");
		
		Recorder recorder = new RMIRecorder(inJar, "within(orrery..*) || within(CH..*)");
		
		recorder.recordSession();
		
		def graph = recorder.getResults();

		System.exit(0);
	}
}