package com.example.journeytracking.TrackingModule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.journeytracking.AppController;
import com.example.journeytracking.BuildConfig;
import com.example.journeytracking.R;
import com.example.journeytracking.RideHistoryModule.RideHistoryActivity;
import com.example.journeytracking.Service.TrackingService;
import com.example.journeytracking.Utils.CONSTANTS;
import com.example.journeytracking.Utils.DbManager;
import com.example.journeytracking.Utils.SharedPrefManager;
import com.example.journeytracking.Utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

/**
 * This app uses a long-running bound and started service for location updates. The service is
 *  * aware of foreground status of this activity, which is the only bound client in
 *  * this sample. After requesting location updates, when the activity ceases to be in the foreground,
 *  * the service promotes itself to a foreground service and continues receiving location updates.
 *  * When the activity comes back to the foreground, the foreground service stops, and the
 *  * notification associated with that foreground service is removed.
 *  *
 *  * While the foreground service notification is displayed, the user has the option to launch the
 *  * activity from the notification.
 *
 *  Note: Users have three options regarding location:
 *  * <ul>
 *  *     <li>Allow all the time</li>
 *  *     <li>Allow while app is in use, i.e., while app is in foreground</li>
 *  *     <li>Not allow location at all</li>
 *  * </ul>
 *  * Because this app creates a foreground service (tied to a Notification) when the user navigates
 *  * away from the app, it only needs location "while in use." That is, there is no need to ask for
 *  * location all the time (which requires additional permissions in the manifest).
 */

public class TrackingActivity extends TrackingPermissionManager implements OnMapReadyCallback,
        TrackingContract.View, TrackingService.TrackingActivityCallback {

    private static final String TAG = "TrackingActivity";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    //enum to toggle button and view states based on the user actions
    private enum StateSelector {RIDE_START, RIDE_STOP, DISABLE_ALL, ENABLE_ALL, DEFAULT}


    private GoogleMap mMap;
    //presenter object for our tracking activity
    private TrackingPresenter mPresenter;
    //instance of the service responsible for providing location updates
    private TrackingService trackingService;
    //intent to connect to the service
    private Intent serviceIntent;
    //service connection object used to bind and unbind from the tracking service
    private TrackingServiceConnection serviceConnection;
    //boolean variable to check if the service is bound or not
    private boolean mBound = false;
    private SharedPrefManager sharedPrefManager;

    //Ui elements
    private MaterialButton startTrackingButton;
    private MaterialButton stopTrackingButton;
    private MaterialButton rideHistoryButton;
    private TextView distanceCoveredTextView;
    private TextView timerTextView;
    private Group timeDistanceGroup;

    //MArkers to be displayed on the map
    private Marker startLocationMarker;
    private Marker currentLocationMarker;

    //stores the current ride id
    private long currentRideId;
    //stores the distance covered in the ongoing journey
    private double distanceCovered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        //we pass an instance of the room database as presenter is used to perform queries on it
        mPresenter = new TrackingPresenter(this, new DbManager(AppController.getInstance().getRideDatabaseInstance()));
        sharedPrefManager = new SharedPrefManager(getApplicationContext());
        //shared preference value to check if the ride has started or not
        sharedPrefManager.setRideStarted(false);

        //bind ui buttons
        startTrackingButton = this.findViewById(R.id.buttonStartTracking);
        stopTrackingButton = this.findViewById(R.id.buttonStopTracking);
        rideHistoryButton = this.findViewById(R.id.buttonRideHistory);
        distanceCoveredTextView = this.findViewById(R.id.textViewDistance);
        timerTextView = this.findViewById(R.id.textViewTimer);
        timeDistanceGroup = this.findViewById(R.id.viewGroupTimeDistance);

        //set listeners
        startTrackingButton.setOnClickListener(new StartTrackingClickListener());
        stopTrackingButton.setOnClickListener(new StopTrackingClickListener());
        rideHistoryButton.setOnClickListener(new RideHistoryClickListener());

        serviceIntent = new Intent(getApplicationContext(), TrackingService.class);
        serviceConnection = new TrackingServiceConnection();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    //objects of this class are used to bind/unbind to the service
    private class TrackingServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
               //get the instance of the service from the binder object
                trackingService = ((TrackingService.LocalBinder) service).getService();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }

            //once the service is bound set the boolean variable and start receiving location updates
            mBound = true;
            startReceivingUpdates();
        }

        //this method is called if the service is killed by the system for some reason
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            trackingService = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //check if the user has location settings enabled
        checkLocationSettings();

        //if the activity is not bound to the service bind it
        //this comes in handy when the app is returning from another activity after stopping a ride
        if (!mBound && serviceConnection != null)
            bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        //if we are bound to the service, then unbind
        if (mBound && serviceConnection != null) {
            unbindService(serviceConnection);
            mBound = false;
            trackingService = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //remove the reference to the activity from the presenter
        //in case some object has a weak reference to the activity, then it wont be able to access it
        mPresenter.onDetach();
    }

    //callback function for when map is ready to be manipualted
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    /*
     * Listener class for the start button
     * */
    private class StartTrackingClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //once a ride is started it has to be replaced by the stop button
            //stop button is displayed once the database has been updated
            startTrackingButton.setEnabled(false);

            //if we have received a starting location put it into the databse as our start location
            //in case we haven't been receiving locations the start location marker will be null and the ride wont start
            if (startLocationMarker != null) {
                mPresenter.insertNewRide(startLocationMarker.getPosition(), false, Utils.getDateTime());
                trackingService.removeLocationUpdates();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.location_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
     * Listener for stop button
     * */
    private class StopTrackingClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            //once the ride has been stopped it has to be replaced by the start button
            //start button is displayed once the database has been updated
            stopTrackingButton.setEnabled(false);

            //stop receiving location updated from the service
            trackingService.removeLocationUpdates();

            //update the current ride with final location, time and distance (stored in kms)
            mPresenter.updateCurrentRide(currentLocationMarker.getPosition(), distanceCovered / 1000, true, Utils.getDateTime(), currentRideId);
        }
    }

    /*
     * Listener for Ride History Button
     * */
    private class RideHistoryClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            //if no ride is in progress stop receiving location updates
            if (!sharedPrefManager.isRideStarted())
                trackingService.removeLocationUpdates();

            Intent intent = new Intent(TrackingActivity.this, RideHistoryActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Presenter Callback Methods
     *
     */

    //callback method from the presenter
    //called when  a new ride has been inserted into the db
    @Override
    public void onRideInserted(long insertedRowId) {
        currentRideId = insertedRowId;
        sharedPrefManager.setRideStarted(true);

        setButtonState(StateSelector.RIDE_START);
        setViewState(StateSelector.RIDE_START);

        //once the ride has started, initiate the timer to be displayed on the screen
        mPresenter.startTimer();
        startReceivingUpdates();

    }

    //callback method from the presenter
    //called when a new location update for the current ride is inserted into the db
    @Override
    public void onLocationUpdateInserted() {
        String distanceToDisplay = String.format("%.2f", distanceCovered / 1000);
        distanceCoveredTextView.setText(distanceToDisplay);
    }

    //callback method from the presenter
    //called when the details pertaining to the ending of a ride are inserted into the db
    @Override
    public void onRideUpdated() {
        setViewState(StateSelector.RIDE_STOP);
        setButtonState(StateSelector.RIDE_STOP);
        sharedPrefManager.setRideStarted(false);

        Intent intent = new Intent(this, RideHistoryActivity.class);
        startActivity(intent);
    }

    //callback method from the presenter
    //called every second to update the timer vie
    @Override
    public void updateTimeElapsed(String time) {
        timerTextView.setText(time);
    }

    /**
     * Service Callback Methods
     */

    //callback method from the service
    //this is called every time user changes the location settings
    @Override
    public void checkGpsSettings() {
        checkLocationSettings();
    }

    //callback method from service
    //this is called every time a new location is received
    @Override
    public void onNewLocationReceived(Location location) {
        if (location != null) {
            Log.d(TAG, "onReceive: " + location.getLatitude() + "  " + location.getLongitude());

            //if a ride is in progress insert the new location into the database and update the distance travelled
            if (sharedPrefManager.isRideStarted()) {
                updateDistanceCovered(location);
                mPresenter.insertLocationUpdate(location, currentRideId);
            }

            //set the markers on the map based on the new location
            setMarkers(location);
        }
    }



    //for each new location increment the distance covered by the distance between the old and new lcoation
    private void updateDistanceCovered(Location location) {
        if (currentLocationMarker != null) {
            Location previousLocation = new Location("");
            previousLocation.setLatitude(currentLocationMarker.getPosition().latitude);
            previousLocation.setLongitude(currentLocationMarker.getPosition().longitude);
            //distance to method return the distance between two points on the map
            distanceCovered += (previousLocation.distanceTo(location));
        }
    }

    //utility function to set markers on the map based on the new locations received
    private void setMarkers(Location location) {

        //if a ride is in progress update the current marker but do not change the start marker
        if (sharedPrefManager.isRideStarted()) {
            LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
            //check if we are receiving the locationfor the first time or not
            if (currentLocationMarker != null)
                currentLocationMarker.setPosition(coordinates);
            else
                currentLocationMarker = mMap.addMarker(new MarkerOptions().position(coordinates));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(coordinates));

        } else {
            LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
            //check if we are receiving the locationfor the first time or not
            if (startLocationMarker != null) {
                startLocationMarker.setPosition(coordinates);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(coordinates));
            } else {
                startLocationMarker = mMap.addMarker(new MarkerOptions()
                        .position(coordinates)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 16));
            }


        }
    }

    //checks for permissions every time before requesting location updates from the service
    private void startReceivingUpdates() {
        if (!checkPermissions())
            requestPermissions();
        else
            trackingService.requestLocationUpdates(TrackingActivity.this);
    }

    //utility function to toggle button states based on user interaction
    private void setButtonState(StateSelector state) {
        switch (state) {
            case RIDE_START:
                startTrackingButton.setVisibility(View.GONE);
                stopTrackingButton.setVisibility(View.VISIBLE);
                startTrackingButton.setEnabled(true);
                break;

            case RIDE_STOP:
                stopTrackingButton.setVisibility(View.GONE);
                startTrackingButton.setVisibility(View.VISIBLE);
                stopTrackingButton.setEnabled(true);
                break;

            case DISABLE_ALL:
                startTrackingButton.setEnabled(false);
                stopTrackingButton.setEnabled(false);
                rideHistoryButton.setEnabled(false);
                break;

            case ENABLE_ALL:
                startTrackingButton.setEnabled(true);
                stopTrackingButton.setEnabled(true);
                rideHistoryButton.setEnabled(true);
                break;

            case DEFAULT:
        }
    }

    //utility function to toggle view states based on user interaction
    private void setViewState(StateSelector state) {
        switch (state) {
            case RIDE_START:
                //time distance group consists of the timer and distance text view
                //they are hidden when a ride is not in progress and vice versa
                timeDistanceGroup.setVisibility(View.VISIBLE);
                break;

            case RIDE_STOP:
                //reset al field objects to make it ready for the next ride
                timeDistanceGroup.setVisibility(View.GONE);
                mPresenter.resetTimer();
                distanceCovered = 0;
                mMap.clear();
                currentLocationMarker = null;
                startLocationMarker = null;
                break;
        }
    }

    /*Callback received when a permissions request has been completed.
     * Permission are requested in the base class for this activity TrackingPermissionManger.class*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.d(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                trackingService.requestLocationUpdates(TrackingActivity.this);

            } else {

                // Permission denied.
                Snackbar.make(
                        findViewById(R.id.constraintLayoutTrackingActivity),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    //results for location settings are handled here
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        // All required changes were successfully made
                        CONSTANTS.locationSettingRequestCount = 0;
                        break;

                    case RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        CONSTANTS.locationSettingRequestCount = 0;
                        checkLocationSettings();
                        break;

                    default:
                        break;

                }
                break;
        }
    }
}
