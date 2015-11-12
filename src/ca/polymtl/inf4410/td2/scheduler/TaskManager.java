package ca.polymtl.inf4410.td2.scheduler;

import ca.polymtl.inf4410.td2.shared.model.ITask;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ConcurrentLinkedQueue;


public class TaskManager extends Observable{

    private ConcurrentLinkedQueue<ITask> queue = new ConcurrentLinkedQueue<>();
    private int nbTaskToExecute;
    private int result = 0;
    private final Object MUTEX_RESULT = new Object();

    public TaskManager(String filename) {
         addTask(filename);
    }

    private void addTask(String filename){
        queue.addAll(TaskReader.getTaskList(filename));
        nbTaskToExecute = queue.size();
    }

    public synchronized List<ITask> getTask(int nb){
        int i = 0;
        ArrayList<ITask> returnTask = new ArrayList<>();

        while(i<nb && !queue.isEmpty()){
             returnTask.add(queue.poll());
            i++;
        }

        return returnTask;
    }

    public int getResult(){
        synchronized (MUTEX_RESULT){
            return result;
        }
    }

    public boolean isFinish(){
        synchronized (MUTEX_RESULT){
            return nbTaskToExecute == 0;
        }
    }

    public void updateResult(int intermediateResult, int nbTaskHandled){
        synchronized (MUTEX_RESULT){
            result = (intermediateResult + result) % 5000;
            nbTaskToExecute -= nbTaskHandled;

            if(nbTaskToExecute == 0){
                this.setChanged();
                this.notifyObservers();
            }
        }
    }

    public void addTask(List<ITask> task){
        this.queue.addAll(task);

        this.setChanged();
        this.notifyObservers();
    }

    public boolean isEmpty(){
        return queue.isEmpty();
    }
}
