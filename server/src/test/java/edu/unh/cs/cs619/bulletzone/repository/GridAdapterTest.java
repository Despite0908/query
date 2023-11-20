package edu.unh.cs.cs619.bulletzone.repository;


import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;

import edu.unh.cs.cs619.bulletzone.datalayer.account.BankAccount;
import edu.unh.cs.cs619.bulletzone.datalayer.account.BankAccountRepository;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUser;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.GameBuilder;
import edu.unh.cs.cs619.bulletzone.model.GameMap;
import edu.unh.cs.cs619.bulletzone.model.Wall;


public class GridAdapterTest {

    @Test
    //test wall loader
    public void wallLoaderTest(){
//        GameMap gameMap = new GameMap();
//        gameMap.addWall(1, 1);
//        gameMap.addWall(2, 2);
        GameBuilder gb = new GameBuilder();



        Game g = gb.build();

        ArrayList<FieldHolder> holderGrid = g.getHolderGrid();

        //Assert.assertTrue(holderGrid.get(1).getEntity() instanceof Wall);

        GameMap gameMap = new GameMap();

        gameMap.addWall(1, 1);
        gameMap.addWall(2, 2);




        gb.setWall(1);
//      gb.setWall(2);

        Assert.assertTrue(holderGrid.get(2).getEntity() instanceof Wall);

//        gb.setWall(1);
//        gb.setWall(2);

        //iterate through fieldarray, get fieldholder for each index, check of fieldholder is an instace of
        //x


    }









}
