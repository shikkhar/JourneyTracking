package com.example.journeytracking.Data;

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

    public boolean isRideComplete;

    public String startTime;

    public String endTime;

    @Ignore
    public RideDetails(double endLatitude, double endLongitude, double distanceCovered, boolean isRideComplete, String endTime, long rideId) {

        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.distanceCovered = distanceCovered;
        this.isRideComplete = isRideComplete;
        this.endTime = endTime;
        this.id = rideId;
    }

    public RideDetails(double startLatitude, double startLongitude, boolean isRideComplete, String startTime) {
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.isRideComplete = isRideComplete;
        this.startTime = startTime;
    }
}
