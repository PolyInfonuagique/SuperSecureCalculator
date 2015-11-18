package ca.polymtl.inf4410.td2.scheduler;

import ca.polymtl.inf4410.td2.scheduler.utils.PropertiesReader;

public class Main {

    public static void main(String[] args) {
        final long startTime = System.nanoTime();

        PropertiesReader reader = new PropertiesReader("resources/scheduler/scenario1.properties");
        String[] ipAddress = reader.getServerAddr();

        TaskManager queue = new TaskManager("resources/scheduler/donnees-2317.txt",false);
        queue.addObserver((o, arg) -> {
            if(o instanceof TaskManager){
                if(((TaskManager) o).isFinish()){
                    System.out.println("Resultat : " + ((TaskManager) o).getResult());

                    final long endTime = System.nanoTime();
                    double duration = (endTime - startTime)/1000000;  //divide by 1000000 to get milliseconds.
                    System.out.println("Temps d'execution : " + duration + "ms");
                }
            }
        });

        for(String ip : ipAddress){
            try {
                new ServerThread(queue, ip.trim()).start();
            } catch (Exception e) {
                System.err.println("Echec dans le démarrage du serveur "+ip);
            }
        }
    }
}
