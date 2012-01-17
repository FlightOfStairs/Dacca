package org.flightofstairs.honours.app.panels

import java.util.jar.JarFile;

import javax.swing.JPanel;
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

class PackageChooser extends JPanel {
	private final DefaultTreeModel model = new DefaultTreeModel(new DefaultMutableTreeNode("Packages"));
	
	private final CheckboxTree tree;
	
	private final List<NotificationListener> listeners = [];
	
	public PackageChooser() {
		tree = new CheckboxTree(model);
		
		tree.getCheckingModel().setCheckingMode(TreeCheckingModel.CheckingMode.PROPAGATE_PRESERVING_CHECK)
		
		JScrollPane sp = new JScrollPane(tree);
		
		setLayout(new BorderLayout());
		add(sp, BorderLayout.CENTER);
		
		tree.addTreeCheckingListener({ notifyListeners() } as TreeCheckingListener);
	}
	
	@Requires({ file != null && file.exists() })
	public void setJarFile(File file) {
		model.setRoot(new DefaultMutableTreeNode(file.getName()))
		
		addPackages(model.getRoot(), jarPackages(file))
		
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
		def roots = tree.getCheckingRoots();
		
		if(roots.size() == 1 && roots[0].getPath().size() == 1)
			return model.getRoot().children.collect { it.getUserObject() }
		
		return roots.collect { it.getPath()[1..<it.getPath().size()].join(".") }
	}
	
	@Requires({ file != null && file.exists() })
	@Ensures({ result})
	private static List<String> jarPackages(File file) {
		JarFile jarFile = new JarFile(file);
		
		def usedPackages = [] as Set;

		jarFile.entries().each {
			def parts = it.getName().tokenize('.')
			if(parts[parts.size() - 1].equals("class")) {
				parts = it.getName().tokenize('/')		// TODO check this on windows.
				usedPackages << parts[0..<(parts.size() - 1)].join(".")
			}
		}
		
		def packages = [] as Set
		usedPackages.each {
			def parts = it.tokenize('.')
			
			(0..<parts.size()).each {
				packages << parts[0..it].join(".");
			}
		}
		return packages.sort { it };
	}
}