package edu.unh.cs.cs619.bulletzone.model;

public class Token {

    protected int tokenID;
    protected int position;

    public Token(long id, int position) {
        tokenID = (int)id;
        this.position = position;
    }
    public int getID() {
        return tokenID;
    }
    public void setNewPosition(int position) {
        this.position = position;
    }
}
