package ca.polymtl.inf4410.td2.shared.model;


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
