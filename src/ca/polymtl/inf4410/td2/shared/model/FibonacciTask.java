package ca.polymtl.inf4410.td2.shared.model;

/**
 * FibonacciTask
 * Représente le calcule du nième terme de la suite de fibonacci
 */
public class FibonacciTask implements ITask {

    private int value;

    @Override
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
