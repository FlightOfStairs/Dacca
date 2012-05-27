package org.flightofstairs.honours.capture.agent

import org.flightofstairs.honours.common.Call
import org.objectweb.asm.Frame
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
import org.objectweb.asm.tree.analysis.SourceInterpreter
import org.objectweb.asm.tree.analysis.SourceValue
import org.slf4j.LoggerFactory

class MethodWeaver extends MethodNode implements Opcodes {

	private final String owner
	private final MethodVisitor mv

	private final ArrayList<MethodInsnNode> calls = new ArrayList<MethodInsnNode>();

	MethodWeaver(String owner, int access, String name, String desc,
	                                                          String signature, String[] exceptions, MethodVisitor mv) {
		super(access, name, desc, signature, exceptions);
		this.owner = owner;
		this.mv = mv;
	}

	@Override
	void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {

		if(opcode == INVOKEVIRTUAL) {
			int probeID = Tracer.INSTANCE.probes.createProbeIDAt(new Call(owner, name, desc))

			super.visitFieldInsn(GETSTATIC, "org/flightofstairs/honours/capture/agent/Tracer", "INSTANCE", "Lorg/flightofstairs/honours/capture/agent/Tracer;")
			super.visitLdcInsn(probeID)
			super.visitMethodInsn(INVOKEVIRTUAL, "org/flightofstairs/honours/capture/agent/Tracer", "probe", "(I)V")
		}

		super.visitMethodInsn(opcode, owner, name, desc);

		if(opcode==INVOKEINTERFACE) {
			calls.add((MethodInsnNode) instructions.getLast());
		}


	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		super.visitMaxs(maxStack + 5 * calls.size(), maxLocals)
	}

	@Override
	public void visitEnd() {
		if(!calls.isEmpty()) {
			try {
				List<AbstractInsnNode> objectRefLoadPoints = []

				Analyzer analyzer = new Analyzer(new SourceInterpreter())
				Frame[] frames = analyzer.analyze(owner, this) as Frame[]

				for (MethodInsnNode methodInsnNode : calls) {
					Frame frame = frames[instructions.indexOf(methodInsnNode)]

					if(frame == null) continue

					int stackSlot = frame.getStackSize() - 1

					for(Type type : Type.getArgumentTypes(methodInsnNode.desc)) {
						stackSlot -= type.getSize()
					}

					SourceValue stackValue = (SourceValue) frame.getStack(stackSlot)
					objectRefLoadPoints.addAll stackValue.insns
				}

				for(AbstractInsnNode node : objectRefLoadPoints) {
					instructions.insert(node, traceInstructions(owner, "lol"))
				}
			} catch (AnalyzerException ex) {
				LoggerFactory.getLogger(getClass()).warn("Error instrumenting method: {} {}", owner, name, ex)
			}
		}

		accept(mv)

		LoggerFactory.getLogger(getClass()).trace("Instrumented method: {} {}", owner, name)
	}

	private static InsnList traceInstructions(String owner, String method) {
		InsnList list = new InsnList();

		list.add(new InsnNode(Opcodes.DUP))
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;"))
		list.add(new FieldInsnNode(Opcodes.GETSTATIC, "org/flightofstairs/honours/capture/agent/Tracer", "INSTANCE", "Lorg/flightofstairs/honours/capture/agent/Tracer;"))
		list.add(new InsnNode(Opcodes.SWAP))
		list.add(new LdcInsnNode(owner))
		list.add(new LdcInsnNode(method))
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "org/flightofstairs/honours/capture/agent/Tracer", "probe", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;)V"))

		return list
	}
}
