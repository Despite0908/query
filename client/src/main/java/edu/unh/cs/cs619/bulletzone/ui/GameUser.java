package edu.unh.cs.cs619.bulletzone.ui;

public class GameUser {

    private static GameUser user;

    private int[] inventory = new int[1];


    private String username;
    private int id = -1;


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
