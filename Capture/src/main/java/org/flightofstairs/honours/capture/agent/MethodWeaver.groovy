package org.flightofstairs.honours.capture.agent

import org.flightofstairs.honours.common.Call
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.analysis.Analyzer
import org.objectweb.asm.tree.analysis.Frame
import org.objectweb.asm.tree.analysis.AnalyzerException
import org.objectweb.asm.tree.analysis.SourceInterpreter
import org.objectweb.asm.tree.analysis.SourceValue
import org.objectweb.asm.tree.*

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

		Integer probeID = Tracer.INSTANCE.probes.createProbeIDAt(new Call(owner, name, desc))

		super.visitMethodInsn(opcode, owner, name, desc);

		if(opcode==INVOKEVIRTUAL || opcode==INVOKEINTERFACE) {
			calls.add((MethodInsnNode) instructions.getLast());
		}

		//super.visitFieldInsn(Opcodes.GETSTATIC, "org/flightofstairs/honours/capture/agent/Tracer", "INSTANCE", "Lorg/flightofstairs/honours/capture/agent/Tracer;");

		//super.visitLdcInsn(probeID)

		//super.visitMethodInsn(INVOKEVIRTUAL, "org/flightofstairs/honours/capture/agent/Tracer", "probe", "(Ljava/lang/Integer;)V")
	}

	public void visitEnd() {
		if(!calls.isEmpty()) {
			try {
				Analyzer analyzer = new Analyzer(new SourceInterpreter());
				Frame[] frames = analyzer.analyze(owner, this) as Frame[];

				for (MethodInsnNode methodInsnNode : calls) {
					Frame frame = frames[instructions.indexOf(methodInsnNode)];

					if(frame == null) continue

					int stackSlot = frame.getStackSize()

					for(Type type : Type.getArgumentTypes(methodInsnNode.desc)) {
						stackSlot -= type.getSize()
					}

					SourceValue stackValue = (SourceValue) frame.getStack(stackSlot - 1);
					Set<AbstractInsnNode> insns = stackValue.insns

					for(AbstractInsnNode node : insns) {
						println node
					}
				}
			} catch (AnalyzerException ex) {
			}
		}
		accept(mv);
	}
}
