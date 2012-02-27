package org.flightofstairs.honours.app.panels

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;

class PackageChooserTest extends GroovyTestCase {

	// Change name for execution. Displays dialog.
	void testDisplay() {
		def file = new File(getClass().getResource("/JHotDraw.jar").getFile());
		assertTrue(file.exists());
		
		PackageChooser chooser = new PackageChooser();
		
		int notifyCount = 0;
		
		chooser.addNotificationListener({ notifyCount++ } as NotificationListener);
		chooser.addNotificationListener({ println chooser.getSelectedPackages() } as NotificationListener)
		
		chooser.updateClassList(JARUtils.classesInJarFile(file));
		
		assertEquals(["CH", "orrery"], chooser.getSelectedPackages());
		
		assertEquals(1, notifyCount);
		
		def panel = new JPanel();
		panel.add(chooser);
		
		present(panel);
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

