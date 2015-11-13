package ca.polymtl.inf4410.td2.scheduler;

import ca.polymtl.inf4410.td2.server.Operations;
import ca.polymtl.inf4410.td2.shared.model.FibonacciTask;
import ca.polymtl.inf4410.td2.shared.model.ITask;
import ca.polymtl.inf4410.td2.shared.model.PrimeTask;

import java.util.List;

/**
 * Created by david on 11-11-15.
 */
public class FakeServer {

    private int q;

    public FakeServer(int q) {
        this.q = q;
    }

    public int traitement(List<ITask> test) throws Exception {

        int result = 0;

        for(ITask t : test){
            if(t instanceof FibonacciTask){
                result = (result + Operations.fib(t.getValue())) % 5000;
            }
            else if(t instanceof PrimeTask){
                result = (result + Operations.prime(t.getValue())) % 5000;
            }
        }
        if(test.size() > q){
            throw new Exception();
        }

        return result;
    }
}
