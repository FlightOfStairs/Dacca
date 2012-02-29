 package org.flightofstairs.honours.capture.recorder;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import org.flightofstairs.honours.common.Call;

public interface RemoteRecorder extends Remote {
	public void addCalls(List<Call> calls) throws RemoteException;
	public void end() throws RemoteException;
}
