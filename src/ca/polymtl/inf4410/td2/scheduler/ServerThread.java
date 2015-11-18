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

public class ServerThread extends Thread implements Observer {

    private static final int DELTA_ADD_TASK = 5;
    private static final int MAX_FAILURE = 10;
    private int nbTaskSended = 20;

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
                System.out.println(Thread.currentThread() + " Demande de "+nbTaskSended+" taches.");

                nbSended = toSend.getTasks().size();
                List<ITask> listToSend = toSend.getTasks();
                // Send back to server
                try {
                    indexFisrt = 0;
                    res = 0;
                    System.out.println(Thread.currentThread() + " Traitement de "+ nbSended+" taches.");

                    do {
                        indexLast = indexFisrt + Math.min(nbSended-indexFisrt, nbTaskSended);
                        System.out.println(Thread.currentThread() + "\t "+ indexFisrt+" - "+ indexLast+"");

                        res = (res + server.work(new HashSet<>(listToSend.subList(indexFisrt, indexLast)))) % 5000;
                        indexFisrt = indexLast;
                    }while(indexLast != nbSended);

                    toSend.setResult(res);
                    taskManager.updateResult(toSend);
                    if(nbSended == nbTaskSended ){
                        nbTaskSended += DELTA_ADD_TASK;
                    }
                    nbFailure = 0;
                } catch (Exception e) {
                    taskManager.addTask(toSend);
                    nbTaskSended = nbSended - Math.round(nbSended/2);
                    nbFailure ++;
                }
            }
            if(taskManager.isEmpty() && !taskManager.isFinish()){
                if(toSend == null){
                    taskManager.revalidate();
                }

                synchronized (LOCK){
                    try {
                        LOCK.wait(500);
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
