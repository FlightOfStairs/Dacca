package org.flightofstairs.honours.app.dialogs;

import com.sun.tools.attach.VirtualMachineDescriptor;
import org.flightofstairs.honours.capture.sources.AttachSource;
import org.flightofstairs.honours.capture.sources.Source;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;


public class AttachDialog extends javax.swing.JDialog {
	
	private final List<VirtualMachineDescriptor> descriptors = new LinkedList<VirtualMachineDescriptor>();

	private final VMTableModel tableModel = new VMTableModel();
	
	private Source source = null;
	public Source getSource() { return source; }
	public boolean isAccepted() { return source != null; }

	public AttachDialog(final java.awt.Frame parent, final boolean modal) {
		super(parent, modal);
		
		refreshVMs();
		
		initComponents();
	}

	private List<String> getPackages() {
		List<String> packages = new LinkedList<String>();
		
		StringTokenizer tokenizer = new StringTokenizer(packageTextField.getText(), ",");
		
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().trim();

			if(! token.isEmpty())
				packages.add(token);
		}
		
		return packages;
	}
	
	private void refreshVMs() {
		descriptors.clear();

		descriptors.addAll(AttachSource.getVMList());

		tableModel.fireTableDataChanged();
	}

	private class VMTableModel extends AbstractTableModel {
		@Override public int getRowCount() { return descriptors.size(); }
		@Override public int getColumnCount() { return 2; }

		@Override
		public Object getValueAt(int row, int column) {
			if(column == 0) return descriptors.get(row).id();
			if(column == 1) return descriptors.get(row).displayName();

			return null;
		}

		public String getColumnName(int column) {
			if(column == 0) return "Process ID";
			if(column == 1) return "Name";

			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        vmTable = new javax.swing.JTable();
        refreshButton = new javax.swing.JButton();
        attachButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        warningLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        packageTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Attach to running JVM");
        setMinimumSize(new java.awt.Dimension(464, 409));
        setModal(true);

        vmTable.setModel(tableModel);
        vmTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(vmTable);
        vmTable.getColumnModel().getColumn(0).setMaxWidth(75);

        refreshButton.setText("Refresh JVMs");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        attachButton.setText("Attach");
        attachButton.setPreferredSize(new java.awt.Dimension(100, 25));
        attachButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attachButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.setPreferredSize(new java.awt.Dimension(100, 25));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        warningLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        warningLabel.setText("<html> Important:<br/> <br/> Attaching an agent will not provide complete information.<br/><br/> Important startup details will be lost.<br/><br/> Objects with frames currently on the stack will not provide information until they have been completely removed from the stack.</html>"); // NOI18N
        warningLabel.setToolTipText("<html>Limits are imposed by the JVM specification.<br/><br/>\n\nAn object has a frame on the stack if one of its methods has been called that has not yet returned.");
        warningLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel1.setText("Packages seperated by comma");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 82, Short.MAX_VALUE))
                    .addComponent(packageTextField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(warningLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(refreshButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(attachButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(refreshButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(warningLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(attachButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(packageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
		refreshVMs();
	}//GEN-LAST:event_refreshButtonActionPerformed

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
		setVisible(false);
	}//GEN-LAST:event_cancelButtonActionPerformed

	private void attachButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attachButtonActionPerformed
		if(getPackages().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Doesn't make much sense to record without watching any classes...", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		source = new AttachSource(descriptors.get(vmTable.getSelectedRow()), getPackages());
		dispose();
	}//GEN-LAST:event_attachButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton attachButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField packageTextField;
    private javax.swing.JButton refreshButton;
    private javax.swing.JTable vmTable;
    private javax.swing.JLabel warningLabel;
    // End of variables declaration//GEN-END:variables
}
