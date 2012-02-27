package org.flightofstairs.honours.app.panels

import org.gcontracts.annotations.*

import java.util.jar.JarFile;


public class JARUtils {
	private JARUtils() {} // prevent instantiation.
	
	
	@Requires({ classes != null })
	@Ensures({ result != null })
	public static List<String> classPackages(List<String> classes) {
		def packages = [] as Set
		
		classes.each {
			def parts = it.tokenize('.')
			
			(0..<parts.size() - 2).each {
				packages << parts[0..it].join(".");
			}
		}
		
		return packages.sort { it }
	}
	
	@Requires({ file != null && file.exists() })
	@Ensures({ result != null && ! result.any { it.endsWith(".class") } })
	public static List<String> classesInJarFile(File file) {
		JarFile jarFile = new JarFile(file);
		
		def classes = [] as Set;

		jarFile.entries().each {
			def parts = it.getName().tokenize('.')
			if(parts[parts.size() - 1].equals("class")) {
				parts = it.getName().tokenize('/')		// TODO check this on windows.
				classes << parts[0..<parts.size()].join(".")
			}
		}
		
		return classes.sort { it };
	}
}

