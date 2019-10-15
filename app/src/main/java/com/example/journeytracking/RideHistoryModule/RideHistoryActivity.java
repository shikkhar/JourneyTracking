package com.example.journeytracking.RideHistoryModule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.journeytracking.R;
import com.example.journeytracking.RideHistoryModule.RideSummaryModule.RideSummaryFragment;

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
}
