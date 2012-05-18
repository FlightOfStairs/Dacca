package org.flightofstairs.honours.display.selection

import org.gcontracts.annotations.Requires

public interface SelectionChangeListener {
	
	@Requires({ model != null })
	public void selectionChanged(SelectedClassModel model);
}

