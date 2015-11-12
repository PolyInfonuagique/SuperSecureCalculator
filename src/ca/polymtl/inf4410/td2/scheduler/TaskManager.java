package ca.polymtl.inf4410.td2.scheduler;

import ca.polymtl.inf4410.td2.shared.model.ITask;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ConcurrentLinkedQueue;


public class TaskManager extends Observable{

    private ConcurrentLinkedQueue<ITask> queue = new ConcurrentLinkedQueue<>();

    public void addTask(String filename){
        queue.addAll(TaskReader.getTaskList(filename));

        this.setChanged();
        this.notifyObservers();
    }

    public synchronized List<ITask> getTask(int nb){
        int i = 0;
        ArrayList<ITask> returnTask = new ArrayList<>();

        while(i<nb && !queue.isEmpty()){
             returnTask.add(queue.poll());
        }

        return returnTask;
    }



}
