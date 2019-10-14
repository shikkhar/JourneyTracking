package com.example.journeytracking.Data.RoomDb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RideDetailsDao {

    @Query("Select * from ride_details")
    List<RideDetails> getRideDetailsList ();

    @Insert
    long insertRideDetails(RideDetails rideDetails);

    @Query("Update ride_details " +
            "SET endLatitude = :endLocationLatitude, " +
            "endLongitude = :endLocationLongitude, " +
            "distanceCovered = :distanceCovered " +
            "WHERE id = :rideId")
    void updateRideDetails(double endLocationLatitude, double endLocationLongitude, double distanceCovered, long rideId);

    @Delete
    void deleteRideDetails(RideDetails rideDetails);
}
