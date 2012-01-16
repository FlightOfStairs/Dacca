package org.flightofstairs.honours.display

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import org.flightofstairs.honours.common.CallGraph;

import org.junit.Ignore

import org.flightofstairs.honours.analysis.HITSScorer;

class GraphPanelTest extends GroovyTestCase {
	private CallGraph orrery;
	
	void setUp() {
		def file = new File(getClass().getResource("/orrery.callgraph").getFile());
		
		assertTrue(file.exists());
		
		orrery = CallGraph.open(file);
	}
	
//	void testStatic() {
//		present(new GraphPanel(orrery));
//	}
	
	void testSetScorer() {
		def gp = new GraphPanel(orrery);
		gp.setScorer(new HITSScorer());
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

