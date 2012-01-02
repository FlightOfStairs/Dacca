package org.flightofstairs.honours.capture.agent;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteTracer extends Remote {
	boolean existanceTest() throws RemoteException;
}
