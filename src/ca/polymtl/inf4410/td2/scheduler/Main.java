package ca.polymtl.inf4410.td2.scheduler;

import ca.polymtl.inf4410.td2.scheduler.utils.PropertiesReader;

import java.io.IOException;

/**
 * Main class to execute
 */
public class Main {

    /**
     * Main
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final long startTime = System.nanoTime();

        // Check arguments
        if(args.length <2){
            System.err.println("Erreur arguments ");
            return;
        }

        // Load file properties
        PropertiesReader reader = new PropertiesReader("resources/scheduler/"+args[0]);
        String[] ipAddress = reader.getServerAddr();

        // Init main queue manager
        TaskManager queue = new TaskManager("resources/scheduler/"+args[1],args.length>1);
        queue.addObserver((o, arg) -> {
            if(o instanceof TaskManager){
                if(((TaskManager) o).isFinish()){
                    final long endTime = System.nanoTime();
                    double duration = (endTime - startTime)/1000000;  //divide by 1000000 to get milliseconds.
                    System.out.println(((TaskManager) o).getResult() + " : " + duration);
                }
            }
        });

        // Starts server threads
        for(String ip : ipAddress){
            try {
                new ServerThread(queue, ip.trim()).start();
            } catch (Exception e) {
                System.err.println("Echec dans le démarrage du serveur "+ip);
            }
        }
    }
}
