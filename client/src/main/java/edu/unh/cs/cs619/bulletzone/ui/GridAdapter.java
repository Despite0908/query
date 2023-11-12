package edu.unh.cs.cs619.bulletzone.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

import java.util.HashMap;

import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.TankController;
import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;
import edu.unh.cs.cs619.bulletzone.ClientActivity;

@EBean
public class GridAdapter extends BaseAdapter {

    private final Object monitor = new Object();
    @SystemService
    protected LayoutInflater inflater;
    private int[][] mEntities = new int[16][16];
    private long playerTankId = -1;  // Initialize with an invalid ID

    private ClientActivity clientActivity;
    private BulletZoneRestClient restClient;
    private TankController tc;

    private HashMap<Integer, Integer> bulletDirections = new HashMap<>();


    public void setClientActivityAndRestClient(ClientActivity clientActivity, BulletZoneRestClient restClient) {
        this.clientActivity = clientActivity;
        this.restClient = restClient;
    }

    public void setPlayerTankId(long tankId) {
        this.playerTankId = tankId;
    }


    public void setTankController(TankController tc) {
        this.tc = tc;
    }

    public void updateList(int[][] entities) {
        synchronized (monitor) {
            this.mEntities = entities;
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return 16 * 16;
    }

    @Override
    public Object getItem(int position) {
        return mEntities[(int) position / 16][position % 16];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.field_item, null);
        }

        int size = parent.getWidth() / 16;  // Assuming 16 columns
        convertView.setLayoutParams(new GridView.LayoutParams(size, size));

        int row = position / 16;
        int col = position % 16;
        int val = mEntities[row][col];

        ImageView imageView = (ImageView) convertView;
        imageView.setLayoutParams(new GridView.LayoutParams(size, size));  // Ensure uniform size for all cells

        synchronized (monitor) {
            if (val > 0) {
                if (val == 1000 || (val > 1000 && val <= 2000)) {
                    imageView.setImageResource(R.drawable.crate_metal);
                } else if (val >= 2000 && val < 2000000) {
                    //Log.d("Item Check", "THIS  WOULD BE A POWERUP of VALUE " + val);
                }else if (val >= 2000000 && val <= 3000000) {
                    imageView.setImageResource(R.drawable.bullet_red);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    imageView.setRotation(tc.getDirection() * 45);  // Set bullet rotation based on tank's direction
                } else if (val >= 10000000 && val <= 20000000) {
                    int tankId = val /10000 % 1000;  // Extract the tankId
                    if (tankId == playerTankId) {
                        imageView.setImageResource(R.drawable.tank_sand);
                        imageView.setRotation(tc.getDirection() * 45);  // Set rotation based on direction
                    } else {
                        imageView.setImageResource(R.drawable.tank_dark);
                    }
                } else {
                    imageView.setImageResource(R.drawable.tile_grass);
                }
            } else {
                imageView.setImageResource(R.drawable.tile_grass);
            }
        }

        return convertView;
    }



}
