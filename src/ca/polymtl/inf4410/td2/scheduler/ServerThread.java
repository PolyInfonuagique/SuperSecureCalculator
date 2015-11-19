package ca.polymtl.inf4410.td2.scheduler;


import ca.polymtl.inf4410.td2.shared.ServerInterface;
import ca.polymtl.inf4410.td2.shared.model.ITask;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * ServerThread class
 *
 * S'occupe de la communication avec le server en utilisant JavaRMI
 */
public class ServerThread extends Thread implements Observer {

    /**
     * Incrément du Qi entre chaque tâche réussite
     */
    private static final int DELTA_ADD_TASK = 5;

    /**
     * Nombre d'échec avant de fermer la communication avec le server
     */
    private static final int MAX_FAILURE = 10;

    /**
     * Qi approximatif du serveur distant
     */
    private int nbTaskSended = 20;


    /**
     * Server distant
     */
    private ServerInterface server = null;

    /**
     * TaskManager
     */
    private TaskManager taskManager = null;

    /**
     * Pattern Wait/Notify
     */
    private final Object LOCK = new Object();

    /**
     * Constructeur
     * @param taskManager référence sur le task manager
     * @param ipAddress adresse ip du serveur correspondant
     * @throws Exception en cas d"échec de connexion
     */
    public ServerThread(TaskManager taskManager, String ipAddress) throws Exception {
        this.taskManager = taskManager;
        taskManager.addObserver(this);

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            Registry registry = LocateRegistry.getRegistry(ipAddress,5002);
            server = (ServerInterface) registry.lookup("Server");
        } catch (RemoteException | NotBoundException e) {
            throw new Exception("Echec de la connexion avec le serveur", e);
        }
    }

    @Override
    public void run() {
        super.run();

        PartialResult toSend;
        int nbSended, nbFailure = 0;
        int indexFisrt, indexLast, res;

        while(!taskManager.isFinish() && nbFailure < MAX_FAILURE){

            // Read x task
            toSend = taskManager.getTask(nbTaskSended);

            if(toSend != null){

                nbSended = toSend.getTasks().size();
                List<ITask> listToSend = toSend.getTasks();

                // Send back to server
                try {
                    indexFisrt = 0;
                    res = 0;

                    do { // Split huge List when nbSended > nbTaskSended
                        indexLast = indexFisrt + Math.min(nbSended-indexFisrt, nbTaskSended);

                        res = (res + server.work(new HashSet<>(listToSend.subList(indexFisrt, indexLast)))) % 5000;
                        indexFisrt = indexLast;
                    }while(indexLast != nbSended);

                    toSend.setResult(res);
                    taskManager.updateResult(toSend);

                    if(nbSended == nbTaskSended ){
                        nbTaskSended += DELTA_ADD_TASK;
                    }

                    nbFailure = 0;
                } catch (Exception e) { // Server fail
                    taskManager.addTask(toSend);
                    nbTaskSended = nbSended - Math.round(nbSended/2);
                    nbFailure ++;
                }
            }

            // Wait until another task available
            if(taskManager.isEmpty() && !taskManager.isFinish()){

                synchronized (LOCK){
                    try {
                        LOCK.wait(500);
                    } catch (InterruptedException ignored) {}
                }
            }
        }

        // Delete observe when task it's over
        taskManager.deleteObserver(this);
    }


    @Override
    public void update(Observable o, Object arg) {
        synchronized (LOCK){
            LOCK.notify(); // WAKE UP !!!!
        }
    }

}
