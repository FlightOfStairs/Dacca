package org.flightofstairs.honours.capture.recorder;

import org.flightofstairs.honours.capture.Call;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteRecorder extends Remote {
	public void addCalls(List<Call> calls) throws RemoteException;
	public void end() throws RemoteException;
}
