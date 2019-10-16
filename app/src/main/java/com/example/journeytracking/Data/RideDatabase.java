package com.example.journeytracking.Data;

import androidx.room.Database;
import androidx.room.RoomDatabase;


@Database(entities = {RideDetails.class, RideLocationUpdates.class}, exportSchema = false, version = 1)
public abstract class RideDatabase extends RoomDatabase {

    public abstract RideDetailsDao rideDetailsDao();
    public abstract RideLocationUpdatesDao rideLocationUpdatesDao();
}

