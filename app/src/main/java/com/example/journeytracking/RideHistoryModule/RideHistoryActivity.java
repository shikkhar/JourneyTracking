package com.example.journeytracking.RideHistoryModule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.journeytracking.Data.RideDetails;
import com.example.journeytracking.R;
import com.example.journeytracking.RideHistoryModule.RideDetailsModule.RideDetailFragment;
import com.example.journeytracking.RideHistoryModule.RideSummaryModule.RideSummaryFragment;

/**
 * Class holds two fragments
 * one to show ride summaries
 * and second to show the ride details
 */

public class RideHistoryActivity extends AppCompatActivity implements RideHistoryContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history);

        loadRideSummaryFragment();

    }

    private void loadRideSummaryFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment rideSummaryFragment = new RideSummaryFragment();
        fm.beginTransaction().add(R.id.frameRideHistory, rideSummaryFragment).commit();
    }

    //callback received from the fragment when a recycler view item is clicked
    //clicked item is passed to the next fragment as an argument
    public void rideSummaryItemClicked(RideDetails rideDetails) {
        //get the clicked object from the bundle
        Bundle bundle = new Bundle();
        bundle.putParcelable("rideDetails", rideDetails);
        Fragment fragment = new RideDetailFragment();
        fragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //add the previous fragment to the back stack
        ft.replace(R.id.frameRideHistory, fragment).addToBackStack(null).commit();
    }
}
