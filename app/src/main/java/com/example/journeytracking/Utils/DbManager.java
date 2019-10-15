package com.example.journeytracking.Utils;

import com.example.journeytracking.Data.RideDatabase;
import com.example.journeytracking.Data.RideDetails;
import com.example.journeytracking.Data.RideLocationUpdates;

import static com.example.journeytracking.Utils.CONSTANTS.QuerySelector.*;

public class DbManager {

    private RideDatabase db;

    public DbManager(RideDatabase db) {
        this.db = db;
    }

    public void insertRideDetails(RideDetails rideDetails, DbOperationCallback dbOperationCallback) {

        DbQueryExecutor executor  = new DbQueryExecutor<RideDetails>(db, dbOperationCallback, INSERT_RIDE);
        executor.execute(rideDetails);
        /*InsertRideDetailsAsyncTask asyncTask = new InsertRideDetailsAsyncTask(dbOperationCallback);
        asyncTask.execute(new RideDetails[] {rideDetails});*/
    }

    public void insertLocationUpdates(RideLocationUpdates locationUpdates, DbOperationCallback dbOperationCallback) {
        DbQueryExecutor executor  = new DbQueryExecutor<RideLocationUpdates>(db, dbOperationCallback, INSERT_LOCATION_UPDATE);
        executor.execute(locationUpdates);
       /*InsertLocationUpdatesAsyncTask asyncTask = new InsertLocationUpdatesAsyncTask(dbOperationCallback);
       asyncTask.execute(new RideLocationUpdates[] {locationUpdates});*/
    }

    public void updateRideDetails(RideDetails rideDetails, DbOperationCallback dbOperationCallback) {
        DbQueryExecutor executor  = new DbQueryExecutor<RideDetails>(db, dbOperationCallback, UPDATE_RIDE);
        executor.execute(rideDetails);
        /*UpdateRideDetailsAsyncTask asyncTask = new UpdateRideDetailsAsyncTask(dbOperationCallback);
        asyncTask.execute(new RideDetails[] {rideDetails});*/
    }




   /* private class InsertRideDetailsAsyncTask extends AsyncTask<RideDetails, Void, Long> {
        private WeakReference<DbOperationCallback> dbOperationCallback;

        public InsertRideDetailsAsyncTask(DbOperationCallback dbOperationCallback) {
            this.dbOperationCallback = new WeakReference<>(dbOperationCallback);
        }

        @Override
        protected Long doInBackground(RideDetails... rideDetails) {
            return db.rideDetailsDao().insertRideDetails(rideDetails[0]);
        }

        @Override
        protected void onPostExecute(Long value) {
            super.onPostExecute(value);
            DbOperationCallback dbOperationCallback = this.dbOperationCallback.get();
            if (dbOperationCallback != null)
                dbOperationCallback.onInsertRide(value);

        }
    }

    private class InsertLocationUpdatesAsyncTask extends AsyncTask<RideLocationUpdates, Void, Long> {
        private WeakReference<DbOperationCallback> dbOperationCallback;

        public InsertLocationUpdatesAsyncTask(DbOperationCallback dbOperationCallback) {
            this.dbOperationCallback = new WeakReference<>(dbOperationCallback);
        }

        @Override
        protected Long doInBackground(RideLocationUpdates... locationUpdates) {
            return db.rideLocationUpdatesDao().insertRideLocationUpdates(locationUpdates[0]);
        }

        @Override
        protected void onPostExecute(Long value) {
            super.onPostExecute(value);
            DbOperationCallback dbOperationCallback = this.dbOperationCallback.get();
            if (dbOperationCallback != null)
                dbOperationCallback.onInsertLocationUpdate(value);

        }
    }

    private class UpdateRideDetailsAsyncTask extends AsyncTask<RideDetails, Void, Void> {
        private WeakReference<DbOperationCallback> dbOperationCallback;

        public UpdateRideDetailsAsyncTask(DbOperationCallback dbOperationCallback) {
            this.dbOperationCallback = new WeakReference<>(dbOperationCallback);
        }


        @Override
        protected Void doInBackground(RideDetails... rideDetails) {
             db.rideDetailsDao().updateRideDetails(rideDetails[0].endLatitude,
                     rideDetails[0].endLongitude,
                     rideDetails[0].distanceCovered,
                     rideDetails[0].id);

             return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            DbOperationCallback dbOperationCallback = this.dbOperationCallback.get();
            if (dbOperationCallback != null)
                dbOperationCallback.onUpdateRide();
        }
    }*/

    public interface DbOperationCallback {
        void onInsertRide(long insertedRowId);
        void onInsertLocationUpdate(long insertedRowId);
        void onUpdateRide();
    }
}
