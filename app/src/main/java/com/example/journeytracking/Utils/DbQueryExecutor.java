package com.example.journeytracking.Utils;

import android.os.AsyncTask;

import com.example.journeytracking.Data.RideDatabase;
import com.example.journeytracking.Data.RideDetails;
import com.example.journeytracking.Data.RideLocationUpdates;

import java.lang.ref.WeakReference;

import static com.example.journeytracking.Utils.CONSTANTS.QuerySelector.*;

public class DbQueryExecutor<S> extends AsyncTask<S, Void, Long> {

    private RideDatabase db;
    private WeakReference<DbManager.DbOperationCallback> dbOperationCallback;
    private String querySelection;

    public DbQueryExecutor(RideDatabase db, DbManager.DbOperationCallback dbOperationCallback, String querySelection) {
        this.db = db;
        this.dbOperationCallback = new WeakReference<>(dbOperationCallback);
        this.querySelection = querySelection;
    }

    @Override
    protected Long doInBackground(S... s) {

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
    protected void onPostExecute(Long value) {
        initiateCallback(value);
    }

    private void initiateCallback(Long value) {
        DbManager.DbOperationCallback dbOperationCallback = this.dbOperationCallback.get();
        if (dbOperationCallback != null) {

            switch (querySelection) {

                case INSERT_RIDE:
                    dbOperationCallback.onInsertRide(value);
                    break;

                case INSERT_LOCATION_UPDATE:
                    dbOperationCallback.onInsertLocationUpdate(value);
                    break;

                case UPDATE_RIDE:
                    dbOperationCallback.onUpdateRide();
                    break;

            }
        }
    }

    private Long executeQuery(S[] s) throws Exception {
        switch (querySelection) {

            case INSERT_RIDE:
                return db.rideDetailsDao().insertRideDetails((RideDetails) s[0]);

            case INSERT_LOCATION_UPDATE:
                return db.rideLocationUpdatesDao().insertRideLocationUpdates((RideLocationUpdates) s[0]);

            case UPDATE_RIDE:
                Long result = Long.valueOf(db.rideDetailsDao().updateRideDetails(((RideDetails) s[0]).endLatitude,
                        ((RideDetails) s[0]).endLongitude,
                        ((RideDetails) s[0]).distanceCovered,
                        ((RideDetails) s[0]).isRideComplete,
                        ((RideDetails) s[0]).id));
                return result;

            default:
                return null;
        }
    }
}
