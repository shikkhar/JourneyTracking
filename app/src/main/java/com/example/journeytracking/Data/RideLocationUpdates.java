package com.example.journeytracking.Data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "ride_location_updates"/*, foreignKeys = @ForeignKey(entity = RideDetails.class,
        parentColumns = "id",
        childColumns = "rideId",
        onDelete = CASCADE)*/)
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
