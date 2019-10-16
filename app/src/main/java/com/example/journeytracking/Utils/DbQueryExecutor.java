package com.example.journeytracking.Utils;

import android.os.AsyncTask;

import com.example.journeytracking.Data.RideDatabase;
import com.example.journeytracking.Data.RideDetails;
import com.example.journeytracking.Data.RideLocationUpdates;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.example.journeytracking.Utils.CONSTANTS.QuerySelector.*;

/**
 * Since Room Queries must be made on threads other than the main thread
 * We are using generic async tasks to accomplish this
 *
 * @param <S> is the type of the input argument
 * @param <T> is the type of the output argument
 */
class DbQueryExecutor<S, T> extends AsyncTask<S, Void, T> {

    private RideDatabase db;
    //weak reference to our db operation callback object
    //it is only needed once we get the results of the query
    private WeakReference<DbManager.DbOperationCallback> dbOperationCallback;
    private String querySelection;
    //object of return type T for casting purposes
    private Class<T> clazz;

    public DbQueryExecutor(RideDatabase db, DbManager.DbOperationCallback dbOperationCallback, String querySelection, Class<T> clazz) {
        this.db = db;
        this.dbOperationCallback = new WeakReference<>(dbOperationCallback);
        this.querySelection = querySelection;
        this.clazz = clazz;
    }

    //execute the query
    @Override
    protected T doInBackground(S... s) {

        try {
            return executeQuery(s);
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //initiate callback on receiving the results
    @Override
    protected void onPostExecute(T value) {
        initiateCallback(value);
    }

    //execute query based on the which query has been passed as argument
    private T executeQuery(S[] s) throws ClassCastException {
        switch (querySelection) {

            case INSERT_RIDE:
                return clazz.cast(db.rideDetailsDao().insertRideDetails((RideDetails) s[0]));

            case INSERT_LOCATION_UPDATE:
                return clazz.cast(db.rideLocationUpdatesDao().insertRideLocationUpdates((RideLocationUpdates) s[0]));

            case UPDATE_RIDE:
                return clazz.cast(db.rideDetailsDao().updateRideDetails(((RideDetails) s[0]).endLatitude,
                        ((RideDetails) s[0]).endLongitude,
                        ((RideDetails) s[0]).distanceCovered,
                        ((RideDetails) s[0]).isRideComplete,
                        ((RideDetails) s[0]).endTime,
                        ((RideDetails) s[0]).id));

            case FETCH_RIDE_LIST:
                return clazz.cast(db.rideDetailsDao().getRideDetailsList());

            case FETCH_RIDE_LOCATIONS_LIST:
                return clazz.cast(db.rideLocationUpdatesDao().getRideLocationUpdatesList((Long) s[0]));

            default:
                return null;
        }
    }

    //initiate callbacks based on the type of query
    private void initiateCallback(T value) throws ClassCastException {
        DbManager.DbOperationCallback dbOperationCallback = this.dbOperationCallback.get();
        if (dbOperationCallback != null) {

            switch (querySelection) {

                case INSERT_RIDE:
                    dbOperationCallback.onInsertRide((Long) value);
                    break;

                case INSERT_LOCATION_UPDATE:
                    dbOperationCallback.onInsertLocationUpdate((Long) value);
                    break;

                case UPDATE_RIDE:
                    dbOperationCallback.onUpdateRide();
                    break;
                case FETCH_RIDE_LIST:
                    dbOperationCallback.onRideListFetched((ArrayList<RideDetails>) value);
                    break;
                case FETCH_RIDE_LOCATIONS_LIST:
                    dbOperationCallback.onRideLocationsListFetched((ArrayList<RideLocationUpdates>) value);
                    break;

            }
        }
    }
}
