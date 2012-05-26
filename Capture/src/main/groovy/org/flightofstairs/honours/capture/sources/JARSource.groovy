package org.flightofstairs.honours.capture.sources

import jlibs.core.lang.JavaProcessBuilder
import org.flightofstairs.honours.capture.agent.Agent
import org.flightofstairs.honours.capture.recorder.RMIRecorder
import org.gcontracts.annotations.Requires
import org.slf4j.LoggerFactory

import java.util.jar.JarInputStream

public class JARSource implements Source {

	public final File jarFile

	private final List<String> packages

	private final List<String> programArgs
	private final List<String> jvmArgs

	@Requires({ jarFile != null && packages != null && programArgs != null && jvmArgs != null })
	public JARSource(final File jarFile, final List<String> packages,
	                 final List<String> programArgs, final List<String> jvmArgs) {

		this.jarFile = jarFile

		this.packages = new LinkedList<String>(packages)

		this.programArgs = new LinkedList<String>(programArgs)
		this.jvmArgs = new LinkedList<String>(jvmArgs)
	}

	@Override public String getName() { return jarFile.getName(); }

	@Override
	void startSource(int port) {
		String main = getMainClass()

		JavaProcessBuilder jpb = new JavaProcessBuilder()

		// Mirror the classpath on the new jvm.
		System.getProperty("java.class.path").tokenize(File.pathSeparatorChar).each {
			if(!it.isAllWhitespace()) jpb.classpath((new File(it)).getAbsoluteFile())
		}

		// Use the aspectj weaver as a java agent.
		String weaverPath = getWeaverFile().getAbsolutePath();
		LoggerFactory.getLogger(RMIRecorder.class).info("Using [{}] for weaver.", weaverPath);
		jpb.jvmArg("-javaagent:" + weaverPath + "=orrery,CH"); // TODO fix

		jpb.jvmArg("-Dorg.flightofstairs.honours.capture.port=" + port);

		programArgs.each { if(!it.isAllWhitespace()) jpb.arg(it) }
		jvmArgs.each { if(!it.isAllWhitespace()) jpb.jvmArg(it) }

		jpb.classpath(jarFile.getAbsoluteFile())
		jpb.mainClass(main)

		LoggerFactory.getLogger(JARSource.class).info("Launching traced application: {}", jpb.command().toString())

		try {
			Process p = jpb.launch(System.out, System.err)
			p.waitFor()
		} catch (all) {
			LoggerFactory.getLogger(JARSource.class).warn("Error running traced application.", all)
		}

	}

	private String getMainClass() {
		JarInputStream jarStream = new JarInputStream(new FileInputStream(jarFile))

		try {
			return jarStream.getManifest().getMainAttributes().getValue("Main-Class")
		} catch (IOException ex) {
			LoggerFactory.getLogger(JARSource.class).error("Could not find main class in {}", jarFile, ex)
			throw ex
		} finally {
			jarStream.close()
		}
	}

	private File getWeaverFile() {
		String weaverPath = Agent.class.getProtectionDomain().getCodeSource().getLocation().getPath()

		File weaverFile = new File(weaverPath)

		if(! weaverFile.exists())
			throw new FileNotFoundException("Can't find AspectJ weaver on cp.");

		return weaverFile
	}
}
