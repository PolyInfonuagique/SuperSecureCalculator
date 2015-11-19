package ca.polymtl.inf4410.td2.shared.model;

/**
 * PrimeTask
 * Représente le calcule du plus grand facteur commun
 */
public class PrimeTask implements ITask{
    private int value;

    @Override
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
