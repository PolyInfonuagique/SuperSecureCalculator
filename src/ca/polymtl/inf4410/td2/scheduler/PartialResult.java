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
        System.out.println(Thread.currentThread() + " GetResult - nb result : "+threadResult.size()+"/"+nbThreadAlive);

        if(threadResult.size() >= nbThreadAlive){

            System.out.println(Thread.currentThread() + "\t\t\t Thread result : ");
            threadResult.forEach((aLong, integer) -> System.out.println(Thread.currentThread() + "\t\t\t\t\t\t ID:" + aLong + "-" + integer));

            System.out.println(Thread.currentThread() + "\t\t\t Occur result : ");
            occurResult.forEach((aLong, integer) -> System.out.println(Thread.currentThread() + "\t\t\t\t\t\t ID:" + aLong + "-" + integer));

            Optional<Map.Entry<Integer, Integer>> maxValue = occurResult.entrySet().stream().max((o1, o2) -> o1.getValue().compareTo(o2.getValue()));
            System.out.println("Nb thread vivant : "+nbThreadAlive+", nb occurence requis : "+Math.ceil(nbThreadAlive*0.6));
            if(maxValue.get().getValue() >= Math.ceil(nbThreadAlive*0.6)){
                System.out.println(Thread.currentThread() + " -> Valeur retenu : "+maxValue.get().getKey()+" ("+maxValue.get().getValue()+" occurences)");
                return maxValue.get().getKey();
            }
            else{
                Optional<Map.Entry<Integer, Integer>> minValue = occurResult.entrySet().stream().min((o1, o2) -> o1.getValue().compareTo(o2.getValue()));
                System.out.println(Thread.currentThread() + " -> Valeur supprimée : "+minValue.get().getKey()+" ("+minValue.get().getValue()+" occurences)");

                threadResult.forEach((threadId, res) -> {
                    if(res.equals(minValue.get().getKey())){
                        threadResult.remove(threadId);
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
