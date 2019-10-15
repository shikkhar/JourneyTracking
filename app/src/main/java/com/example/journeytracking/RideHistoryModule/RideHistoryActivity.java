package com.example.journeytracking.RideHistoryModule;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.journeytracking.R;

public class RideHistoryActivity extends AppCompatActivity implements RideHistoryContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history);
    }
}
