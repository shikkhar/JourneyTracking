package com.example.journeytracking.RideHistoryModule.RideSummaryModule;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.journeytracking.AppController;
import com.example.journeytracking.Data.RideDetails;
import com.example.journeytracking.R;
import com.example.journeytracking.RideHistoryModule.RideHistoryActivity;
import com.example.journeytracking.Utils.DbManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RideSummaryFragment extends Fragment implements RideSummaryContract.View {

    private List<RideDetails> rideDetailsList;
    private RecyclerView recyclerView;
    private RideSummaryRvAdapter mAdapter;
    private RideHistoryActivity parentActivity;
    private RideSummaryPresenter mPresenter;

    public RideSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            parentActivity = (RideHistoryActivity) context;
        } catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rideDetailsList = new ArrayList<>();
        mAdapter = new RideSummaryRvAdapter(rideDetailsList, this);
        mPresenter = new RideSummaryPresenter(this, new DbManager(AppController.getInstance().getRideDatabaseInstance()));
        mPresenter.getRideDetailsList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ride_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rvRideSummary);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
    }

    public void rvItemClicked(int position) {
    }

    @Override
    public void rideListFetched(ArrayList<RideDetails> rideList) {
        rideDetailsList.addAll(rideList);
        mAdapter.notifyDataSetChanged();
    }
}
