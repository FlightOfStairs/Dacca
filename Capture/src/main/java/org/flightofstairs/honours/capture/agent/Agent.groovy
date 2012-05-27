package org.flightofstairs.honours.capture.agent

import org.slf4j.LoggerFactory

import java.lang.instrument.Instrumentation

class Agent {

	public static void premain(final String options, final Instrumentation inst) {
		inst.addTransformer(new ClassWeaver(inst, options.replaceAll("\\.", "/").split(",") as List<String>), true)

		LoggerFactory.getLogger(Agent.class).debug "Dacca agent started."
	}

	public static void agentmain(final String options, final Instrumentation inst) {
		inst.addTransformer(new ClassWeaver(inst, options.replaceAll("\\.", "/").split(",") as List<String>), true)

		LoggerFactory.getLogger(Agent.class).debug "Dacca agent attached and started."
	}
}
