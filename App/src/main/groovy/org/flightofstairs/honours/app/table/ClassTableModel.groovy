package org.flightofstairs.honours.app.table;

import javax.swing.table.AbstractTableModel;
import org.flightofstairs.honours.common.CallGraph;
import org.flightofstairs.honours.common.CallGraphListener;
import org.flightofstairs.honours.analysis.ClassScorer;
import org.flightofstairs.honours.analysis.CacheDecorator;

import groovy.transform.Synchronized;

import org.gcontracts.annotations.*

@Invariant({ scorer != null })
public class ClassTableModel extends AbstractTableModel {
	
	private final CallGraph callGraph;
	
	private ClassScorer scorer;
	
	private List<String> sorted;
	private Map<String, Double> scores;
	
	@Requires({callGraph != null && scorer != null})
	public ClassTableModel(CallGraph callGraph, ClassScorer scorer) {
		this.callGraph = callGraph;
		
		setScorer(scorer);
		
		callGraph.addListener({ updateClassData() } as CallGraphListener);
	}
	
	@Synchronized
	private void updateClassData() {
		if(sorted == null || scores == null) {
			sorted = new LinkedList<String>();
			scores = new HashMap<String, Double>();
		}
		
		def oldSorted = sorted;
		def oldScores = scores;
		
		def newSorted = callGraph.classes();
		
		def newScores = [:]
		newSorted.each {
			newScores[it] = scorer.rank()[it]
		}
		
		newSorted.sort { a, b ->
				return newScores[b] <=> newScores[a]
			}
		
		def changes = [] as Set;
			
		if(newSorted.size() != oldSorted.size()) {
			sorted = newSorted;
			scores = newScores;
			
			fireTableDataChanged();
		} else {
			for(i in 0..<newSorted.size()) {
				if(newSorted[i] != oldSorted[i]) {
					changes << new Tuple(0, i);
				}
				if(newScores[newSorted[i]] != oldScores[oldSorted[i]]) {
					changes << new Tuple(1, i);
				}
			}
		}
		
		if(! changes.isEmpty()) {
			sorted = newSorted;
			scores = newScores;
			
			changes.each {
				fireTableCellUpdated(it[1], it[0]);
			}
		}
	}
	
	@Requires({ scorer != null })
	public void setScorer(ClassScorer scorer) {
		this.scorer =  new CacheDecorator(callGraph, scorer);
		
		updateClassData();
	}
	
	@Override
	public int getRowCount() {
		if(sorted == null) updateClassData();
		
		return sorted.size();
	}
	
	public int getColumnCount() { return 2; }
	
	public String getColumnName(int column) {
		return column == 0 ? "Class" : "Score";
	}
	
	public Class<?> getColumnClass(int column) {
		return column == 0 ? String.class : Double.class;
	}
	
	@Requires({ column <= 1; row < getRowCount() })
	public Object getValueAt(int row, int column) {
		if(sorted == null || scores == null) updateClassData();
		
		return column == 0 ? sorted[row] : scores[sorted[row]];
	}
}

