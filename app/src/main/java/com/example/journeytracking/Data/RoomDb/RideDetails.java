package com.example.journeytracking.Data.RoomDb;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "ride_details")
public class RideDetails {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public double startLatitude;

    public double startLongitude;

    public double endLatitude;

    public double endLongitude;

    public double distanceCovered;

    @Ignore
    public RideDetails(double endLatitude, double endLongitude, double distanceCovered, long rideId) {

        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.distanceCovered = distanceCovered;
        this.id = rideId;
    }

    public RideDetails(double startLatitude, double startLongitude) {
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
    }
}
