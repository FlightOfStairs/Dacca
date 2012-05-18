package org.flightofstairs.honours.common

import org.gcontracts.annotations.Ensures

public interface CallGraphListener {

	@Ensures({ callGraph != null })
	public void callGraphChange(CallGraph callGraph);
}

