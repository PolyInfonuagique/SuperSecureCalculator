package ca.polymtl.inf4410.td2.scheduler;

import ca.polymtl.inf4410.td2.shared.model.ITask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PartialResult class
 *
 * Elle associe à une liste de tâche les résultats obtenus de chaque server (identifié par l'id du thread)
 */
public class PartialResult {

    /**
     * Liste des résultats pour chaque server (i.e. thread)
     */
    private ConcurrentHashMap<Long,Integer> threadResult = new ConcurrentHashMap<>();

    /**
     * Nombre d'occurence de chaque résultat
     */
    private ConcurrentHashMap<Integer,Integer> occurResult = new ConcurrentHashMap<>();

    /**
     * Liste de tâches
     */
    private final List<ITask> tasks;

    /**
     * Constructeur
     * @param tasks liste des tâches
     */
    public PartialResult(final List<ITask> tasks) {
        this.tasks = tasks;
    }

    /**
     * @return liste des tâches
     */
    public List<ITask> getTasks() {
        return tasks;
    }

    /**
     * Insert un nouveau résultat avec le Thread correspondant
     * @param result int
     */
    public void setResult(final int result) {
        this.threadResult.put(Thread.currentThread().getId(), result);
        this.occurResult.put(result,this.occurResult.getOrDefault(result,0)+1);
    }

    /**
     * Retourne le résultat final par rapport aux résultats obtenus des différents thread
     *
     * Un résultat doit être calculé par tous les threads actif et être obtenu par au moins 60%
     * des threads pour être retourné
     *
     * S'il y a désacord, le résultat ayant le moins d'occurence est supprimé
     *
     * @param nbThreadAlive nombre de thread actif
     * @return int
     * @throws Exception Si le résultat final ne peut pas être encore obtenu
     */
    public synchronized int getResult(final int nbThreadAlive) throws Exception {
        // All thread had processed
        if(threadResult.size() >= nbThreadAlive){

            Optional<Map.Entry<Integer, Integer>> maxValue = occurResult.entrySet().stream().max((o1, o2) -> o1.getValue().compareTo(o2.getValue()));

            if(maxValue.get().getValue() >= Math.ceil(nbThreadAlive * 0.6)){
                return maxValue.get().getKey();
            }
            else{
                // No agree between threads, delete min value
                Optional<Map.Entry<Integer, Integer>> minValue = occurResult.entrySet().stream().min((o1, o2) -> o1.getValue().compareTo(o2.getValue()));

                threadResult.forEach((threadId, res) -> {
                    if(res.equals(minValue.get().getKey())){
                        threadResult.remove(threadId);
                        occurResult.remove(minValue.get().getKey());
                    }
                });
            }
        }
        throw new Exception("No result");
    }

    /**
     * Vrai si le thread doit exécuter les tâches (i.e. il n'a pas encore donné de résultat)
     *
     * @param threadId id du thread
     * @return boolean
     */
    public boolean toProcess(long threadId) {
        return !threadResult.containsKey(threadId);
    }
}
