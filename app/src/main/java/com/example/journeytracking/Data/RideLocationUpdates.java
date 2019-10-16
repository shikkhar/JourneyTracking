package com.example.journeytracking.Data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a table within the database. Room creates a table for each class that has @Entity annotation,
 * the fields in the class correspond to columns in the table. Therefore, the entity classes tend to be small
 * model classes that don’t contain any logic.
 */

@Entity(tableName = "ride_location_updates")
public class RideLocationUpdates {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long rideId;

    public double latitude;

    public double longitude;

    public RideLocationUpdates(long rideId, double latitude, double longitude) {
        this.rideId = rideId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
