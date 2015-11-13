package ca.polymtl.inf4410.td2.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import ca.polymtl.inf4410.td2.shared.model.ITask;

public interface ServerInterface extends Remote {
	
	public int work(Set<ITask> tasks) throws RemoteException;

}
  