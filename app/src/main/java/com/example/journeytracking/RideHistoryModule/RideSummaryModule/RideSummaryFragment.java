package com.example.journeytracking.RideHistoryModule.RideSummaryModule;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.journeytracking.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RideSummaryFragment extends Fragment implements RideSummaryContract.View {


    public RideSummaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ride_summary, container, false);
    }

}
