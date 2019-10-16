package com.example.journeytracking.Utils;

import com.example.journeytracking.Data.RideDetails;
import com.example.journeytracking.Data.RideLocationUpdates;

import java.util.ArrayList;

/**
 * Class to build GET requests for google roads api and static maps
 */

public class GoogleApiRequestBuilder {
    private static final String ROADS_API_ENDPOINT = "https://roads.googleapis.com/v1/snapToRoads?path=";
    private static final String STATIC_MAP_API_ENDPOINT = "https://maps.googleapis.com/maps/api/staticmap?";

    //Request Builder for Snap to Road
    public static ArrayList<String> snapToRoadRequestBuilder(ArrayList<RideLocationUpdates> rideLocationUpdatesList, String apiKey){
        ArrayList<String> requestUrls = new ArrayList<>();
        StringBuilder path = new StringBuilder();

        //since number of location points in a request is limited to 100
        //we have to break down the requests
        int size = rideLocationUpdatesList.size();
        //number of requests needed based on location
        int numberOfRequests = (int) Math.ceil(size/100.00);

        String interpolate = "&interpolate=true";

        int k = 0;
        //build the url
        for(int i=0; i<numberOfRequests; i++){
            path.setLength(0);
            for(int j=i*100; j<((i+1)*100-1) && j<size-1; j++){
                path.append(rideLocationUpdatesList.get(j).latitude).append(",")
                        .append(rideLocationUpdatesList.get(j).longitude).append("|");
            }

            int position = size - 1;
            if((i+1)*99 < size)
                position = (i+1) *99;

            path.append(rideLocationUpdatesList.get(position).latitude)
                    .append(",")
                    .append(rideLocationUpdatesList.get(position).longitude)
                    .append(interpolate)
                    .append("&key=").append(apiKey);

            String result = ROADS_API_ENDPOINT + path.toString();
            requestUrls.add(result);
        }

        //return list of requests
        //number of items in this list = number of responses expected from the server
        return requestUrls;

    }

    //Request Builder for static Maps with polyline
    public static String staticMapPolyLineRequestBuilder(String imageWidth, String imageHeight, RideDetails rideDetails,
                                                 ArrayList<RideLocationUpdates> snappedLocationUpdates, String api){

        String size = "size=" + imageWidth + "x" + imageHeight;
        String startMarker = "&markers=size:small|color:green|" + rideDetails.startLatitude +"," + rideDetails.startLongitude;
        String endMarker = "&markers=size:mid|color:red|" + rideDetails.endLatitude +"," + rideDetails.endLongitude;


        String path = "&path=color:blue|weight:5|geodesic:true" + stringBuilder(snappedLocationUpdates);

        return STATIC_MAP_API_ENDPOINT + size  + startMarker + endMarker + path + "&key=" + api;
    }

    public static String staticMapMarkersRequest(RideDetails rideDetails, String api) {
        String size = "size=100x100";
        String scale = "&scale=2";
        String startMarker = "&markers=size:tiny|color:green|" + rideDetails.startLatitude +"," + rideDetails.startLongitude;
        String endMarker = "&markers=size:mid|color:red|" + rideDetails.endLatitude +"," + rideDetails.endLongitude;

        return STATIC_MAP_API_ENDPOINT +size+scale+startMarker+endMarker+"&key="+api;
    }

    private static String stringBuilder(ArrayList<RideLocationUpdates> snappedPointsList){

        StringBuilder result = new StringBuilder();
        int size = snappedPointsList.size();

        for (int i = 0; i < size - 1 ; i++) {
            result.append(snappedPointsList.get(i).latitude).append(",")
                    .append(snappedPointsList.get(i).longitude).append("|");
        }

        result.append(snappedPointsList.get(size-1).latitude).append(",")
                .append(snappedPointsList.get(size-1).longitude);

        return result.toString();
    }
}
