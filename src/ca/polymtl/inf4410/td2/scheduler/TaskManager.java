package ca.polymtl.inf4410.td2.scheduler;

import ca.polymtl.inf4410.td2.shared.model.ITask;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * TaskManager class
 *
 * S'occupe de gérer l'ensemble des taches à exécuter entre les serveurs
 */
public class TaskManager extends Observable{

    /**
     * File des tâches exécuter pour la première fois
     */
    private ConcurrentLinkedQueue<ITask> queueToDo = new ConcurrentLinkedQueue<>();

    /**
     * Liste des résultats partiels à valider par les autres serveurs
     */
    private ArrayList<PartialResult> listToValidate = new ArrayList<>();

    /**
     * Nombre de tache restante à exécuter
     */
    private int nbTaskToExecute;

    /**
     * Résultat final du fichier
     */
    private int result = 0;

    /**
     * Mutex sur le résultat
     */
    private final Object MUTEX_RESULT = new Object();

    /**
     * Nombre de thread vivant
     */
    private int nbThreadAlive = 0;

    /**
     * Secure mode :
     * - false : on fait confiance au résultat des serveurs
     * - true : on exécute les mêmes taches sur l'ensemble des serveurs et on choisi le + fréquent
     */
    private boolean secureMode = false;

    /**
     * Constructor
     * @param filename nom du fichier contenant les tâches
     * @param secureMode mode securisé ou non
     */
    public TaskManager(String filename, boolean secureMode) {
        addTask(filename);
        this.secureMode = secureMode;
    }

    private void addTask(String filename){
        queueToDo.addAll(TaskReader.getTaskList(filename));
        nbTaskToExecute = queueToDo.size();
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
        if(o instanceof ServerThread){
            nbThreadAlive ++;
        }
    }

    @Override
    public synchronized void deleteObserver(Observer o) {
        super.deleteObserver(o);
        if(o instanceof ServerThread){
            nbThreadAlive --;
        }
    }

    /**
     * Retourne un PartialResult à exécuter (soit pour la première fois, soit pour le re-valider dans le mode non sécurisé)
     * @param nb nombre de tâche souhaité (ce paramètre n'est pas contractuel)
     * @return PartialResult|null null si aucune tâche n'est disponible
     */
    public synchronized PartialResult getTask(int nb){
        int i = 0;
        ArrayList<ITask> returnTask = new ArrayList<>();

        // 1. check into the queue of new task
        while(i<nb && !queueToDo.isEmpty()){
             returnTask.add(queueToDo.poll());
            i++;
        }

        if(returnTask.size() > 0){
            return new PartialResult(returnTask);
        }
        else if(secureMode){  // If the queue is empty
            for(PartialResult r : listToValidate){ // Find a list of task to validate for current thread
                if(r.toProcess(Thread.currentThread().getId())){
                    return r;
                }
            }
        }
        return null;
    }

    /**
     * @return Retourne le resultat de l'ensemble des tâches du fichier
     */
    public int getResult(){
        synchronized (MUTEX_RESULT){
            return result;
        }
    }

    /**
     * @return vrai lorsque l'ensemble des tâches a été calculées et validées
     */
    public boolean isFinish(){
        synchronized (MUTEX_RESULT){
            return nbTaskToExecute == 0 && listToValidate.isEmpty();
        }
    }

    /**
     * Mise à jour du PartialResult avec la valeur du server
     * - s'il n'existe pas dans la liste à valider on l'ajoute
     * - s'il existe on essaye d'obtenir le résultat afind de mettre à jour le résultat global
     * @param partialResult PartialResult
     */
    public void updateResult(PartialResult partialResult){
        synchronized (MUTEX_RESULT){
            if(!listToValidate.contains(partialResult)){
                nbTaskToExecute -= partialResult.getTasks().size();

                if(secureMode){
                    listToValidate.add(partialResult); // Add to the list to validate
                }
                else{ // Else update global result
                    try{
                        int res = partialResult.getResult(1);
                        result = (res + result) % 5000;
                    } catch (Exception ignored) {
                    }
                }
            }
            else{
                try{
                    int res = partialResult.getResult(nbThreadAlive);
                    result = (res + result) % 5000;
                    listToValidate.remove(partialResult);
                } catch (Exception ignored) {
                }
            }

            this.setChanged();
            this.notifyObservers();
        }
    }

    /**
     * Rajout des tâches à la file lorsqu'un échec se produit et si ce n'est pas une tâche à revalider
     * @param partialResult
     */
    public void addTask(PartialResult partialResult){
        synchronized (MUTEX_RESULT){
            if(!listToValidate.contains(partialResult)){
                this.queueToDo.addAll(partialResult.getTasks());
            }
        }

        this.setChanged();
        this.notifyObservers();
    }

    /**
     * @return vrai si aucune tâche n'est disponible pour l'instant
     */
    public boolean isEmpty(){
        boolean isEmpty = queueToDo.isEmpty(); // Task in queue ?

        // Exist one partialResult to validate for current thread ?
        for(PartialResult r : listToValidate){
            isEmpty &= !r.toProcess(Thread.currentThread().getId());
        }
        return isEmpty;
    }
}
