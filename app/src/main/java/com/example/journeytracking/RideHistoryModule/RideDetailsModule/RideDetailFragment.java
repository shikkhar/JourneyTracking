package com.example.journeytracking.RideHistoryModule.RideDetailsModule;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.journeytracking.AppController;
import com.example.journeytracking.Data.RideDetails;
import com.example.journeytracking.Data.RideLocationUpdates;
import com.example.journeytracking.R;
import com.example.journeytracking.Utils.ApiRequestManager;
import com.example.journeytracking.Utils.DbManager;
import com.example.journeytracking.Utils.GoogleApiRequestBuilder;
import com.example.journeytracking.Utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Fragment to display the details of the ride clicked in the recycler view in the previous fragment
 */
public class RideDetailFragment extends Fragment implements RideDetailContract.View {

    //presenter for this fragment
    private RideDetailContract.Presenter mPresenter;
    //object that was clicked in the previous fragment will be stored in this object
    private RideDetails rideDetails;

    //UI
    private ImageView rideDetailImageView;
    private TextView rideIdTextView;
    private TextView totalTimeTextView;
    private TextView totalDistanceTextView;

    //list of all the locations of a ride
    private ArrayList<RideLocationUpdates> snappedLocationUpdates;

    public RideDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();

        //get the item selected in the recycler view from the arguments
        if(bundle != null && bundle.containsKey("rideDetails")){
            rideDetails = bundle.getParcelable("rideDetails");
        }

        snappedLocationUpdates = new ArrayList<>();
        mPresenter = new RideDetailPresenter(this, new DbManager(AppController.getInstance().getRideDatabaseInstance()), new ApiRequestManager());
        //get a list of all the locations of a ride
        mPresenter.getRideLocationList(rideDetails.id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ride_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //bind the views
        rideDetailImageView = view.findViewById(R.id.imageViewRideDetail);
        rideIdTextView = view.findViewById(R.id.textViewRideId);
        totalTimeTextView = view.findViewById(R.id.textViewTimeTaken);
        totalDistanceTextView = view.findViewById(R.id.textViewDistance);

        try {
            updateViewValues();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateViewValues() throws ParseException {
        String totalTime = Utils.getTimeDifference(rideDetails.startTime, rideDetails.endTime);
        rideIdTextView.setText(String.valueOf(rideDetails.id));
        totalDistanceTextView.setText(String.format("%.2f", rideDetails.distanceCovered));
        totalTimeTextView.setText(totalTime);

    }

    //presenter callback method
    //called when all the locations of a ride have been fetched
    //once we have all the locations we are going to use the roads api to snap them to the respective roads
    // snapping to the roads provides a more uniform polyline which otherwise would not be possible due to fluctuating gps signals
    @Override
    public void onRideLocationListFetched(ArrayList<RideLocationUpdates> rideLocationUpdatesList) {
        mPresenter.snapToRoad(rideLocationUpdatesList, getString(R.string.google_maps_key));
    }

    //we can only send up to a 100 points in a request to the roads API
    //therefore the requests are broken down
    //and the results are then tied together to give the final static image
    @Override
    public void snappedPointsBuildResult(ArrayList<RideLocationUpdates> snappedPointsList) {
        this.snappedLocationUpdates.addAll(snappedPointsList);
    }

    //last result from the roads api
    @Override
    public void snappedPointsFinalResult(ArrayList<RideLocationUpdates> snappedPointsList) {
        this.snappedLocationUpdates.addAll(snappedPointsList);

        String imageHeight = String.valueOf(Utils.getDisplayHeight(getContext().getApplicationContext()));
        String imageWidth = String.valueOf(Utils.getDisplayWidth(getContext().getApplicationContext()));

        //build a request to get a static map with polyline path
        //right now return a map of max size 64x640 due to account restrictions
        String request = GoogleApiRequestBuilder.staticMapPolyLineRequestBuilder(imageWidth, imageHeight, rideDetails, snappedLocationUpdates, this.getString(R.string.google_maps_key));

        Glide.with(getContext().getApplicationContext())
                .load(request)
                .placeholder(R.drawable.default_image)
                .error(R.drawable.error)
                .into(rideDetailImageView);

        this.snappedLocationUpdates.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }
}
