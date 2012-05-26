package org.flightofstairs.honours.capture.agent

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor

class ClassWeaver extends ClassWriter {
	private String className

	ClassWeaver(int i) {
		super(i)
	}

	void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces)

		this.className = name
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		return new MethodWeaver(getClassName(), className, access, name, desc, signature, exceptions)
	}
}
