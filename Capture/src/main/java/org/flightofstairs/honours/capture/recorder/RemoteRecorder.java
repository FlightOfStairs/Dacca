 package org.flightofstairs.honours.capture.recorder;

import java.rmi.Remote;
import java.rmi.RemoteException;
<<<<<<< HEAD
=======
import java.util.List;
>>>>>>> 9938a74b70ab4fcaaee96429975f8b5db4c52d4e
import java.util.Map;
import org.flightofstairs.honours.common.Call;

public interface RemoteRecorder extends Remote {
	public void end() throws RemoteException;

	public void addCallCounts(Map<Call, Integer> callCounts) throws RemoteException;
}
