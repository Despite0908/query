package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import java.util.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import edu.unh.cs.cs619.bulletzone.datalayer.BulletZoneData;
import edu.unh.cs.cs619.bulletzone.datalayer.account.BankAccount;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUser;
import edu.unh.cs.cs619.bulletzone.repository.DataRepository;

public final class Game {
    /**
     * Field dimensions
     */
    private static final int FIELD_DIM = 16;
    private final long id;
    private final ArrayList<FieldHolder> holderGrid = new ArrayList<>();

    private final ConcurrentMap<Long, Tank> tanks = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, Soldier> soldiers = new ConcurrentHashMap<>();

    BulletZoneData data = new DataRepository().getbzData();

    //associate the inventory with username

    /**
     * Map of Items on the Grid
     * Key = grid cell location
     * Item = Powerup
     */
    private final ConcurrentMap<Integer, Item> items = new ConcurrentHashMap<>();

    /**
     * Key: IP Address
     * Value: The ID of it's associated Tank
     */
    private final ConcurrentMap<String, Long> playersIP = new ConcurrentHashMap<>();

    private final Object monitor = new Object();

    public Game() {
        this.id = 0;
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    @JsonIgnore
    public ArrayList<FieldHolder> getHolderGrid() {
        return holderGrid;
    }

    public void addTank(String ip, Tank tank) {
        synchronized (tanks) {
            tanks.put(tank.getId(), tank);
            playersIP.put(ip, tank.getId());
        }
    }

    public ConcurrentMap<Integer, Item> getItems() {
        return items;
    }

    public Tank getTank(int tankId) {
        return tanks.get(tankId);
    }

    public ConcurrentMap<Long, Tank> getTanks() {
        return tanks;
    }

    public List<Optional<FieldEntity>> getGrid() {
        synchronized (holderGrid) {
            List<Optional<FieldEntity>> entities = new ArrayList<Optional<FieldEntity>>();

            FieldEntity entity;
            for (FieldHolder holder : holderGrid) {
                if (holder.isPresent()) {
                    entity = holder.getEntity();
                    entity = entity.copy();

                    entities.add(Optional.<FieldEntity>of(entity));
                } else {
                    entities.add(Optional.<FieldEntity>empty());
                }
            }
            return entities;
        }
    }

    public void addSoldier(Soldier soldier) {
        synchronized (soldiers) {
            soldiers.put(soldier.getId(), soldier);
        }
    }

    public int[] getInventory(String username){
        int [] inventory = new int[1];

        int balance = getCredits(username);

        inventory[0] = balance;

        return inventory;
    }

    public int getCredits(String username) {
        try {
            GameUser user = data.users.getUser(username);

            Collection<BankAccount> accounts = user.getOwnedAccounts();

            BankAccount ba = accounts.iterator().next();

            double balance = ba.getBalance();

            return (int) balance;
        } catch (NullPointerException e) {
            System.out.println("Error: getting coins, user not found");
        }

        return 0;
    }



    //TODO: This is bad
    public void removeSoldier(long soldierId){
        synchronized (soldiers) {
            Soldier s = soldiers.remove(soldierId);
        }
    }

    public Tank getTank(String ip){
        if (playersIP.containsKey(ip)){
            return tanks.get(playersIP.get(ip));
        }
        return null;
    }

    public ConcurrentMap<Long, Soldier> getSoldiers() {
        return soldiers;
    }

    public Soldier getSoldier(int soldierId) {
        return soldiers.get(soldierId);
    }

    public void removeTank(long tankId){
        synchronized (tanks) {
            Tank t = tanks.remove(tankId);
            if (t != null) {
                playersIP.remove(t.getIp());
            }
        }
    }

    public int[][] getGrid2D() {
        int[][] grid = new int[FIELD_DIM][FIELD_DIM];

        synchronized (holderGrid) {
            FieldHolder holder;
            for (int i = 0; i < FIELD_DIM; i++) {
                for (int j = 0; j < FIELD_DIM; j++) {
                    holder = holderGrid.get(i * FIELD_DIM + j);
                    if (holder.isPresent()) {
                        grid[i][j] = holder.getEntity().getIntValue();
                    } else {
                        grid[i][j] = 0;
                    }
                }
            }
        }

        return grid;
    }
}
