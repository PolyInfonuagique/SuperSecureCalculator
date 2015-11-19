package ca.polymtl.inf4410.td2.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import ca.polymtl.inf4410.td2.shared.model.ITask;

/**
 * ServerInterface
 *
 */
public interface ServerInterface extends Remote {

	/**
	 * Calcule le resultat des tâches (attention peut être malicieux)
	 *
	 * @param tasks tâche à exécuter
	 * @return resultat
	 * @throws RemoteException en cas de surcharge du serveur
	 */
	int work(Set<ITask> tasks) throws RemoteException;

}
  