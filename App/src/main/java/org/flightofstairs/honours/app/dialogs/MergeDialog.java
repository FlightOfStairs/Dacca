package org.flightofstairs.honours.app.dialogs;

import java.io.File;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.flightofstairs.honours.common.CallGraph;

public class MergeDialog extends javax.swing.JDialog {
	
	private final DefaultListModel listModel = new DefaultListModel();

	private File mergedFile = null;
	
	public MergeDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
		
		setLocationRelativeTo(parent);
	}
	
	public File getMergedFile() {
		return mergedFile;
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        mergeButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Merge callgraph files");
        setModal(true);

        list.setModel(listModel);
        list.setToolTipText("List of callgraph files to merge");
        jScrollPane1.setViewportView(list);

        mergeButton.setText("Merge to...");
        mergeButton.setToolTipText("Click to merge listed files to new file");
        mergeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mergeEvent(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.setToolTipText("Exit without merging");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeHandler(evt);
            }
        });

        addButton.setText("Add");
        addButton.setToolTipText("Add Callgraph file to merge set");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEvent(evt);
            }
        });

        removeButton.setText("Remove");
        removeButton.setToolTipText("Remove file from set");
        removeButton.setEnabled(listModel.getSize() > 0);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeEvent(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(mergeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(removeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 114, Short.MAX_VALUE)
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mergeButton)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void closeHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeHandler
		setVisible(false);
	}//GEN-LAST:event_closeHandler

	private void addEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEvent
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select a file to add");
		fileChooser.setFileFilter(new FileNameExtensionFilter("Callgraph files", "callgraph"));
		fileChooser.setMultiSelectionEnabled(true);
		
		int ret = fileChooser.showOpenDialog(this);
		if(ret != JFileChooser.APPROVE_OPTION) return;
		
		for(File file : fileChooser.getSelectedFiles()) {
			boolean exists = false;
			for(int i = 0; i < listModel.size(); i++) 
				if(listModel.get(i).equals(file))
					exists = true;
			
			if(! exists) listModel.addElement(file);
		}
		
		removeButton.setEnabled(listModel.getSize() > 0);
	}//GEN-LAST:event_addEvent

	private void removeEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeEvent
		if(list.getSelectedIndex() != -1) listModel.remove(list.getSelectedIndex());
		
		removeButton.setEnabled(listModel.getSize() > 0);
	}//GEN-LAST:event_removeEvent

	private void mergeEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mergeEvent
		
		// Confirmation code from http://stackoverflow.com/questions/3651494/jfilechooser-with-confirmation-dialog
		final JFileChooser fileChooser = new JFileChooser() {
			@Override
			public void approveSelection(){
				File f = getSelectedFile();
				if(f.exists() && getDialogType() == SAVE_DIALOG){
					int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
					switch(result){
						case JOptionPane.YES_OPTION:
							super.approveSelection();
							return;
						case JOptionPane.NO_OPTION:
							return;
						case JOptionPane.CANCEL_OPTION:
							cancelSelection();
							return;
					}
				}
				super.approveSelection();
			}
		};
		// End of attributed code.

		fileChooser.setDialogTitle("Save merged callgraph");
		fileChooser.setFileFilter(new FileNameExtensionFilter("Callgraph files", "callgraph"));

		int ret = fileChooser.showSaveDialog(this);
		if(ret != JFileChooser.APPROVE_OPTION) return;
		
		CallGraph<String> target = new CallGraph<String>();
		
		for(int i = 0; i < listModel.size(); i++) {
			target.merge(CallGraph.open((File) listModel.get(i)));
		}
		
		target.save(fileChooser.getSelectedFile());
		mergedFile = fileChooser.getSelectedFile();
	}//GEN-LAST:event_mergeEvent


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList list;
    private javax.swing.JButton mergeButton;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables
}
