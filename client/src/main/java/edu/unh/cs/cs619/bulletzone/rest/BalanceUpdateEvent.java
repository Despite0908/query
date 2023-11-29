package edu.unh.cs.cs619.bulletzone.rest;

import edu.unh.cs.cs619.bulletzone.util.DoubleWrapper;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

public class BalanceUpdateEvent {

    public DoubleWrapper dw;

    public BalanceUpdateEvent(DoubleWrapper dw) {
        this.dw = dw;
    }
}
