package com.example.journeytracking.RideHistoryModule.RideDetailsModule;

import android.os.SystemClock;

import com.android.volley.VolleyError;
import com.example.journeytracking.Data.RideDetails;
import com.example.journeytracking.Data.RideLocationUpdates;
import com.example.journeytracking.Utils.ApiRequestManager;
import com.example.journeytracking.Utils.DbManager;
import com.example.journeytracking.Utils.GoogleApiRequestBuilder;
import com.example.journeytracking.Utils.VolleySeverRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.example.journeytracking.Utils.CONSTANTS.VolleyRequestTags.SNAP_TO_ROAD;

public class RideDetailPresenter implements RideDetailContract.Presenter {
    //fragment instance to initiate callbacks
    private RideDetailContract.View mView;
    //db manager to execute queries
    private DbManager dbManager;
    //api manager to send get requests using volley
    private ApiRequestManager apiRequestManager;

    public RideDetailPresenter(RideDetailContract.View mView, DbManager dbManager, ApiRequestManager apiRequestManager) {
        this.mView = mView;
        this.dbManager = dbManager;
        this.apiRequestManager = apiRequestManager;
    }

    //get all the locations of ride with the given ride id
    @Override
    public void getRideLocationList(Long rideId) {
        dbManager.getRideLocations(rideId, new DbOperationCallbackImpl(mView));
    }

    //snap all the locations received to roads using the road api
    @Override
    public void snapToRoad(ArrayList<RideLocationUpdates> rideLocationUpdatesList, String apiKey) {

        //since a request to the road API can only contain 100 locations
        //it is broken down into separate requests
        ArrayList<String> urls = GoogleApiRequestBuilder.snapToRoadRequestBuilder(rideLocationUpdatesList, apiKey);
        //number of expected responses = number of requests made
        //since a request to the road API can only contain 100 locations
        //it is broken down into separate requests
        //this fields helps to keep count of number of responses we are expecting
        int numberOfResponses = urls.size();
        for (String url : urls) {
            apiRequestManager.snapToRoad(url, SNAP_TO_ROAD, new VolleyRequestCallbackImpl(mView, numberOfResponses));
            SystemClock.sleep(700);
        }
    }

    @Override
    public void onDetach() {
        mView = null;
        apiRequestManager.cancelAll(SNAP_TO_ROAD);
    }

    //Volley callback implementation
    private static class VolleyRequestCallbackImpl implements VolleySeverRequest.VolleyResponseCallback {
        //Weak reference to our fragment to initiate callbacks
        private WeakReference<RideDetailContract.View> mView;
        //expected number of responses
        //used to locate the final response
        private int expectedNumberOfResponses;
        //number of responses received
        private static int actualNumberOfResponses = 0;

         VolleyRequestCallbackImpl(RideDetailContract.View view, int expectedNumberOfResponses) {
            this.mView = new WeakReference<>(view);
            this.expectedNumberOfResponses = expectedNumberOfResponses;
        }

        @Override
        public void onSuccess(JSONObject response) {

            //parse the json result
            try {
                actualNumberOfResponses++;
                ArrayList<RideLocationUpdates> snappedPointsList = new ArrayList<>();
                JSONArray snappedPointsArray = response.getJSONArray("snappedPoints");
                JSONObject jsonObject;
                JSONObject locationObject;

                for (int i = 0; i < snappedPointsArray.length(); i++) {
                    jsonObject = snappedPointsArray.getJSONObject(i);
                    locationObject = jsonObject.getJSONObject("location");
                    double latitude = locationObject.getDouble("latitude");
                    double longitude = locationObject.getDouble("longitude");
                    RideLocationUpdates object = new RideLocationUpdates(0, latitude, longitude);
                    snappedPointsList.add(object);
                }

                //once we have the snapped locations, initiate a callback to the fragment
                // based on whether the response is last or not
                RideDetailContract.View view = mView.get();
                if (view != null) {
                    if (actualNumberOfResponses == expectedNumberOfResponses) {
                        actualNumberOfResponses = 0;
                        view.snappedPointsFinalResult(snappedPointsList);
                    }
                    else
                        view.snappedPointsBuildResult(snappedPointsList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFail(VolleyError error) {
             actualNumberOfResponses++;
             if(actualNumberOfResponses == expectedNumberOfResponses)
                 actualNumberOfResponses = 0;
        }
    }

    //Callback implementation for database operations
    private static class DbOperationCallbackImpl implements DbManager.DbOperationCallback {
        private WeakReference<RideDetailContract.View> mView;

         DbOperationCallbackImpl(RideDetailContract.View view) {
            this.mView = new WeakReference<>(view);
        }

        @Override
        public void onRideLocationsListFetched(ArrayList<RideLocationUpdates> rideLocationsList) {
            RideDetailContract.View view = mView.get();
            if (view != null) {
                view.onRideLocationListFetched(rideLocationsList);
            }
        }

        @Override
        public void onRideListFetched(ArrayList<RideDetails> rideDetailsList) {

        }

        @Override
        public void onInsertRide(long insertedRowId) {
        }

        @Override
        public void onInsertLocationUpdate(long insertedRowId) {
        }

        @Override
        public void onUpdateRide() {

        }

    }
}
