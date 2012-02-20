package org.flightofstairs.honours.display.selection;

import groovy.transform.Synchronized;

public class SelectedClassModel {	
	private final Set<String> selected = Collections.synchronizedSet([] as Set);
	
	private final listeners = [] as Set;
	
	@Synchronized
	public void setSelection(Set<String> selected) {
		this.selected.removeAll();
		this.selected.addAll(selected);
		
		notifyListeners();
	}
	
	@Synchronized
	public void setSelection(String className) {
		this.selected.removeAll();
		this.selected << className;
		
		notifyListeners();
	}
	
	@Synchronized
	public Set<String> getSelection() {
		def result = [] as Set;
		result.addAll(selected);
		return result;
	}
	
	@Synchronized
	public boolean isSelected(String className) {
		return selected.contains(className);
	}
	
	@Synchronized
	public void addListener(SelectionChangeListener listener) {
		listeners << listener;
	}
	
	@Synchronized
	public void removeListener(SelectionChangeListener listener) {
		listeners.remove listener;
	}
	
	private void notifyListeners() {
		listeners.each {
			it.selectionChanged(this);
		}
	}
}

