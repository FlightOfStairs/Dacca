
package org.flightofstairs.honours.app.dialogs;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.flightofstairs.honours.app.panels.PackageChooser;
import org.flightofstairs.honours.app.panels.JARUtils;
import org.flightofstairs.honours.capture.launchers.BaseLaunchConfiguration;
import org.flightofstairs.honours.capture.launchers.LaunchConfiguration;

public class LaunchDialog extends javax.swing.JDialog {

	private boolean launched = false;
	
	public LaunchDialog(final java.awt.Frame parent, final boolean modal) {
		super(parent, modal);
		initComponents();
		
		setLocationRelativeTo(parent);
	}
	
	public LaunchDialog(final File file, final java.awt.Frame parent, final boolean modal) {
		this(parent, modal);

		jarPath.setText(file.getPath());
		
		((PackageChooser) packageChooser).setRootText(file.getName());
		((PackageChooser) packageChooser).updateClassList(JARUtils.classesInJarFile(file));
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        launchButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jarPath = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        programArgumentsField = new javax.swing.JTextField();
        jvmArgumentsField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        packageChooser = new PackageChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Launch recording session");
        setMinimumSize(new java.awt.Dimension(534, 272));
        setModal(true);

        launchButton.setText("Launch");
        launchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                launchButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.setPreferredSize(new java.awt.Dimension(94, 25));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("JAR file");

        jarPath.setEditable(false);
        jarPath.setText("Click here to choose executable");
        jarPath.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                chooseJarHandler(evt);
            }
        });

        jLabel2.setText("JVM arguments");

        jLabel3.setText("Program arguments");

        jLabel4.setText("Packages to instrument");

        jScrollPane1.setViewportView(packageChooser);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3)
                    .addComponent(programArgumentsField, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jarPath, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addComponent(jvmArgumentsField))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 37, Short.MAX_VALUE)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(launchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jarPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(programArgumentsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jvmArgumentsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 61, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(launchButton)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void launchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_launchButtonActionPerformed
		if(! (new File(jarPath.getText()).exists())) {
			JOptionPane.showMessageDialog(this, "Please choose a JAR file", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if(((PackageChooser) packageChooser).getSelectedPackages().size() == 0) {
			JOptionPane.showMessageDialog(this, "Doesn't make much sense to record without watching any classes...", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		launched = true;
		
		dispose();
	}//GEN-LAST:event_launchButtonActionPerformed

	private void chooseJarHandler(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chooseJarHandler
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select JAR file");
		fileChooser.setFileFilter(new FileNameExtensionFilter("JAR files", "jar"));
		fileChooser.setMultiSelectionEnabled(false);
		
		int ret = fileChooser.showOpenDialog(this);
		if(ret != JFileChooser.APPROVE_OPTION) return;
		
		jarPath.setText(fileChooser.getSelectedFile().getPath());
		
		// Cast since swing builder only knows about trees.
		((PackageChooser) packageChooser).setRootText(fileChooser.getSelectedFile().getName());
		((PackageChooser) packageChooser).updateClassList(JARUtils.classesInJarFile(fileChooser.getSelectedFile()));
	}//GEN-LAST:event_chooseJarHandler

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
		dispose();
	}//GEN-LAST:event_cancelButtonActionPerformed

	
	public boolean launched() {
		return launched;
	}
	
	public LaunchConfiguration getLaunchConfiguration() {
		if(!launched()) throw new IllegalStateException("Launch configuration useless without "); 
		
		return new BaseLaunchConfiguration(
				new File(jarPath.getText()),
				jvmArgumentsField.getText(),
				programArgumentsField.getText(),
				((PackageChooser) packageChooser).getSelectedPackages());
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jarPath;
    private javax.swing.JTextField jvmArgumentsField;
    private javax.swing.JButton launchButton;
    private javax.swing.JTree packageChooser;
    private javax.swing.JTextField programArgumentsField;
    // End of variables declaration//GEN-END:variables
}
