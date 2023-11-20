package edu.unh.cs.cs619.bulletzone.util;

public class InventoryWrapper {

    //needs to look exact same as in client
    //define default constructor (no arguments
    //
    //
    //

    private int[] collection;

    private boolean poweredUp;

    public InventoryWrapper() {}

    public InventoryWrapper(int[] input) {
        this.collection = input;

    }

    public int[] getResult() {
        return collection;
    }

    public void setResult(int[] set) {
        this.collection = set;
    }






}
