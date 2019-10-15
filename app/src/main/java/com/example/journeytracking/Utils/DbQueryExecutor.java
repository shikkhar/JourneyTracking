package com.example.journeytracking.Utils;

import android.os.AsyncTask;

import com.example.journeytracking.Data.RideDatabase;
import com.example.journeytracking.Data.RideDetails;
import com.example.journeytracking.Data.RideLocationUpdates;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.example.journeytracking.Utils.CONSTANTS.QuerySelector.*;

public class DbQueryExecutor<S, T> extends AsyncTask<S, Void, T> {

    private RideDatabase db;
    private WeakReference<DbManager.DbOperationCallback> dbOperationCallback;
    private String querySelection;
    private Class<T> clazz;

    public DbQueryExecutor(RideDatabase db, DbManager.DbOperationCallback dbOperationCallback, String querySelection, Class<T> clazz) {
        this.db = db;
        this.dbOperationCallback = new WeakReference<>(dbOperationCallback);
        this.querySelection = querySelection;
        this.clazz = clazz;
    }

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

    @Override
    protected void onPostExecute(T value) {
        initiateCallback(value);
    }

    private void initiateCallback(T value) {
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

            }
        }
    }

    private T executeQuery(S[] s) throws Exception {
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

            default:
                return null;
        }
    }
}
