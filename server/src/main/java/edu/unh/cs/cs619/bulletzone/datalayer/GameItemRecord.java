/**
 * Internal package structure for representing data coming out of a SQL query on the Item table
 */
package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import edu.unh.cs.cs619.bulletzone.datalayer.itemType.ItemType;
import edu.unh.cs.cs619.bulletzone.datalayer.itemType.ItemTypeRepository;

class GameItemRecord extends EntityRecord {
    ItemType itemType;
    double usageMonitor;

    GameItemRecord(ItemType giItemType) {
        super(giItemType.isContainer() ? EntityType.ItemContainer : EntityType.Item);
        itemType = giItemType;
        usageMonitor = 0;
    }

    GameItemRecord(ResultSet itemResult, ItemTypeRepository itemTypeRepo) {
        super(itemResult);
        try {
            itemType = itemTypeRepo.getType(itemResult.getInt("ItemTypeID"));
            usageMonitor = itemResult.getDouble("UsageMonitor");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to extract data from item result set", e);
        }
    }

    String getInsertString() {
        return " INSERT INTO Item ( EntityID, ItemTypeID, UsageMonitor )\n" +
                "    VALUES (" + entityID + ","
                + itemType.getID() + ", "
                + usageMonitor + "); ";
    }

    @Override
    void insertInto(Connection dataConnection) throws SQLException {
        super.insertInto(dataConnection);
        PreparedStatement containerStatement = dataConnection.prepareStatement(getInsertString());

        int affectedRows = containerStatement.executeUpdate();
        if (affectedRows == 0)
            throw new SQLException("Creating Item record for type " + itemType.getName() + " failed.");
    }

    void insertContainerInfoInto(String name, Connection dataConnection) throws SQLException {
        PreparedStatement containerStatement = dataConnection.prepareStatement(
                "INSERT INTO ItemContainer ( EntityID, Name ) VALUES ( " + entityID + ", '" + name + "');");
        int affectedRows = containerStatement.executeUpdate();
        if (affectedRows == 0)
            throw new SQLException("Creating ItemContainer record for type " + itemType.getName() + " failed.");
    }
}