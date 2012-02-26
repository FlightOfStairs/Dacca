package org.flightofstairs.honours.app.panels

import java.util.jar.JarFile;

import java.util.List;

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

import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
	
	@Requires({ classes != null && rootText != null })
	public void setClassList(String rootText, List<String> classes) {
		model.setRoot(new DefaultMutableTreeNode(rootText))
		setPackageList(packagesUsed(classes));
		
		expandRow(0);
	}
	
	private Set<String> getPackages() {
		def packages = [] as Set;
		
		DefaultMutableTreeNode root = model.getRoot();
		
		for(DefaultMutableTreeNode child : root.children()) {
			packages << getPackagesFrom(child);
		}
		
		return packages;
	}
	
	private static Set<String> getPackagesFrom(DefaultMutableTreeNode node) {
		if(node.isLeaf()) return [node.getUserObject().toString()];
		
		def packages = [] as Set;
		
		for(DefaultMutableTreeNode child : node.children()) {
			packages.addAll(getPackagesFrom(child).collect { node.getUserObject().toString() + "." + it});
		}
		
		return packages;
	}
	
	private void setPackages(final Set<String> packages) {
		def sortedList = packages.sort();
		
		def root = model.getRoot();
		
		sortedList.each {
			addPackageParts(root);
		}
	}
	
	private static void addPackageParts(DefaultMutableTreeNode node, List<String> classParts) {
		if(classParts.size() == 0) return;
		
		TreeNode next;
		
		for(TreeNode child : node.children()) {
			if(child.getUserObject() == classParts[0]) {
				next = child;
				break;
			}
		}
		
		if(next == null) {
			next = new DefaultMutableTreeNode(classParts[0]);
			node.add(next);
		}
		
		addClassParts(next, classParts[1..<classParts.size()]);
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