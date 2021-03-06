package org.flightofstairs.honours.app.panels

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import java.awt.GraphicsEnvironment;

class PackageChooserTest extends GroovyTestCase {
	
	public static final List<String> orreryPackages = [
		"CH.ifa.draw.application",
		"CH.ifa.draw.command",
		"CH.ifa.draw.connector",
		"CH.ifa.draw.contrib",
		"CH.ifa.draw.figure.connection",
		"CH.ifa.draw.framework",
		"CH.ifa.draw.handle",
		"CH.ifa.draw.locator",
		"CH.ifa.draw.painter",
		"CH.ifa.draw.palette",
		"CH.ifa.draw.samples.javadraw",
		"CH.ifa.draw.samples.net",
		"CH.ifa.draw.samples.nothing",
		"CH.ifa.draw.samples.pert",
		"CH.ifa.draw.standard",
		"CH.ifa.draw.storable",
		"CH.ifa.draw.tool",
		"CH.ifa.draw.util",
		"orrery.handles",
		"orrery.system"
	]
	
	void testAdd() {
		if(GraphicsEnvironment.isHeadless()) return;
		
		def file = new File(getClass().getResource("/JHotDraw.jar").getFile());
		assertTrue(file.exists());
		
		PackageChooser chooser = new PackageChooser();
		
		def classList = JARUtils.classesInJarFile(file);
		
		chooser.updateClassList(classList);
						
		assertTrue(chooser.getPackages().containsAll(orreryPackages));

		assertFalse(chooser.getPackages().contains("CH.ifa.draw.addition"));
		
		classList << "CH.ifa.draw.addition.NewClass.class";
		
		chooser.updateClassList(classList);
		
		assertTrue(chooser.getPackages().containsAll(orreryPackages));
				
		assertTrue(chooser.getPackages().contains("CH.ifa.draw.addition"));
	}
	
	void dont_testDisplay() {
		if(GraphicsEnvironment.isHeadless()) return;
		
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

