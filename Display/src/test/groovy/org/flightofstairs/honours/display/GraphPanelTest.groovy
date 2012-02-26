package org.flightofstairs.honours.display

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import org.flightofstairs.honours.common.CallGraph;

import java.awt.Dimension;

import org.junit.Ignore

import org.flightofstairs.honours.analysis.HITSScorer;

class GraphPanelTest extends GroovyTestCase {
	private CallGraph orrery;
	
	void setUp() {
		def file = new File(getClass().getResource("/orrery.callgraph").getFile());
		
		assertTrue(file.exists());
		
		orrery = CallGraph.open(file);
	}
	
	void dont_testHiddenSelection() {
		def gp = new GraphPanel(orrery, new HITSScorer(orrery))
		// this stops GroovyTestCase complaining without having to display dialog during build.
		
		gp.initGraphPanel();
		gp.selectionModel.setSelection("orrery.PlanetFigure");
	}
	
	void dont_testDisplay() {
		def gp = new GraphPanel(orrery, new HITSScorer(orrery));
		gp.setPreferredSize(new Dimension(800, 800));
		gp.initGraphPanel();
		gp.selectionModel.setSelection("orrery.PlanetFigure");
		present(gp);
	}
	
	private void present(JPanel panel) {
		def diag = new JDialog();
		diag.setModal(true)
		
		diag.setLayout(new BorderLayout());
		diag.getContentPane().add(panel);
		
		def failButton = new JButton("fail");
		
		boolean failed = false;
		
		failButton.addActionListener({
				failed = true;
				diag.setVisible(false);
			} as ActionListener);
		
		diag.add(failButton, BorderLayout.SOUTH);
		
		diag.pack();
		diag.setVisible(true);
		while(diag.isVisible()) Thread.sleep(20);
		
		if(failed) fail("User said fail.");
	}
}

