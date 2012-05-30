package org.flightofstairs.honours.capture.agent

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.analysis.Analyzer
import org.objectweb.asm.tree.analysis.AnalyzerException
import org.objectweb.asm.tree.analysis.Frame
import org.objectweb.asm.tree.analysis.SourceInterpreter
import org.objectweb.asm.tree.analysis.SourceValue
import org.slf4j.LoggerFactory

class MethodWeaver extends MethodNode implements Opcodes {

	private final String className
	private final MethodVisitor mv

	private final ArrayList<MethodInsnNode> calls = new ArrayList<MethodInsnNode>();

	MethodWeaver(String className, int access, String methodName, String desc, String signature, String[] exceptions, MethodVisitor mv) {
		super(access, methodName, desc, signature, exceptions);
		this.className = className;
		this.mv = mv;
	}

	@Override
	void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {

		if((opcode == INVOKESPECIAL || opcode == INVOKESTATIC) && ! name.equals("<init>")) {
			int probeID = Tracer.INSTANCE.probes.createProbeIDAt(new InternalCall(className, owner, name, desc))

			super.visitFieldInsn(GETSTATIC, "org/flightofstairs/honours/capture/agent/Tracer", "INSTANCE", "Lorg/flightofstairs/honours/capture/agent/Tracer;")
			super.visitLdcInsn(probeID)
			super.visitMethodInsn(INVOKEVIRTUAL, "org/flightofstairs/honours/capture/agent/Tracer", "probe", "(I)V")
		}

		super.visitMethodInsn(opcode, owner, name, desc);

		if(opcode==INVOKEINTERFACE || opcode == INVOKEVIRTUAL)
			calls.add((MethodInsnNode) instructions.getLast());
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		super.visitMaxs(maxStack + 6 * calls.size(), maxLocals)
	}

	@Override
	public void visitEnd() {
		if(!calls.isEmpty()) {
			try {
				Map<AbstractInsnNode, MethodInsnNode> objectRefLoadPoints = [:]

				Analyzer analyzer = new Analyzer(new SourceInterpreter())
				Frame[] frames = analyzer.analyze(className, this) as Frame[]

				for (MethodInsnNode methodInsnNode : calls) {
					Frame frame = frames[instructions.indexOf(methodInsnNode)]

					if(frame == null) continue

					int stackSlot = frame.getStackSize() - 1

					for(Type type : Type.getArgumentTypes(methodInsnNode.desc)) {
						stackSlot -= type.getSize()
					}

					SourceValue stackValue = (SourceValue) frame.getStack(stackSlot)
					stackValue.insns.each {
						objectRefLoadPoints[it] = methodInsnNode
					}
				}

				objectRefLoadPoints.each { objectRefSource, callPoint ->
					instructions.insert(objectRefSource, getFullProbe(callPoint))
				}
			} catch (AnalyzerException ex) {
				LoggerFactory.getLogger(getClass()).warn("Error instrumenting method: {} {}", className, name, ex)
			}
		}

		accept(mv)

		LoggerFactory.getLogger(getClass()).trace("Instrumented method: {} {} {}", className, name)
	}

	private InsnList getFullProbe(MethodInsnNode callPoint) {
		int probeID = Tracer.INSTANCE.probes.createProbeIDAt(new InternalCall(className, callPoint.owner, callPoint.name, callPoint.desc))

		InsnList list = new InsnList();

		list.add(new InsnNode(Opcodes.DUP))
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;"))
		list.add(new FieldInsnNode(Opcodes.GETSTATIC, "org/flightofstairs/honours/capture/agent/Tracer", "INSTANCE", "Lorg/flightofstairs/honours/capture/agent/Tracer;"))
		list.add(new InsnNode(Opcodes.SWAP))
		list.add(new LdcInsnNode(probeID))
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "org/flightofstairs/honours/capture/agent/Tracer", "probe", "(Ljava/lang/Class;I)V"))

		return list
	}
}
