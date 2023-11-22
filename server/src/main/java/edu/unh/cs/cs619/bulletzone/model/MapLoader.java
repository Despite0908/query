package edu.unh.cs.cs619.bulletzone.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.unh.cs.cs619.bulletzone.model.entities.ItemTypes;

/**
 * Loads map from a JSON map file.
 * @author Anthony Papetti
 */

public class MapLoader {
    private final String mapPath;

    /**
     * Constructor that specifies the map file to be loaded.
     * @param mapPath Path of the file to be loaded
     */
    public MapLoader(String mapPath) {
        this.mapPath = mapPath;
    }

    /**
     * Loads map file into a GameMap object.
     * @return Object representation of the map file.
     */
    public GameBuilder load() {
        //Get file
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(mapPath));
        } catch (FileNotFoundException e) {
            return null;
        }

        //Read json file to String;
        String str;
        try {
            //Read json file into string
            StringBuilder builder = new StringBuilder();
            while ((str = reader.readLine()) != null) {
                builder.append(str).append("\n");
            }
            str = builder.toString();
        } catch (IOException e) {
            return null;
        }

        //String to JSONArray for walls, parse json
        GameBuilder g = new GameBuilder();
        JSONArray wallPos = new JSONObject(str).getJSONArray("map");
        for (int i = 0; i < wallPos.length(); i++) {
            Object holder = wallPos.get(i);
            if (holder instanceof Integer) {
                g.setWall((Integer) holder);
            } else {
                JSONObject w = (JSONObject) holder;
                g.setWall(w.getInt("pos"), w.getInt("destructVal"));
            }
        }

        //String to JSONArray for terrain, parse JSON
        Map<Terrain, JSONArray> terrainPosMap = new HashMap<>();
        try {
            terrainPosMap.put(Terrain.Rocky, new JSONObject(str).getJSONArray("rocky"));
        } catch (JSONException ignored) {}
        try {
            terrainPosMap.put(Terrain.Hilly, new JSONObject(str).getJSONArray("hilly"));
        } catch (JSONException ignored) {}
        try {
            terrainPosMap.put(Terrain.Forest, new JSONObject(str).getJSONArray("forest"));
        } catch (JSONException ignored) {}

        for (Terrain t: terrainPosMap.keySet()) {
            JSONArray curArr = terrainPosMap.get(t);
            if (curArr != null) {
                for (int i = 0; i < curArr.length(); i++) {
                    g.addTerrain(curArr.getInt(i), t);
                }
            }
        }

        //String to JSONArray for Items, parse JSON
        Map<ItemTypes, JSONArray> itemPosMap = new HashMap<>();
        try {
            itemPosMap.put(ItemTypes.ANTI_GRAV, new JSONObject(str).getJSONArray("ANTI_GRAV"));
        } catch (JSONException ignored) {}
        try {
            itemPosMap.put(ItemTypes.FUSION_REACTOR, new JSONObject(str).getJSONArray("FUSION_REACTOR"));
        } catch (JSONException ignored) {}
        try {
            itemPosMap.put(ItemTypes.COIN, new JSONObject(str).getJSONArray("COIN"));
        } catch (JSONException ignored) {}

        for (ItemTypes t: itemPosMap.keySet()) {
            JSONArray curArr = itemPosMap.get(t);
            if (curArr != null) {
                for (int i = 0; i < curArr.length(); i++) {
                    g.addItem(curArr.getInt(i), t);
                }
            }
        }



        return g;
    }
}
