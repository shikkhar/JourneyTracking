package com.example.journeytracking.Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

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
            "distanceCovered = :distanceCovered, " +
            "isRideComplete = :isRideComplete " +
            "WHERE id = :rideId")
    int updateRideDetails(double endLocationLatitude, double endLocationLongitude, double distanceCovered, boolean isRideComplete, long rideId);

    @Delete
    int deleteRideDetails(RideDetails rideDetails);
}
