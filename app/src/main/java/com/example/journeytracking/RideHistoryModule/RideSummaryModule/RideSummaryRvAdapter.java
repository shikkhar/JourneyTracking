package com.example.journeytracking.RideHistoryModule.RideSummaryModule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.journeytracking.Data.RideDetails;
import com.example.journeytracking.R;
import com.example.journeytracking.Utils.GoogleApiRequestBuilder;
import com.example.journeytracking.Utils.Utils;

import java.text.ParseException;
import java.util.List;

public class RideSummaryRvAdapter extends RecyclerView.Adapter<RideSummaryRvAdapter.MyViewHolder> {

    //list to feed data to the recycler view
    private List<RideDetails> rideDetailsList;
    //fragment instance to implement callbacks and for context
    private RideSummaryFragment containerFragment;

    public RideSummaryRvAdapter(List<RideDetails> rideDetailsList, RideSummaryFragment containerFragment) {
        this.rideDetailsList = rideDetailsList;
        this.containerFragment = containerFragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ride_summary, parent, false);
        return new MyViewHolder(view, new ItemClickListener());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //update the position in the click listener
        holder.itemClickListener.setPosition(position);
        //get the corresponding list value
        RideDetails rideDetails = rideDetailsList.get(position);

        //build a GET request to get static map with two markers to be displayed in the image view
        String request = GoogleApiRequestBuilder.staticMapMarkersRequest(rideDetails, containerFragment.getString(R.string.google_maps_key));
        Glide.with(containerFragment.getContext().getApplicationContext())
                .load(request)
                .placeholder(R.drawable.default_image)
                .error(R.drawable.error)
                .into(holder.rideImageView);

        holder.rideId.setText(String.valueOf(rideDetails.id));
        holder.distance.setText(String.format("%.2f", rideDetails.distanceCovered));
        try {
            holder.rideDate.setText(Utils.getDate(rideDetails.startTime));
            holder.startTime.setText(Utils.getTime(rideDetails.startTime));
            holder.endTime.setText(Utils.getTime(rideDetails.endTime));
        } catch (ParseException e){
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return rideDetailsList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView rideImageView;
        private TextView rideId;
        private TextView rideDate;
        private TextView startTime;
        private TextView endTime;
        private TextView distance;
        //click listener for the items in recycler view
        private ItemClickListener itemClickListener;

         MyViewHolder(@NonNull View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            rideImageView = itemView.findViewById(R.id.imageViewRide);
            rideId = itemView.findViewById(R.id.textViewRideId);
            rideDate = itemView.findViewById(R.id.textViewDate);
            startTime = itemView.findViewById(R.id.textViewStartTime);
            endTime = itemView.findViewById(R.id.textViewEndTime);
            distance = itemView.findViewById(R.id.textViewDistance);
            this.itemClickListener = itemClickListener;
            itemView.setOnClickListener(itemClickListener);
        }
    }

    private class ItemClickListener implements View.OnClickListener{
        //every time a view holder is bound to a new view position is updated
        //so we can fetch the correct item from the list
        private int position;

        @Override
        public void onClick(View v) {
            if(containerFragment != null)
                containerFragment.rvItemClicked(position);
        }

         void setPosition(int position) {
            this.position = position;
        }
    }
}
