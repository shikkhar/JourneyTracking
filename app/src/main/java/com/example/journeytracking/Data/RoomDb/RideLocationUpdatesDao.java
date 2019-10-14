package com.example.journeytracking.Data.RoomDb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RideLocationUpdatesDao {

    @Query("Select * from ride_location_updates")
    List<RideLocationUpdates> getRideLocationUpdatesList ();

    @Insert
    long insertRideLocationUpdates(RideLocationUpdates rideLocationUpdates);

    @Update
    void updateRideLocationUpdates(RideLocationUpdates rideLocationUpdates);

    @Delete
    void deleteRideLocationUpdates(RideLocationUpdates rideLocationUpdates);
}
