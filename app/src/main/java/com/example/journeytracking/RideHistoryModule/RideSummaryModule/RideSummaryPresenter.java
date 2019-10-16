package com.example.journeytracking.RideHistoryModule.RideSummaryModule;

import com.example.journeytracking.Data.RideDetails;
import com.example.journeytracking.Data.RideLocationUpdates;
import com.example.journeytracking.Utils.DbManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class RideSummaryPresenter implements RideSummaryContract.Presenter {
    //fragment instance to implement callbacks
    private RideSummaryContract.View mView;
    //instance of db manager to execute queries
    private DbManager dbManager;

    public RideSummaryPresenter(RideSummaryContract.View mView, DbManager dbManager) {
        this.mView = mView;
        this.dbManager = dbManager;
    }

    //function to fetch all the rides that have been completed
    @Override
    public void getRideDetailsList() {
        dbManager.getRideDetails(new DbOperationCallbackImpl(mView));
    }

    @Override
    public void onDetach() {
        mView = null;
    }

    //static inner class to implement Database operations callbacks
    //called whenever a query returns a result
    //holds a weak reference to our fragment to initiate callbacks to the fragment class
    private static class DbOperationCallbackImpl implements DbManager.DbOperationCallback{
        //Weak reference to avoid memory leaks
        private WeakReference<RideSummaryContract.View> mView;

         DbOperationCallbackImpl(RideSummaryContract.View view) {
            this.mView = new WeakReference<>(view);
        }

        @Override
        public void onRideListFetched(ArrayList<RideDetails> rideList) {
            RideSummaryContract.View view = mView.get();
            if(view != null){
                view.rideListFetched(rideList);
            }
        }

        @Override
        public void onInsertRide(long insertedRowId) {
        }

        @Override
        public void onInsertLocationUpdate(long insertedRowId) {
        }

        @Override
        public void onUpdateRide() {

        }

        @Override
        public void onRideLocationsListFetched(ArrayList<RideLocationUpdates> rideLocationsList) {

        }
    }
}
