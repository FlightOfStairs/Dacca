package org.flightofstairs.honours.app.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.ListModel;
import javax.swing.DefaultListModel;


import groovy.swing.SwingBuilder

class MergeDialog extends JDialog {
	private final ListModel model = new DefaultListModel();
	
	public MergeDialog() {
		setModal(true);
		
		def swing = new SwingBuilder();
		
		def panel = swing.panel {
			borderLayout(hgap: 5, vgap: 5)
			label(text: "Add ")
			
			scrollPane(constraints:BorderLayout.CENTER) {
				list(model:this.model)
			}
			panel(constraints:BorderLayout.EAST) {
				borderLayout(hgap: 5, vgap: 5)
				panel(constraints:BorderLayout.NORTH) {
					gridLayout(cols:1, rows:0, hgap: 5, vgap: 5)
					button(text:"add")
					button(text:"remove")
				}
				panel(constraints:BorderLayout.SOUTH) {
					gridLayout(cols:1, rows:0, hgap: 5, vgap: 5)
					button(text:"cancel")
					button(text:"save")
				}
			}
		}
		setPreferredSize(new Dimension(500, 500))
		setResizable(false);
		setLayout(new BorderLayout(5, 5));
		add(panel, BorderLayout.CENTER);
		pack();
	}
}

