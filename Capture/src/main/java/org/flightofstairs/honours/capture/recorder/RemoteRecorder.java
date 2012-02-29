 package org.flightofstairs.honours.capture.recorder;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import org.flightofstairs.honours.common.Call;

public interface RemoteRecorder extends Remote {
	public void end() throws RemoteException;

	public void addCallCounts(Map<Call, Integer> callCounts) throws RemoteException;
}
