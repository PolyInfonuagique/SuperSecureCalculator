package ca.polymtl.inf4410.td2.scheduler;


import ca.polymtl.inf4410.td2.shared.model.FibonacciTask;
import ca.polymtl.inf4410.td2.shared.model.ITask;
import ca.polymtl.inf4410.td2.shared.model.PrimeTask;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TaskReader {

    private static ITask toTask(String t){
        String[] params = t.split(" ");
        ITask task = null;

        if(params.length == 2){
            if("fib".equals(params[0])){
                task = new FibonacciTask();
                ((FibonacciTask) task).setValue(Integer.parseInt(params[1]));
            }
            else if("prime".equals(params[0])){
                task = new PrimeTask();
                ((PrimeTask) task).setValue(Integer.parseInt(params[1]));
            }
        }
        return task;
    }

    public static List<ITask> getTaskList(String filename){
        List<ITask> list = new ArrayList<>();

        InputStream stream = TaskReader.class.getClassLoader().getResourceAsStream(filename);

        try {
            BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));

            String line;
            while((line = buffer.readLine()) != null){
                 list.add(toTask(line));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        return list;
    }
}
