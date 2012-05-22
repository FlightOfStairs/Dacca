 package org.flightofstairs.honours.capture.recorder;

import org.flightofstairs.honours.common.Call;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

 public interface RemoteRecorder extends Remote {
	public void addCallCounts(Map<Call, Integer> callCounts) throws RemoteException;
}
