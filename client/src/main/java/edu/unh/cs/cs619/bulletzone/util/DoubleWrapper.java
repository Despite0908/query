package edu.unh.cs.cs619.bulletzone.util;

public class DoubleWrapper {
    private double result;

    public DoubleWrapper() {

    }

    public DoubleWrapper(long result) {
        this.result = result;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }
}
