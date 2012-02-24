package org.flightofstairs.honours.app.dialogs;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

// Confirmation code from http://stackoverflow.com/questions/3651494/jfilechooser-with-confirmation-dialog

public class OverrideFileChooser extends JFileChooser {
	@Override
	public void approveSelection() {
		final File file = getSelectedFile();
		if(file.exists() && getDialogType() == SAVE_DIALOG) {
			final int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
			switch(result) {
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
}
