
package org.flightofstairs.honours.app.panels;

import org.flightofstairs.honours.analysis.CacheDecorator;
import org.flightofstairs.honours.analysis.ClassScorer;
import org.flightofstairs.honours.analysis.RankDecorator;
import org.flightofstairs.honours.analysis.ScorerFactory;
import org.flightofstairs.honours.app.dialogs.CallGraphInfoDialog;
import org.flightofstairs.honours.app.dialogs.OverrideFileChooser;
import org.flightofstairs.honours.app.table.ClassTableModel;
import org.flightofstairs.honours.capture.recorder.RMIRecorder;
import org.flightofstairs.honours.capture.recorder.Recorder;
import org.flightofstairs.honours.capture.sources.Source;
import org.flightofstairs.honours.common.CallGraph;
import org.flightofstairs.honours.common.CallGraphListener;
import org.flightofstairs.honours.display.GraphPanel;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.File;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SessionPanel extends javax.swing.JPanel {
	
	private File saveLocation = null;
	
	private final CallGraph callGraph;

	private final String name;
		
	private final DefaultComboBoxModel scorerSelectModel = new DefaultComboBoxModel();
	
	// This should never be null. It would be final if it could.
	private ClassTableModel tableModel;
	private ClassScorer scorer;

	public SessionPanel(final Source source) throws RemoteException {
		
		final Recorder recorder = new RMIRecorder(source);
		
		this.callGraph = recorder.getResults();

		name = source.getName();

		startInit();		
		initComponents();
		endInit();

		ExecutorService service = Executors.newSingleThreadExecutor();
		
		service.submit(recorder);
		
		callGraph.addListener(new CallGraphListener() {
			@Override
			public void callGraphChange(CallGraph callGraph) {
				((PackageChooser) packageChooser).updateClassList(callGraph.classes());
			}
		});
	}
	
	public SessionPanel(File callGraphFile) {
		saveLocation = callGraphFile;
		
		callGraph = CallGraph.open(callGraphFile);

		name = callGraphFile.getName();
		
		startInit();
		initComponents();
		endInit();

		((PackageChooser) packageChooser).updatePackageList(callGraph.classes());
	}

	public void closing() {
		int saveDialogResult = JOptionPane.showConfirmDialog(this, "Save graph before closing?", "Save graph?",JOptionPane.YES_NO_OPTION);
		
		if(saveDialogResult == JOptionPane.YES_OPTION) save();
	}

	public void save() {
		if(saveLocation == null) {
			final JFileChooser fileChooser = new OverrideFileChooser();

			fileChooser.setDialogTitle("Save callgraph");
			fileChooser.setFileFilter(new FileNameExtensionFilter("Callgraph files", "callgraph"));

			final int ret = fileChooser.showSaveDialog(this);
			if(ret != JFileChooser.APPROVE_OPTION) return;
			
			saveLocation = fileChooser.getSelectedFile();
		}
		
		callGraph.save(saveLocation);
	}

	public void showInfo() {
		JFrame frame = (JFrame) SwingUtilities.getRoot(this);
		CallGraphInfoDialog infoDialog = new CallGraphInfoDialog(frame, callGraph);

		infoDialog.setLocationRelativeTo(frame);
		infoDialog.setVisible(true);
	}

	private ClassScorer getScorer() {
		String scorerString;
		
		if(scorerSelector == null) // still in initialization
			scorerString = (String) scorerSelectModel.getElementAt(0);
		else
			scorerString = (String) scorerSelector.getSelectedItem();
		
		return (ClassScorer) ScorerFactory.scorers.get(scorerString).call(callGraph);
	}
	
	private void startInit() {
		for(String s : ScorerFactory.scorers.keySet()) scorerSelectModel.addElement(s);
		
		tableModel = new ClassTableModel(callGraph, getScorer());
		
		this.scorer = new CacheDecorator(callGraph, new RankDecorator(getScorer()));
	}
	
	private void endInit() {
		classList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override public void valueChanged(final ListSelectionEvent event) {
				if(event.getValueIsAdjusting()) return;
				
				final Set<String> selected = new HashSet<String>();
				
				for(int i : classList.getSelectedRows()) {
					selected.add((String) classList.getValueAt(i, 0));
				}
				
				((GraphPanel) displayPanel).selectionModel.setSelection(selected);
			}
		});
		
		((GraphPanel) displayPanel).setPackages(((PackageChooser) packageChooser).getSelectedPackages());
		
		((PackageChooser) packageChooser).addNotificationListener(new NotificationListener() {
			@Override public void eventOccurred() {
				LoggerFactory.getLogger(SessionPanel.class).debug("Setting selected packages: {}", ((PackageChooser) packageChooser).getSelectedPackages());
				
				((GraphPanel) displayPanel).setPackages(((PackageChooser) packageChooser).getSelectedPackages());
			}
		});

		((PackageChooser) packageChooser).setRootText(name);
	}

	
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        displayPanel = new GraphPanel(callGraph, getScorer());
        jLabel2 = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        packageChooser = new PackageChooser();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        classList = new javax.swing.JTable();
        scorerSelector = new javax.swing.JComboBox();

        displayPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        displayPanel.setMinimumSize(new java.awt.Dimension(600, 600));

        javax.swing.GroupLayout displayPanelLayout = new javax.swing.GroupLayout(displayPanel);
        displayPanel.setLayout(displayPanelLayout);
        displayPanelLayout.setHorizontalGroup(
            displayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 996, Short.MAX_VALUE)
        );
        displayPanelLayout.setVerticalGroup(
            displayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel2.setText("Class Ranking Metric");

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jLabel3.setText("Packages Displayed");

        jScrollPane2.setViewportView(packageChooser);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(51, 51, 51))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setTopComponent(jPanel1);

        jLabel1.setText("Classes");

        classList.setAutoCreateRowSorter(true);
        classList.setModel(tableModel);
        classList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(classList);
        classList.getColumnModel().getColumn(0).setCellRenderer(new ColourRenderer(this));
        classList.getColumnModel().getColumn(1).setCellRenderer(new ColourRenderer(this));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 192, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel2);

        scorerSelector.setModel(scorerSelectModel);
        scorerSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scorerSelectorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(displayPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scorerSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(scorerSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jSplitPane1))
                        .addComponent(displayPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(displayPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ((GraphPanel) displayPanel).initGraphPanel();
    }// </editor-fold>//GEN-END:initComponents

	private void scorerSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scorerSelectorActionPerformed
		final ClassScorer rawScorer = getScorer();
		
		this.scorer = new CacheDecorator(callGraph, new RankDecorator(rawScorer));
		
		getDisplayPanel().setScorer(rawScorer);
		
		tableModel.setScorer(rawScorer);
	}//GEN-LAST:event_scorerSelectorActionPerformed

	
	private GraphPanel getDisplayPanel() { return (GraphPanel) displayPanel; }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable classList;
    private javax.swing.JPanel displayPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTree packageChooser;
    private javax.swing.JComboBox scorerSelector;
	// End of variables declaration//GEN-END:variables

	
	public class ColourRenderer extends JLabel implements TableCellRenderer {
		private final SessionPanel panel;
		
		public ColourRenderer(SessionPanel panel) {
			this.panel = panel;
			
			setOpaque(true);
		}
 
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
		
			String name = value.toString();
			
			// Shorten name if it's name column.
			if(vColIndex == 0)
				setText(name.substring(name.lastIndexOf(".") + 1));
			else
				setText(name);
			
			setToolTipText(name);
			final Double score = (Double) panel.scorer.rank().get((String) table.getValueAt(rowIndex, 0));
			
			if(score != null) {
				int colourness = (int) (255 * score);
				
				if(isSelected) {
					setBackground(new Color(255 - colourness, 255 - colourness, 255));
				} else {
					setBackground(new Color(255 - colourness, 255, 255 - colourness));
				}
			}

			return this;
		}
	}
}
