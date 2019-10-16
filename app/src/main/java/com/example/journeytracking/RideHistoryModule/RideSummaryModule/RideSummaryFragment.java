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
 * Used to display all the rides in a recycler view
 */
public class RideSummaryFragment extends Fragment implements RideSummaryContract.View {

    //list to feed data to the recycler view
    private List<RideDetails> rideDetailsList;
    private RecyclerView recyclerView;
    //adapter associated with our recycler view
    private RideSummaryRvAdapter mAdapter;
    //parent activity to launch the second fragment when a recycler view item is clicked
    private RideHistoryActivity parentActivity;
    //presenter for this fragment
    private RideSummaryPresenter mPresenter;

    public RideSummaryFragment() {
        // Required empty public constructor
    }

    //get the instacne of the activity for callbacks
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
        //fetch all the rides that have been completed
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

    //callback to the activity when a recycler view item is clicked
    //this method is called from the recycler view
    public void rvItemClicked(int position) {
        parentActivity.rideSummaryItemClicked(rideDetailsList.get(position));
    }

    //presenter callback method
    //called when all the rides have been fetched
    @Override
    public void rideListFetched(ArrayList<RideDetails> rideList) {
        rideDetailsList.addAll(rideList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }
}
