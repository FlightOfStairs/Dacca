package org.flightofstairs.honours.capture.agent

import org.objectweb.asm.ClassAdapter
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.util.CheckClassAdapter
import org.slf4j.LoggerFactory

import java.lang.instrument.ClassFileTransformer
import java.lang.instrument.Instrumentation
import java.security.ProtectionDomain

public class ClassWeaver implements ClassFileTransformer {

	private final Instrumentation instrumentation
	private final List<String> packages

	public ClassWeaver(final Instrumentation instrumentation, final List<String> packages) {
		this.instrumentation = instrumentation
		this.packages = Collections.unmodifiableList(new LinkedList<String>(packages))
	}

	public final byte[] transform(final ClassLoader ldr, final String className, final Class clazz, final ProtectionDomain domain, final byte[] bytes) {
		try {
			if(! packages.any { className.startsWith(it) }) return null

			final ClassReader cr = new ClassReader(bytes);
			final ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);

			cr.accept(new ClassAdapter(new ClassAdapter(cw)) {

				public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

					MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)

					if (mv == null || (access & (Opcodes.ACC_NATIVE & Opcodes.ACC_ABSTRACT)) > 0)
						return mv;

					return new MethodWeaver(cr.getClassName(), access, name, desc, signature, exceptions, mv)
				}
			}, ClassReader.EXPAND_FRAMES);

			PrintWriter pw = new PrintWriter(System.out);
			CheckClassAdapter.verify(new ClassReader(cw.toByteArray()), false, pw)

			return cw.toByteArray()

		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).warn("Error in agent.", e)
			return null
		}
	}

}
