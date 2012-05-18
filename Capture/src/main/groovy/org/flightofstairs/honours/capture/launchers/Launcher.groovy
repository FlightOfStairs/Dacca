package org.flightofstairs.honours.capture.launchers

import org.gcontracts.annotations.Requires

interface Launcher {

	@Requires({ launchConfig != null })
	public void launch(LaunchConfiguration launchConfig);
}

