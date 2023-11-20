package edu.unh.cs.cs619.bulletzone.model.improvements;

public class Wall extends Improvement {
    int destructValue, pos;

    public Wall(int pos, int destructValue){
        this.destructValue = destructValue;
        this.pos = pos;
    }

    @Override
    public Improvement copy() {
        return new Wall(pos, destructValue);
    }

    @Override
    public int getIntValue() {
        return destructValue;
    }

    @Override
    public String toString() {
        return "W";
    }

    public int getPos(){
        return pos;
    }

    @Override
    public boolean isSolid() {
        return true;
    }
}
