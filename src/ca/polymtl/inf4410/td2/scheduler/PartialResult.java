package ca.polymtl.inf4410.td2.scheduler;

import ca.polymtl.inf4410.td2.shared.model.ITask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by david on 17-11-15.
 */
public class PartialResult {

    private ConcurrentHashMap<Long,Integer> threadResult = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer,Integer> occurResult = new ConcurrentHashMap<>();
    private final List<ITask> tasks;

    public PartialResult(final List<ITask> tasks) {
        this.tasks = tasks;
    }

    public List<ITask> getTasks() {
        return tasks;
    }

    public void setResult(final int result) {
        this.threadResult.put(Thread.currentThread().getId(), result);
        this.occurResult.put(result,this.occurResult.getOrDefault(result,0)+1);
    }

    public synchronized int getResult(final int nbThreadAlive) throws Exception {

        if(threadResult.size() >= nbThreadAlive){

            Optional<Map.Entry<Integer, Integer>> maxValue = occurResult.entrySet().stream().max((o1, o2) -> o1.getValue().compareTo(o2.getValue()));
            if(maxValue.get().getValue() >= Math.ceil(nbThreadAlive * 0.6)){
                return maxValue.get().getKey();
            }
            else{
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

    public boolean toProcess(long threadId) {
        return !threadResult.containsKey(threadId);
    }
}
