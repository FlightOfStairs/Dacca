package org.flightofstairs.honours.display.selection;

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import groovy.transform.Synchronized;

public class SelectedClassModel {	
	private final Set<String> selected = Collections.synchronizedSet([] as Set);
	
	private final listeners = [] as Set;
	
	
	@Synchronized
	public void setSelection(Set<String> selected) {
		this.selected.clear();
				
		this.selected.addAll(selected);
		
		LoggerFactory.getLogger(SelectedClassModel.class).debug("Setting selected classes: {}.", this.selected);
		
		notifyListeners();
	}
	
	@Synchronized
	public void setSelection(String className) {
		this.selected.clear();
		this.selected << className;
		
		LoggerFactory.getLogger(SelectedClassModel.class).debug("Setting selected classes: {}.", this.selected);
		
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
		LoggerFactory.getLogger(SelectedClassModel.class).debug("Adding SelectionChangeListener.");
		
		listeners << listener;
	}
	
	@Synchronized
	public void removeListener(SelectionChangeListener listener) {
		LoggerFactory.getLogger(SelectedClassModel.class).debug("Removing SelectionChangeListener.");
		
		listeners.remove listener;
	}
	
	private void notifyListeners() {
		listeners.each {
			it.selectionChanged(this);
		}
	}
}

