package com.example.journeytracking.RideHistoryModule.RideSummaryModule;

import com.example.journeytracking.Data.RideDetails;
import com.example.journeytracking.TrackingModule.TrackingContract;
import com.example.journeytracking.Utils.DbManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class RideSummaryPresenter implements RideSummaryContract.Presenter {
    private RideSummaryContract.View mView;
    private DbManager dbManager;

    public RideSummaryPresenter(RideSummaryContract.View mView, DbManager dbManager) {
        this.mView = mView;
        this.dbManager = dbManager;
    }

    @Override
    public void getRideDetailsList() {
        dbManager.getRideDetails(new DbOperationCallbackImpl(mView));
    }

    private static class DbOperationCallbackImpl implements DbManager.DbOperationCallback{
        private WeakReference<RideSummaryContract.View> mView;

        public DbOperationCallbackImpl(RideSummaryContract.View view) {
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

    }
}
