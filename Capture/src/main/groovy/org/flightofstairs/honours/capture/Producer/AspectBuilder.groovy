package org.flightofstairs.honours.capture.Producer

import org.aspectj.tools.ajc.Main;

import com.google.common.io.Files;

import org.gcontracts.annotations.*

@Invariant({ code != null && code.length() != 0 })
public class AspectBuilder {
	
	private static final String CODE_FILE = "Aspect.aj";
	private static final String ASPECT_JAR = "Aspect.jar";
	
	private final String code;
	
	@Requires({ packages != null })
	public AspectBuilder(List<String> packages) {
		this.code = TEMPLATE1 + packagesPattern(packages) + TEMPLATE2;
	}
	
	@Ensures({ result.exists() })
	public File compileAspect() {
		File dir = Files.createTempDir();
		
		File ajFile = new File(dir.getAbsolutePath() + File.separatorChar + "Aspect.aj");
		
		ajFile.write(code);
		
		List fails = []; List errors = []; List warnings = []; List infos = [];
		
		String[] args = [
			ajFile.getAbsolutePath(),
			"-outxml",
			"-outjar", dir.getAbsolutePath() + File.separator + ASPECT_JAR
		].toArray();
		
		org.aspectj.tools.ajc.Main.bareMain(args, false, fails, errors, warnings, infos);
		
		if(fails.size() != 0 || errors.size() != 0) throw new RuntimeException("Problem building aspects.");
		
		return new File(dir.getAbsolutePath() + File.separatorChar + ASPECT_JAR);
	}
	
	//TODO write unit test.
	@Requires({ packages != null })
	@Ensures({ result != null })
	private static String packagesPattern(List<String> packages) {
		List<String> withins = packages.collect { "within(" + it + "..*)" }
		
		return withins.join(" || ")
	}
	
	private final static TEMPLATE1 = """
package org.flightofstairs.honours.capture.agent;

import org.flightofstairs.honours.capture.agent.Tracer;
import org.flightofstairs.honours.common.Call;

public aspect Aspect {
	
	pointcut anyCall() : call(* *.*(..)) && !within(org.flightofstairs.honours.capture..*) && ("""

	private static final TEMPLATE2 = """);
	
	after() : anyCall() {
		if(thisJoinPoint.getTarget() == null ) return;
				
		String caller = thisEnclosingJoinPointStaticPart.getSourceLocation().getWithinType().getCanonicalName();
		String callee = thisJoinPoint.getTarget().getClass().getCanonicalName();
		String method = thisJoinPoint.getSignature().getName();

		if(caller != null && callee != null) {
		
			Call c = new Call(caller, callee, method);

			Tracer.INSTANCE.traceCall(c);
		}
	}
}
"""
}

