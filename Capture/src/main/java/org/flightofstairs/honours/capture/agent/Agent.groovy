package org.flightofstairs.honours.capture.agent

import org.slf4j.LoggerFactory

import java.lang.instrument.Instrumentation

class Agent {

	public static void premain(final String options, final Instrumentation inst) {
		inst.addTransformer(new ClassWeaver(inst, options.replaceAll("\\.", "/").tokenize(",") as List<String>), true)

		LoggerFactory.getLogger(Agent.class).debug "Dacca agent started."
	}

	public static void agentmain(final String options, final Instrumentation inst) {
		final String port = options.tokenize(',')[0]
		System.setProperty("org.flightofstairs.honours.capture.port", port)

		LoggerFactory.getLogger(Agent.class).debug("Setting port: {}", port)

		inst.addTransformer(new ClassWeaver(inst, options.replaceAll("\\.", "/").tokenize(",").drop(1) as List<String>), true)

		LoggerFactory.getLogger(Agent.class).debug "Dacca agent attached and started."
	}
}
