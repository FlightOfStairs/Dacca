package org.flightofstairs.honours.capture.agent;

import org.flightofstairs.honours.common.Call;
import org.objectweb.asm.Type;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class InternalCall {

	public final String caller;
	public final String calleeRef;
	public final String methodName;
	public final String desc;

	public InternalCall(final String caller, final String calleeRef, final String methodName, final String desc) {
		this.caller = caller;
		this.calleeRef = calleeRef;
		this.methodName = methodName;
		this.desc = desc;
	}

	public Call getCall() {
		Call call =  new Call(
				sanitize(Type.getObjectType(caller).getClassName()),
				sanitize(Type.getObjectType(calleeRef).getClassName()),
				methodName
			);

		return call.getCallee() == null || call.getCaller() == null ? null : call;
	}

	public Call getCallOnClass(Class instanceClass) throws ClassNotFoundException, NoSuchMethodException {

		String calleeName = null;

		try {
			calleeName = getImplementingClass(instanceClass, methodName, desc).getCanonicalName();
		} catch (ClassNotFoundException e) {
			LoggerFactory.getLogger(getClass()).warn("Couldn't find method on instance class " + instanceClass.toString() + " " + methodName + desc, e);
		}

		Call call = new Call(
				sanitize(Type.getObjectType(caller).getClassName()),
				sanitize(calleeName),
				methodName
		);

		return call.getCallee() == null || call.getCaller() == null ? null : call;
	}

	private static Class getImplementingClass(final Class instance, final String methodName, final String desc) throws ClassNotFoundException {
		Class[] arguments = getArgumentClasses(desc);

		Class declaringClass = instance;

		while(! declaringClass.getSuperclass().equals(Object.class)) {
			Method[] methods = declaringClass.getDeclaredMethods();

			for(Method method : methods) {
				if(method.getName().equals(methodName) && Arrays.deepEquals(arguments, method.getParameterTypes())) return declaringClass;
			}

			if(Modifier.isAbstract(declaringClass.getSuperclass().getModifiers())) return declaringClass;

			declaringClass = declaringClass.getSuperclass();
		}

		return declaringClass;
	}

	private static Class[] getArgumentClasses(final String desc) throws ClassNotFoundException {
		final Type[] argumentTypes = Type.getArgumentTypes(desc);

		final int count = argumentTypes.length; // Tiny performance gain, but may be helpful given use of InternalCall.

		final Class[] argumentClasses = new Class[count];

		for(int i = 0; i < count; i++)
			argumentClasses[i] = getClassFromName(argumentTypes[i].getClassName());

		return argumentClasses;
	}

	private static Class getClassFromName(final String className) throws ClassNotFoundException {
		if(className.equals("boolean")) return boolean.class;
		if(className.equals("byte")) return byte.class;
		if(className.equals("char")) return char.class;
		if(className.equals("double")) return double.class;
		if(className.equals("float")) return float.class;
		if(className.equals("int")) return int.class;
		if(className.equals("long")) return long.class;
		if(className.equals("short")) return short.class;
		if(className.equals("void")) return void.class;

		return Class.forName(className);
	}

	private static String sanitize(String name) {
		if(name == null) return null;

		if(name.contains("$"))
			name = name.substring(0, name.indexOf('$'));

		if(name.endsWith("[]")) return null;

		return name;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;

		final InternalCall other = (InternalCall) obj;

		if (!calleeRef.equals(other.calleeRef)) return false;
		if (!caller.equals(other.caller)) return false;
		if (!desc.equals(other.desc)) return false;
		if (!methodName.equals(other.methodName)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = caller.hashCode();
		result = 31 * result + calleeRef.hashCode();
		result = 31 * result + methodName.hashCode();
		result = 31 * result + desc.hashCode();
		return result;
	}
}
