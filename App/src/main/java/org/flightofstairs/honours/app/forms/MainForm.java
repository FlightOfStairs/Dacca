package org.flightofstairs.honours.app.forms;

import org.apache.commons.io.FilenameUtils;
import org.flightofstairs.honours.app.dialogs.HTMLDialog;
import org.flightofstairs.honours.app.dialogs.LaunchDialog;
import org.flightofstairs.honours.app.dialogs.MergeDialog;
import org.flightofstairs.honours.app.panels.SessionPanel;
import org.flightofstairs.honours.capture.launchers.LaunchConfiguration;
import org.slf4j.LoggerFactory;
import tabbedpane.ClosableTabbedPane;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.rmi.RemoteException;

public class MainForm extends javax.swing.JFrame {
	public MainForm() {
		super();
		initComponents();
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabPanel = new SessionTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        menubar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        toolMenu = new javax.swing.JMenu();
        infoMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mergeMenuItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        helpMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dacca");

        tabPanel.setMinimumSize(new java.awt.Dimension(1307, 824));
        tabPanel.setPreferredSize(new java.awt.Dimension(1307, 824));

        jPanel1.setMinimumSize(new java.awt.Dimension(1319, 824));
        jPanel1.setPreferredSize(new java.awt.Dimension(1319, 824));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Please open or start a new session to begin");
        jPanel1.add(jLabel1, java.awt.BorderLayout.CENTER);

        tabPanel.addTab("Welcome", jPanel1);

        fileMenu.setText("File");

        newMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newMenuItem.setText("Launch application");
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newMenuItem);
        fileMenu.add(jSeparator1);

        openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openMenuItem.setText("Open existing file");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setText("Save");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);
        fileMenu.add(jSeparator2);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menubar.add(fileMenu);

        toolMenu.setText("Tools");

        infoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        infoMenuItem.setText("Callgraph info");
        infoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoMenuItemActionPerformed(evt);
            }
        });
        toolMenu.add(infoMenuItem);
        toolMenu.add(jSeparator3);

        mergeMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        mergeMenuItem.setText("Merge files");
        mergeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mergeMenuItemActionPerformed(evt);
            }
        });
        toolMenu.add(mergeMenuItem);

        menubar.add(toolMenu);

        jMenu1.setText("Help");

        helpMenuItem.setText("Help");
        helpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(helpMenuItem);

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(aboutMenuItem);

        menubar.add(jMenu1);

        setJMenuBar(menubar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void newMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMenuItemActionPerformed
		LaunchDialog launchDialog = new LaunchDialog(this, true);
		
		launchDialog.setVisible(true);
		
		if(launchDialog.launched()) launch(launchDialog.getLaunchConfiguration());
	}//GEN-LAST:event_newMenuItemActionPerformed

	private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
		while(tabPanel.getTabCount() != 0) {
			
			if(tabPanel.getTabComponentAt(0) instanceof SessionPanel)
				((SessionPanel) tabPanel.getTabComponentAt(0)).closing();
			
			tabPanel.remove(0);
		}
		
		System.exit(0);
	}//GEN-LAST:event_exitMenuItemActionPerformed

	private void mergeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mergeMenuItemActionPerformed
		MergeDialog mergeDialog = new MergeDialog(this, true);
		
		mergeDialog.setVisible(true);
		
		if(mergeDialog.getMergedFile() != null) {
			tabPanel.addTab(mergeDialog.getMergedFile().getName(),
								new SessionPanel(mergeDialog.getMergedFile()));
		}
		
		tabPanel.setSelectedIndex(tabPanel.getTabCount() - 1);
	}//GEN-LAST:event_mergeMenuItemActionPerformed

	private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select CallGraph file");
		fileChooser.setFileFilter(new FileNameExtensionFilter("CallGraph files", "callgraph"));
		fileChooser.setMultiSelectionEnabled(false);
		
		int ret = fileChooser.showOpenDialog(this);
		if(ret != JFileChooser.APPROVE_OPTION) return;
		
		open(fileChooser.getSelectedFile());
	}//GEN-LAST:event_openMenuItemActionPerformed

	private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
		if(tabPanel.getSelectedComponent() instanceof SessionPanel)
			((SessionPanel) tabPanel.getSelectedComponent()).save();
	}//GEN-LAST:event_saveMenuItemActionPerformed

	private void helpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpMenuItemActionPerformed
		(new HTMLDialog(this, "Dacca Help", "/Help.html")).setVisible(true);

	}//GEN-LAST:event_helpMenuItemActionPerformed

	private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
		(new HTMLDialog(this, "About Dacca", "/About.html")).setVisible(true);
	}//GEN-LAST:event_aboutMenuItemActionPerformed

	private void infoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoMenuItemActionPerformed
		if(tabPanel.getSelectedComponent() instanceof SessionPanel)
			((SessionPanel) tabPanel.getSelectedComponent()).showInfo();
	}//GEN-LAST:event_infoMenuItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JMenuItem infoMenuItem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JMenuBar menubar;
    private javax.swing.JMenuItem mergeMenuItem;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JTabbedPane tabPanel;
    private javax.swing.JMenu toolMenu;
    // End of variables declaration//GEN-END:variables

	public void launch(final LaunchConfiguration launchConfig) {
		LoggerFactory.getLogger(MainForm.class).debug("Launching jar: {}", launchConfig.getJARFile());
		
		try {
			tabPanel.addTab(launchConfig.getJARFile().getName(),
								new SessionPanel(launchConfig));

			tabPanel.setSelectedIndex(tabPanel.getTabCount() - 1);

		} catch (RemoteException ex) {
            LoggerFactory.getLogger(MainForm.class).error("", ex);
		}
	}
	
	public void open(final File file) {
		LoggerFactory.getLogger(MainForm.class).debug("Opening file: {}", file);
		
		tabPanel.addTab(file.getName(), new SessionPanel(file));
		
		tabPanel.setSelectedIndex(tabPanel.getTabCount() - 1);
	}
	
	private class SessionTabbedPane extends ClosableTabbedPane {
		@Override
		public boolean tabAboutToClose(int tab) {
			
			if(getTabCount() == 1) return false;
			
			if(getComponentAt(tab) instanceof SessionPanel)
				((SessionPanel) getComponentAt(tab)).closing();
			
			return true;
		}
	}
	
	public static void main(final String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				final File file = new File(args.length != 1 ? "" : args[0]);
				
				MainForm form = new MainForm();
				form.setVisible(true);
				
				if(file.exists() && ! file.isDirectory()) {
					if(FilenameUtils.isExtension(file.getPath(), "callgraph")) {
						form.open(file);
					}
					if(FilenameUtils.isExtension(file.getPath(), "jar")) {
						LaunchDialog launchDialog = new LaunchDialog(file, form, true);
		
						launchDialog.setVisible(true);

						if(launchDialog.launched()) form.launch(launchDialog.getLaunchConfiguration());
					}
				}
			}
		});
	}
}
