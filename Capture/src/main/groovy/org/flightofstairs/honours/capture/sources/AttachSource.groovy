package org.flightofstairs.honours.capture.sources

import com.sun.tools.attach.VirtualMachine
import com.sun.tools.attach.VirtualMachineDescriptor

public class AttachSource implements Source {

	public static List<VirtualMachineDescriptor> getVMList() { return VirtualMachine.list() }
	public String getName() { return descriptor.displayName() }

	//VirtualMachineDescriptor.java suggests that this is effectively immutable
	public final VirtualMachineDescriptor descriptor

	public final List<String> packages

	public AttachSource(VirtualMachineDescriptor descriptor, List<String> packages) {
		this.descriptor = descriptor

		this.packages = Collections.unmodifiableList(new LinkedList<String>(packages))
	}

	@Override
	void startSource(int port) {
		File weaverFile = AgentUtils.getWeaverFile()

		VirtualMachine vm = VirtualMachine.attach(descriptor)

		String options = port + "," + packages.join(",")

		vm.loadAgent(weaverFile.getPath(), options)
	}
}
