package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.util.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import edu.unh.cs.cs619.bulletzone.datalayer.BulletZoneData;
import edu.unh.cs.cs619.bulletzone.datalayer.account.BankAccount;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUser;
import edu.unh.cs.cs619.bulletzone.model.entities.Builder;
import edu.unh.cs.cs619.bulletzone.repository.DataRepository;
import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.events.CustomEvent;
import edu.unh.cs.cs619.bulletzone.events.CustomEventTypes;
import edu.unh.cs.cs619.bulletzone.events.EventListener;
import edu.unh.cs.cs619.bulletzone.model.entities.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.entities.Soldier;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;

public final class Game {
    /**
     * Field dimensions
     */
    private static final int FIELD_DIM = 16;
    private final long id;
    private final ArrayList<FieldHolder> holderGrid = new ArrayList<>();

    private final ConcurrentMap<Long, Tank> tanks = new ConcurrentHashMap<>();

    private final ConcurrentMap<Long, Builder> builders = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, Soldier> soldiers = new ConcurrentHashMap<>();

    //associate the inventory with username
    EventBus eventBus = BusProvider.BusProvider().eventBus;

    //EventBus eventBus = new AppConfig().eventBus();
    // was working EventBus eventBus = new EventBus();

    /**
     * Map of Items on the Grid
     * Key = grid cell location
     * Item = Powerup
     */
    private int numItems = 0;

    /**
     * Key: IP Address
     * Value: The ID of it's associated Tank
     */
    private final ConcurrentMap<String, Player> playersIP = new ConcurrentHashMap<>();

    private final Object monitor = new Object();

    public Game() {
        EventListener listener = new EventListener(eventBus);
        eventBus.register(this);
        //eventBus.register(listener);
        //CustomEvent customEvent = new CustomEvent("Custom Event");
        //eventBus.post(customEvent);
        this.id = 0;
    }

    public EventBus getGameEventBus() {
        return eventBus;
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    @JsonIgnore
    public ArrayList<FieldHolder> getHolderGrid() {
        return holderGrid;
    }

    public void addPlayer(String ip, Player player) {
        synchronized (tanks) {
            tanks.put(player.getTank().getId(), player.getTank());
            builders.put(player.getBuilder().getId(), player.getBuilder());
            playersIP.put(ip, player);
        }
    }

    public void incrementItems() {
        numItems++;
    }

    public void decrementItems() {
        numItems--;
    }

    public int getNumItems() {
        return numItems;
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



    //TODO: This is bad
    public void removeSoldier(long soldierId){
        synchronized (soldiers) {
            Soldier s = soldiers.remove(soldierId);
        }
    }

    public Tank getTank(String ip){
        if (playersIP.containsKey(ip)){
            return tanks.get(playersIP.get(ip).getTank().getId());
        }
        return null;
    }

    public Builder getBuilder(String ip) {
        if (playersIP.containsKey(ip)) {
            return builders.get(playersIP.get(ip).getBuilder().getId());
        }
        return null;
    }

    public ConcurrentMap<Long, Builder> getBuilders() {
       return builders;
    }

    public ConcurrentMap<Long, Soldier> getSoldiers() {
        return soldiers;
    }

    public Soldier getSoldier(int soldierId) {
        return soldiers.get(soldierId);
    }

    public void removeBuilder(long builderId) {
        synchronized (builders) {
            Builder b = builders.remove(builderId);
            if (b != null) {
                Player p = playersIP.get(b.getIp());
                p.setBuilder(null);
                if (p.getTank() == null) {
                    playersIP.remove(b.getIp());
                }
            }
        }
    }

    public void removeTank(long tankId){
        synchronized (tanks) {
            Tank t = tanks.remove(tankId);
            if (t != null) {
                Player p = playersIP.get(t.getIp());
                p.setTank(null);
                if (p.getBuilder() == null) {
                    playersIP.remove(t.getIp());
                }
            }
        }
    }

    //TODO: IMPROVEMENTS BEING IN HERE IS TEMPORARY!!! CHANGE TO 3 LAYER MODEL WHEN EVENT SYSTEM IS FINISHED
    public int[][] getGrid2D() {
        int[][] grid = new int[FIELD_DIM][FIELD_DIM];

        synchronized (holderGrid) {
            FieldHolder holder;
            for (int i = 0; i < FIELD_DIM; i++) {
                for (int j = 0; j < FIELD_DIM; j++) {
                    holder = holderGrid.get(i * FIELD_DIM + j);
                    if (holder.isPresent()) {
                        grid[i][j] = holder.getEntity().getIntValue();
                    }else if (holder.isImproved()) {
                        grid[i][j] = holder.getImprovement().getIntValue();
                    }else {
                        grid[i][j] = Terrain.toByte(holder.getTerrain());
                    }
                }
            }
        }
        return grid;
    }

    /**
     * Compiles a 2D grid of terrain from the field.
     * @return 2D integer array representation of the field terrain.
     */
    public int[][] getTerrainGrid() {
        int[][] grid = new int[FIELD_DIM][FIELD_DIM];

        synchronized (holderGrid) {
            FieldHolder holder;
            for (int i = 0; i < FIELD_DIM; i++) {
                for (int j = 0; j < FIELD_DIM; j++) {
                    holder = holderGrid.get(i * FIELD_DIM + j);
                    if (holder.isPresent()) {
                        grid[i][j] = Terrain.toByte(holder.getTerrain());
                    } else {
                        grid[i][j] = 0;
                    }
                }
            }
        }
        return grid;
    }

    @Subscribe
    public void someCustomEvent(CustomEvent customEvent) {
        System.out.println("Received event " + customEvent.getEventType().name() + " in class game -- numItems is " + numItems + " about to decrement");
        decrementItems();
    }
}
