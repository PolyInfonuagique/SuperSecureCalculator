package ca.polymtl.inf4410.td2.scheduler;

import ca.polymtl.inf4410.td2.shared.model.ITask;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentLinkedQueue;


public class TaskManager extends Observable{

    private ConcurrentLinkedQueue<ITask> queueToDo = new ConcurrentLinkedQueue<>();
    private ArrayList<PartialResult> listToValidate = new ArrayList<>();
    private int nbTaskToExecute;
    private int result = 0;
    private final Object MUTEX_RESULT = new Object();
    private int nbThreadAlive = 0;
    private boolean secureMode = false;

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

    public synchronized PartialResult getTask(int nb){
        int i = 0;
        ArrayList<ITask> returnTask = new ArrayList<>();

        while(i<nb && !queueToDo.isEmpty()){
             returnTask.add(queueToDo.poll());
            i++;
        }

        if(returnTask.size() > 0){
            return new PartialResult(returnTask);
        }
        else if(secureMode){
            for(PartialResult r : listToValidate){
                if(r.toProcess(Thread.currentThread().getId())){
                    return r;
                }
            }
        }
        return null;
    }

    public int getResult(){
        synchronized (MUTEX_RESULT){
            return result;
        }
    }

    public boolean isFinish(){
        synchronized (MUTEX_RESULT){
            return nbTaskToExecute == 0 && listToValidate.isEmpty();
        }
    }

    public void updateResult(PartialResult partialResult){
        synchronized (MUTEX_RESULT){
            if(!listToValidate.contains(partialResult)){
                nbTaskToExecute -= partialResult.getTasks().size();

                if(secureMode){
                    listToValidate.add(partialResult);
                }
                else{
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

    public void addTask(PartialResult partialResult){
        synchronized (MUTEX_RESULT){
            if(!listToValidate.contains(partialResult)){
                this.queueToDo.addAll(partialResult.getTasks());
            }
        }

        this.setChanged();
        this.notifyObservers();
    }

    public boolean isEmpty(){
        boolean isEmpty = queueToDo.isEmpty();
        for(PartialResult r : listToValidate){
            isEmpty &= !r.toProcess(Thread.currentThread().getId());
        }
        return isEmpty;
    }

    public synchronized void revalidate() {
        synchronized (MUTEX_RESULT) {
            for (PartialResult r : this.listToValidate) {
                updateResult(r);
            }
        }
    }
}
