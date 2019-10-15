package com.example.journeytracking.RideHistoryModule.RideSummaryModule;

import com.example.journeytracking.Data.RideDetails;

import java.util.ArrayList;

public class RideSummaryContract {
    interface Presenter{
        void getRideDetailsList();
    }
    interface View{
        void rideListFetched(ArrayList<RideDetails> rideList);
    }
}
