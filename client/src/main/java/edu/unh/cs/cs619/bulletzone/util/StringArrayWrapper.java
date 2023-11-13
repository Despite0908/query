package edu.unh.cs.cs619.bulletzone.util;

public class StringArrayWrapper {
    private String[] collection;

    public StringArrayWrapper(String[] input) {
        this.collection = input;
    }

    public String[] getGrid() {
        return this.collection;
    }

    public void setGrid(String[] set) {
        this.collection = set;
    }

}

