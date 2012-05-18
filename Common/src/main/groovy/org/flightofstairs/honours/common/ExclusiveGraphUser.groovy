package org.flightofstairs.honours.common

import org.gcontracts.annotations.Requires

public interface ExclusiveGraphUser {
	@Requires({ callGraph != null })
	public void run(CallGraph callGraph);
}

