package org.flightofstairs.honours.app

import org.flightofstairs.honours.capture.recorder.Recorder;
import org.flightofstairs.honours.capture.recorder.RMIRecorder;

import org.flightofstairs.honours.display.GraphPanel;

import javax.swing.JFrame;

import org.flightofstairs.honours.analysis.HITSScorer;

public class App {
	public static void main(String[] args) {
		def inJar = new File("/home/alistair/Projects2011/JHotDraw/dist/JHotDraw.jar");
		
		Recorder recorder = new RMIRecorder(inJar, "within(orrery..*) || within(CH..*)");
				
		def graph = recorder.getResults();
		
		Thread recordThread = new Thread({ recorder.recordSession() } as Runnable);
		
		def gp = new GraphPanel(graph);
		gp.setScorer(new HITSScorer());
		
		def frame = new JFrame("GraphStuff");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().add(gp);
		frame.pack();
		frame.show();
		
		recordThread.start();
	}
}

