package org.flightofstairs.honours.app.panels

import java.util.jar.JarFile;

import java.util.List;

import javax.swing.JScrollPane;
import java.awt.Dimension;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
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
	
	private final TreeCheckingListener treeCheckingListener = { notifyListeners() } as TreeCheckingListener;
	
	public PackageChooser() {
		super();
		
		setModel(model);
		
		getCheckingModel().setCheckingMode(TreeCheckingModel.CheckingMode.PROPAGATE_PRESERVING_CHECK)
		
		addTreeCheckingListener(treeCheckingListener);
		
		model.setRoot(new DefaultMutableTreeNode("Packages"));
	}
	
	public void setRootText(final String text) {
		model.getRoot().setUserObject(text);
	}

	public void updateClassList(final List<String> classes) { updatePackageList(JARUtils.classPackages(classes)); }
	
	public void updatePackageList(final List<String> packages) {		
		final def newPackages = packages.clone();
		newPackages.removeAll(getPackages());
		
		final def previouslySelected = getSelectedPackages();
		final def previouslyExpanded = getExpandedPackages();
		
		setPackages(packages)
		
		removeTreeCheckingListener(treeCheckingListener);

		setSelectedPackages(previouslySelected);
		setSelectedPackages(newPackages);
		
		addTreeCheckingListener(treeCheckingListener);
		
		expandRow(0);
		setExpandedPackages(previouslyExpanded);
		
		notifyListeners();
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
	
	private void setPackages(final Collection<String> packages) {
		def sortedList = packages.sort();
		
		DefaultMutableTreeNode root = model.getRoot();
		root.removeAllChildren();
		
		sortedList.each {
			addPackageParts(root, it.tokenize("."));
		}
	}
	
	private static void addPackageParts(DefaultMutableTreeNode node, List<String> classParts) {
		if(classParts.size() == 0) return;

		TreeNode next;
		
		for(TreeNode child : node.children()) {
			if(child.getUserObject() == classParts.head()) {
				next = child;
				break;
			}
		}
				
		if(next == null) {
			next = new DefaultMutableTreeNode(classParts.head());
			node.add(next);
		}
		
		addPackageParts(next, classParts.tail());
	}
	
	@Ensures({ result != null })
	public List<String> getSelectedPackages() {
		def roots = getCheckingRoots();
		
		if(roots.size() == 1 && roots[0].getPath().size() == 1)
			return model.getRoot().children.collect { it.getUserObject() }
		
		return roots.collect { it.getPath()[1..<it.getPath().size()].join(".") }
	}
	
	public void setSelectedPackages(List<String> packages) {		
		for(item in packages) {
			def pathParts = pathFrom(model.getRoot(), item.split(".") as List)
			
			def path = new TreePath(model.getRoot());
			
			while(pathParts.size() != 0) 
				path = path.pathByAddingChild(pathParts.remove(0));
			
			setCheckingPath(path);
		}
	}
	
	private static TreeNode[] pathFrom(DefaultMutableTreeNode node, List<String> packageParts) {
		if(packageParts.size() == 0) return [];
		
		def nextNode = node.children().toList().find({ it.getUserObject() == packageParts[0]});
		
		def result = [nextNode];
		
		result.addAll pathFrom(nextNode, packageParts.tail());
		
		return result as TreeNode[];
	}
	
	private List<String> getExpandedPackages() {
		def paths = getPackages().collect { new TreePath(pathFrom(model.getRoot(), it.split(".") as List<String>)) }
		
		return paths.findAll({ isNodeExpanded(it) })*.join(".");
	}
	
	private List<String> setExpandedPackages(List<String> packages) {
		def paths = packages.collect { new TreePath(pathFrom(model.getRoot(), it.split(".") as List<String>)) }
		
		paths.each {
			expandPath(it)
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	
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
}