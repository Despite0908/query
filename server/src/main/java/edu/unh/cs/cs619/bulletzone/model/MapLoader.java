package edu.unh.cs.cs619.bulletzone.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MapLoader {
    private final String mapPath;

    public MapLoader(String mapPath) {
        this.mapPath = mapPath;
    }

    public GameMap load() {
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
        GameMap g = new GameMap();
        JSONArray wallPos = new JSONObject(str).getJSONArray("map");
        for (int i = 0; i < wallPos.length(); i++) {
            Object holder = wallPos.get(i);
            if (holder instanceof Integer) {
                g.addWall((Integer) holder);
            } else {
                JSONObject w = (JSONObject) holder;
                g.addWall(w.getInt("pos"), w.getInt("destructVal"));
            }
        }
        return g;
    }
}
