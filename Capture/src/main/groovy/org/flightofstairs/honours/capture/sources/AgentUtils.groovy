package org.flightofstairs.honours.capture.sources

import org.flightofstairs.honours.capture.agent.Agent

class AgentUtils {
	public static File getWeaverFile() {
		String weaverPath = Agent.class.getProtectionDomain().getCodeSource().getLocation().getPath()

		File weaverFile = new File(weaverPath)

		if(! weaverFile.exists())
			throw new FileNotFoundException("Can't find AspectJ weaver on cp.")

		return weaverFile.getAbsoluteFile()
	}
}
