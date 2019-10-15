package com.example.journeytracking.Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface RideDetailsDao {

    @Query("Select * from ride_details ORDER BY id DESC")
    List<RideDetails> getRideDetailsList ();

    @Insert
    long insertRideDetails(RideDetails rideDetails);

    @Query("Update ride_details " +
            "SET endLatitude = :endLocationLatitude, " +
            "endLongitude = :endLocationLongitude, " +
            "distanceCovered = :distanceCovered, " +
            "isRideComplete = :isRideComplete, " +
            "endTime = :endTime " +
            "WHERE id = :rideId ")
    int updateRideDetails(double endLocationLatitude, double endLocationLongitude, double distanceCovered, boolean isRideComplete, String endTime, long rideId);

    @Delete
    int deleteRideDetails(RideDetails rideDetails);
}
