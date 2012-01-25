package org.flightofstairs.honours.app

import org.flightofstairs.honours.capture.recorder.Recorder;
import org.flightofstairs.honours.capture.recorder.RMIRecorder;

import org.flightofstairs.honours.display.GraphPanel;

import javax.swing.JFrame;

import org.flightofstairs.honours.analysis.HITSScorer;

public class App {
	public static void main(String[] args) {
		def inJar = new File(getClass().getResource("/JHotDraw.jar").getFile()); // TODO delete resource when this changes
		
		Recorder recorder = new RMIRecorder(inJar, "within(orrery..*) || within(CH..*)");
				
		def graph = recorder.getResults();
				
		def gp = new GraphPanel(graph);
		gp.setScorer(new HITSScorer());
		
		def frame = new JFrame("Graph Stuff");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().add(gp);
		frame.pack();
		frame.show();
		
		Thread recordThread = new Thread({ recorder.recordSession() } as Runnable);
		recordThread.start();
	}
}

