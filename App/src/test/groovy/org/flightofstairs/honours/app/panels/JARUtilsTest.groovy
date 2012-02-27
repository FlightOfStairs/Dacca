package org.flightofstairs.honours.app.panels

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;

class JARUtilsTest extends GroovyTestCase {
	
	private static final List<String> expectedPackagesJHotDraw = [
		"CH",
		"CH.ifa",
		"CH.ifa.draw",
		"CH.ifa.draw.application",
		"CH.ifa.draw.command",
		"CH.ifa.draw.connector",
		"CH.ifa.draw.contrib",
		"CH.ifa.draw.figure",
		"CH.ifa.draw.figure.connection",
		"CH.ifa.draw.framework",
		"CH.ifa.draw.handle",
		"CH.ifa.draw.locator",
		"CH.ifa.draw.painter",
		"CH.ifa.draw.palette",
		"CH.ifa.draw.samples",
		"CH.ifa.draw.samples.javadraw",
		"CH.ifa.draw.samples.net",
		"CH.ifa.draw.samples.nothing",
		"CH.ifa.draw.samples.pert",
		"CH.ifa.draw.standard",
		"CH.ifa.draw.storable",
		"CH.ifa.draw.tool",
		"CH.ifa.draw.util",
		"orrery",
		"orrery.handles",
		"orrery.system"
	]
	
	void testPackageList() {
		def file = new File(getClass().getResource("/JHotDraw.jar").getFile());
		assertTrue(file.exists());
		
		assertEquals(expectedPackagesJHotDraw, JARUtils.classPackages(JARUtils.classesInJarFile(file)));
	}
}

