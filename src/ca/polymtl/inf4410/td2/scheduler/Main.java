package ca.polymtl.inf4410.td2.scheduler;

import ca.polymtl.inf4410.td2.scheduler.utils.PropertiesReader;
import ca.polymtl.inf4410.td2.shared.model.ITask;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Main {

    public static void main(String[] args) {
        PropertiesReader reader = new PropertiesReader("resources/scheduler/scenario1.properties");
        String[] ipAddress = reader.getServerAddr();

        TaskManager queue = new TaskManager("resources/scheduler/donnees-2317.txt");
        queue.addObserver((o, arg) -> {
            if(o instanceof TaskManager){
                if(((TaskManager) o).isFinish()){
                    System.out.println("Resultat : " + ((TaskManager) o).getResult());
                }
            }
        });

        for(String ip : ipAddress){
            try {
                ServerThread thread = new ServerThread(queue, ip.trim());
                thread.start();

            } catch (Exception e) {
                System.err.println("Echec dans le démarrage du serveur "+ip);
            }
        }
    }
}
