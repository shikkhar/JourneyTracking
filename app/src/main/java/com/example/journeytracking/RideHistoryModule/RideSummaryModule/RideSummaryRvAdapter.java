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
import com.example.journeytracking.Utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class RideSummaryRvAdapter extends RecyclerView.Adapter<RideSummaryRvAdapter.MyViewHolder> {

    private List<RideDetails> rideDetailsList;
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
        holder.itemClickListener.setPosition(position);
        RideDetails rideDetails = rideDetailsList.get(position);

        String request = buildRequest(rideDetails);
        Glide.with(containerFragment.getContext().getApplicationContext())
                .load(request)
                .placeholder(R.drawable.default_image)
                .error(R.drawable.error)
                .into(holder.rideImageView);

        holder.rideId.setText(String.valueOf(rideDetails.id));
        holder.distance.setText(String.valueOf(rideDetails.distanceCovered));
        try {
            holder.rideDate.setText(Utils.getDate(rideDetails.startTime));
            holder.startTime.setText(Utils.getTime(rideDetails.startTime));
            holder.endTime.setText(Utils.getTime(rideDetails.endTime));
        } catch (ParseException e){
            e.printStackTrace();
        }


    }

    private String buildRequest(RideDetails rideDetails) {
        String size = "size=100x100";
        String startMarker = "&markers=size:tiny|color:green|" + rideDetails.startLatitude +"," + rideDetails.startLongitude;
        String endMarker = "&markers=size:mid|color:red|" + rideDetails.endLatitude +"," + rideDetails.endLongitude;
        String request = "https://maps.googleapis.com/maps/api/staticmap?" +size+startMarker+endMarker+containerFragment.getString(R.string.google_maps_key);
        return request;
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
        private ItemClickListener itemClickListener;

        public MyViewHolder(@NonNull View itemView, ItemClickListener itemClickListener) {
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

        private int position;

        @Override
        public void onClick(View v) {
            if(containerFragment != null)
                containerFragment.rvItemClicked(position);
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}
