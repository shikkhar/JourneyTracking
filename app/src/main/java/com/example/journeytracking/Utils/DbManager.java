package com.example.journeytracking.Utils;

import com.example.journeytracking.Data.RideDatabase;
import com.example.journeytracking.Data.RideDetails;
import com.example.journeytracking.Data.RideLocationUpdates;
import java.util.ArrayList;
import static com.example.journeytracking.Utils.CONSTANTS.QuerySelector.*;


/**
 * Class to execute Database Queries
 * All room queries must be made on a separate thread
 * We use Generic Async Tasks to achieve that here
 */
public class DbManager {

    private RideDatabase db;

    public DbManager(RideDatabase db) {
        this.db = db;
    }

    //function to insert a new ride
    public void insertRideDetails(RideDetails rideDetails, DbOperationCallback dbOperationCallback) {

        DbQueryExecutor executor  = new DbQueryExecutor<RideDetails, Long>(db, dbOperationCallback, INSERT_RIDE, Long.class);
        executor.execute(rideDetails);

    }

    //function to insert location updates for a ride
    public void insertLocationUpdates(RideLocationUpdates locationUpdates, DbOperationCallback dbOperationCallback) {
        DbQueryExecutor executor  = new DbQueryExecutor<RideLocationUpdates, Long>(db, dbOperationCallback, INSERT_LOCATION_UPDATE, Long.class);
        executor.execute(locationUpdates);

    }

    //function to update a ride when it has ended
    public void updateRideDetails(RideDetails rideDetails, DbOperationCallback dbOperationCallback) {
        DbQueryExecutor executor  = new DbQueryExecutor<RideDetails, Integer>(db, dbOperationCallback, UPDATE_RIDE, Integer.class);
        executor.execute(rideDetails);

    }

    //function to get all rides which have been completed
    public void getRideDetails(DbOperationCallback dbOperationCallback) {
        DbQueryExecutor executor  = new DbQueryExecutor<Void, ArrayList>(db, dbOperationCallback, FETCH_RIDE_LIST, ArrayList.class);
        executor.execute();
    }

    //function to get all the locations of a ride
    public void getRideLocations(Long rideId, DbOperationCallback dbOperationCallback) {
        DbQueryExecutor executor  = new DbQueryExecutor<Long, ArrayList>(db, dbOperationCallback, FETCH_RIDE_LOCATIONS_LIST, ArrayList.class);
        executor.execute(rideId);
    }

    /**
     * Database operation callbacks
     */

    public interface DbOperationCallback {
        void onInsertRide(long insertedRowId);
        void onInsertLocationUpdate(long insertedRowId);
        void onUpdateRide();
        void onRideListFetched(ArrayList<RideDetails> rideDetailsList);
        void onRideLocationsListFetched(ArrayList<RideLocationUpdates> rideLocationsList);
    }
}
