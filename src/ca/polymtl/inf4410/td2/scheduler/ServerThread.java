package ca.polymtl.inf4410.td2.scheduler;


import ca.polymtl.inf4410.td2.shared.ServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

public class ServerThread extends Thread implements Observer {

    private static final int DELTA_ADD_TASK = 2;
    private static final int MAX_FAILURE = 10;
    private int nbTaskSended = 10;

    private ServerInterface server = null;
    private TaskManager taskManager = null;

    private final Object LOCK = new Object();

    public ServerThread(TaskManager taskManager, String ipAddress) throws Exception {
        this.taskManager = taskManager;
        taskManager.addObserver(this);

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            Registry registry = LocateRegistry.getRegistry(ipAddress);
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

        while(!taskManager.isFinish() && nbFailure < MAX_FAILURE){

            // Read x task
            System.out.println(Thread.currentThread() + " Demande de "+nbTaskSended+" taches.");
            toSend = taskManager.getTask(nbTaskSended);

            if(toSend != null){
                nbSended = toSend.getTasks().size();
                System.out.println(Thread.currentThread() + " Traitement de "+nbSended+" taches.");

                // Send back to server
                try {
                    toSend.setResult(server.work(new HashSet<>(toSend.getTasks())));

                    taskManager.updateResult(toSend);
                    nbTaskSended = nbSended + DELTA_ADD_TASK;
                    nbFailure = 0;
                } catch (Exception e) {
                    taskManager.addTask(toSend);
                    nbTaskSended = nbSended - Math.round(nbSended/2);
                    nbFailure ++;
                }
            }

            if(taskManager.isEmpty() && !taskManager.isFinish()){
                synchronized (LOCK){
                    try {
                        LOCK.wait();
                    } catch (InterruptedException ignored) {}
                }
            }
        }
        System.out.println(Thread.currentThread() + " Fin.");
        taskManager.deleteObserver(this);
    }


    @Override
    public void update(Observable o, Object arg) {
        synchronized (LOCK){
            LOCK.notify();
        }
    }

}
