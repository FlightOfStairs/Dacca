package org.flightofstairs.honours.app.forms;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.flightofstairs.honours.app.dialogs.LaunchDialog;
import org.flightofstairs.honours.app.dialogs.MergeDialog;
import org.flightofstairs.honours.app.panels.SessionPanel;
import org.flightofstairs.honours.capture.launchers.LaunchConfiguration;
import tabbedpane.ClosableTabbedPane;

public class MainForm extends javax.swing.JFrame {

	public MainForm() {
		initComponents();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
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
        mergeMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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
        newMenuItem.setText("New");
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newMenuItem);
        fileMenu.add(jSeparator1);

        openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openMenuItem.setText("Open");
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

        mergeMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        mergeMenuItem.setText("Merge files");
        mergeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mergeMenuItemActionPerformed(evt);
            }
        });
        toolMenu.add(mergeMenuItem);

        menubar.add(toolMenu);

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
		
		if(launchDialog.launched()) {
			try {
				LaunchConfiguration launchConfiguration = launchDialog.getLaunchConfiguration();
				
				tabPanel.addTab(launchConfiguration.getJARFile().getName(),
									new SessionPanel(launchConfiguration));
				
				tabPanel.setSelectedIndex(tabPanel.getTabCount() - 1);
				
			} catch (RemoteException ex) {
				Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
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
		
		tabPanel.addTab(fileChooser.getSelectedFile().getName(),
							new SessionPanel(fileChooser.getSelectedFile()));
		
		tabPanel.setSelectedIndex(tabPanel.getTabCount() - 1);
	}//GEN-LAST:event_openMenuItemActionPerformed

	private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
		if(tabPanel.getTabComponentAt(tabPanel.getSelectedIndex()) instanceof SessionPanel)
			((SessionPanel) tabPanel.getTabComponentAt(tabPanel.getSelectedIndex())).save();
	}//GEN-LAST:event_saveMenuItemActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {

			public void run() {
				new MainForm().setVisible(true);
			}
		});
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JMenuBar menubar;
    private javax.swing.JMenuItem mergeMenuItem;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JTabbedPane tabPanel;
    private javax.swing.JMenu toolMenu;
    // End of variables declaration//GEN-END:variables

	private class SessionTabbedPane extends ClosableTabbedPane {
		@Override
		public boolean tabAboutToClose(int tab) {
			
			if(getTabCount() == 1) return false;
			
			if(getTabComponentAt(tab) instanceof SessionPanel)
				((SessionPanel) getTabComponentAt(tab)).closing();
			
			return true;
		}
	}
}
