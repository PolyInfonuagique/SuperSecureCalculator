package ca.polymtl.inf4410.td2.server;

import java.util.Set;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;

import ca.polymtl.inf4410.td2.server.Operations;
import ca.polymtl.inf4410.td2.shared.ServerInterface;
import ca.polymtl.inf4410.td2.shared.model.PrimeTask;
import ca.polymtl.inf4410.td2.shared.model.ITask;
import ca.polymtl.inf4410.td2.shared.model.FibonacciTask;


public class Server implements ServerInterface {
	
	//nombre d'opérations acceptées par tâche
	private Integer q;
	//pourcentage de "malice" du serveur
	private double malice;
	
	protected Server(Integer q, double malice) throws RemoteException {
		this.q = q;
		this.malice=malice;
	}
	
	
	public static void main(String[] args){
		if(System.getSecurityManager()==null){
			System.setSecurityManager(new SecurityManager());
		}
		try {
			String name = "Server";
			Integer init_q = 10;
			Double init_malice = 0.0;
			
			//tests sur les arguments
			if(args.length == 1){
				init_q = Integer.parseInt(args[0]);
			}
			if(args.length == 2){
				init_q =Integer.parseInt(args[0]);
				init_malice = Double.parseDouble(args[1]);
			}
			
			//initialisation du serveur
			ServerInterface server = new Server(init_q,init_malice);
			ServerInterface stub =
					(ServerInterface) UnicastRemoteObject.exportObject(server,0);

			Registry registry = LocateRegistry.getRegistry();
			registry.rebind(name,stub);

			System.out.println("Server Bound");
		}catch (Exception e) {
			System.err.println("Server Exception:");
			e.printStackTrace();
		}
	}
	
	@Override
	public int work(Set<ITask> tasks) throws RemoteException {
		//variables
		double T = 0;
		int result = 0;
		double random_refus = 0;
		double random_malice = 0;
		
		//corps
		//calcul de la variable random malice
		random_malice=Math.random();
		//calcul du résultat en prenant en compte le refus et la malice
		System.out.println("Traitement de " + tasks.size() + " opérations.");
		if(tasks.size() > q){
			T=((tasks.size()-q)/9*q)*100;
			random_refus = Math.random();
			if(random_refus < T)
			{
				System.out.println(" Refus !");
				throw new RemoteException();
			}
		}

		if(random_malice < malice )
				for(ITask t : tasks)
				{
					if(t instanceof PrimeTask){
						result = (result + Operations.prime(t.getValue())) % 4000 + 50;
					}
					else if(t instanceof FibonacciTask){
						result = (result + Operations.fib(t.getValue())) % 4000 - 10;
					}
				}
		else
			for(ITask t : tasks)
			{
				if(t instanceof PrimeTask){
					result = (result + Operations.prime(t.getValue())) % 5000;
					}
				else if(t instanceof FibonacciTask){
					result = (result + Operations.fib(t.getValue())) % 5000;
					}
			}
					
		System.out.println("Resultat des calculs : " + result);
		return result;
	}
	
}
