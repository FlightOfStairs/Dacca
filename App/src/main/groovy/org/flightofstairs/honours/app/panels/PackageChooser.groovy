package org.flightofstairs.honours.app.panels

import java.util.jar.JarFile;

import javax.swing.JScrollPane;
import java.awt.Dimension;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.BorderLayout;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingListener;

import org.gcontracts.annotations.*
import groovy.transform.Synchronized

class PackageChooser extends CheckboxTree {
	private final DefaultTreeModel model = new DefaultTreeModel(new DefaultMutableTreeNode("Packages"));
		
	private final List<NotificationListener> listeners = [];
	
	public PackageChooser() {
		super();
		
		setModel(model);
		
		getCheckingModel().setCheckingMode(TreeCheckingModel.CheckingMode.PROPAGATE_PRESERVING_CHECK)
		
		addTreeCheckingListener({ notifyListeners() } as TreeCheckingListener);
	}
	
	@Requires({ file != null && file.exists() })
	public void setJarFile(File file) {
		model.setRoot(new DefaultMutableTreeNode(file.getName()))
		setPackageList(packagesUsed(jarClasses(file)));
		
		expandRow(0);
	}
	
	@Requires({ packages != null })
	private setPackageList(List<String> packages) {
		
		addPackages(model.getRoot(), packages)
		
		notifyListeners();
	}
	
	@Requires({ listener != null && ! listeners.contains(listener) })
	@Ensures({ listeners.contains(listener) })
	@Synchronized
	public void addNotificationListener(NotificationListener listener) { listeners << listener }
	
	@Requires({ listener != null })
	@Ensures({ ! listeners.contains(listener) })
	@Synchronized
	public void removeNotificationListener(NotificationListener listener) { listeners.remove listener }
	
	@Synchronized
	private void notifyListeners() {
		listeners.each { it.eventOccurred() }
	}
	
	@Requires({ node != null && packages != null })
	private void addPackages(DefaultMutableTreeNode node, List<String> packages) {
		while(packages.size() != 0) {
			def first = packages.remove(0);
			
			def firstParts = first.tokenize('.');
			
			if(firstParts.size() == 1) {
				node.add(new DefaultMutableTreeNode(firstParts[0]));
				continue;
			}
			
			def samePackage = [first];
			
			while(packages.size() != 0 && packages.get(0).tokenize('.')[0].equals(firstParts[0])) {
				samePackage << packages.remove(0);
			}
			
			samePackage = samePackage.collect { 
				def parts = it.tokenize('.')
				parts[1..<parts.size()].join(".")
			}
			
			TreeNode newNode = node.children().find { it.getUserObject().equals(firstParts[0]) };
			if(newNode == null) newNode = new DefaultMutableTreeNode(firstParts[0]);
			node.add(newNode);
			
			addPackages(newNode, samePackage);
		}
	}
	
	@Ensures({ result != null })
	public List<String> getSelectedPackages() {
		def roots = getCheckingRoots();
		
		if(roots.size() == 1 && roots[0].getPath().size() == 1)
			return model.getRoot().children.collect { it.getUserObject() }
		
		return roots.collect { it.getPath()[1..<it.getPath().size()].join(".") }
	}
	
	@Requires({ classes != null })
	@Ensures({ result != null })
	private static List<String> packagesUsed(List<String> classes) {
		def packages = [] as Set
		
		classes.each {
			def parts = it.tokenize('.')
			
			(0..<parts.size() - 2).each {
				packages << parts[0..it].join(".");
			}
		}
		
		return packages.sort { it }
	}
	
	@Requires({ file != null && file.exists() })
	@Ensures({ result != null && ! result.any { it.endsWith(".class") } })
	private static List<String> jarClasses(File file) {
		JarFile jarFile = new JarFile(file);
		
		def classes = [] as Set;

		jarFile.entries().each {
			def parts = it.getName().tokenize('.')
			if(parts[parts.size() - 1].equals("class")) {
				parts = it.getName().tokenize('/')		// TODO check this on windows.
				classes << parts[0..<parts.size()].join(".")
			}
		}
		
		return classes.sort { it };
	}
}