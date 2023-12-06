package edu.unh.cs.cs619.bulletzone.ui;

public class GameUser {

    private static GameUser user;

    private int[] inventory = new int[3];

    private int tankHealth;

    private int soldierHealth;




    private String username;
    private int id = -1;

    public void setTankHealth(int tankHealth) {
        this.tankHealth = tankHealth;
    }

    public int getTankHealth() {
        return tankHealth;
    }

    public void setSoldierHealth(int soldierHealth) {
        this.soldierHealth = soldierHealth;
    }

    public int getSoldierHealth(){
        return soldierHealth;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static synchronized GameUser getInstance() {
        if (user == null) {
            user = new GameUser();
        }
        return user;
    }

    public int[] getInventory() {
        return inventory;
    }




}
