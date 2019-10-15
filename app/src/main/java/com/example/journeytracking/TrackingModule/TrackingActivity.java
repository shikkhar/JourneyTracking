package com.example.journeytracking.TrackingModule;

import androidx.annotation.NonNull;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.journeytracking.AppController;
import com.example.journeytracking.R;
import com.example.journeytracking.RideHistoryModule.RideHistoryActivity;
import com.example.journeytracking.Service.TrackingService;
import com.example.journeytracking.Utils.DbManager;
import com.example.journeytracking.Utils.SharedPrefManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;


public class TrackingActivity extends TrackingPermissionManager implements OnMapReadyCallback,
        TrackingContract.View, TrackingService.NewLocationCallback {

    private enum StateSelector {RIDE_START, RIDE_STOP, DEFAULT}


    private static final String TAG = "TrackingActivity";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private TrackingPresenter mPresenter;
    private TrackingService trackingService;
    private Intent serviceIntent;
    private TrackingServiceConnection serviceConnection;
    private boolean mBound = false;
    private SharedPrefManager sharedPrefManager;

    private MaterialButton startTrackingButton;
    private MaterialButton stopTrackingButton;
    private TextView distanceCoveredTextView;
    private TextView timerTextView;


    private Marker startLocationMarker;
    private Marker currentLocationMarker;


    private long currentRideId;
    private double distanceCovered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        mPresenter = new TrackingPresenter(this, new DbManager(AppController.getInstance().getRideDatabaseInstance()));
//        myBroadcastReceiver = new MyBroadcastReceiver();
//        appLocationManager = new AppLocationManager(this);
        sharedPrefManager = new SharedPrefManager(getApplicationContext());
        sharedPrefManager.setRideStarted(false);

        startTrackingButton = this.findViewById(R.id.buttonStartTracking);
        stopTrackingButton = this.findViewById(R.id.buttonStopTracking);
        distanceCoveredTextView = this.findViewById(R.id.textViewDistance);
        timerTextView = this.findViewById(R.id.textViewTimer);

        startTrackingButton.setOnClickListener(new StartTrackingClickListener());
        stopTrackingButton.setOnClickListener(new StopTrackingClickListener());

        serviceIntent = new Intent(getApplicationContext(), TrackingService.class);
        serviceConnection = new TrackingServiceConnection();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private class StartTrackingClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startTrackingButton.setEnabled(false);

            if (startLocationMarker != null) {
                mPresenter.insertNewRide(startLocationMarker.getPosition());
                trackingService.removeLocationUpdates();
            }
        }
    }



    private class StopTrackingClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            stopTrackingButton.setEnabled(false);
            trackingService.removeLocationUpdates();
            mPresenter.updateCurrentRide(currentLocationMarker.getPosition(), distanceCovered, true, currentRideId);
            //locationUpdatesRequested = false;
        }
    }

    private class TrackingServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //TODO: throw class cast exception
            trackingService = ((TrackingService.LocalBinder) service).getService();
            mBound = true;

            startReceivingUpdates();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            trackingService = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBound && serviceConnection != null)
            bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
        /*else {
            if (!sharedPrefManager.isRideStarted())
                startReceivingUpdates();
        }*/
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mBound && serviceConnection != null) {
            unbindService(serviceConnection);
            mBound = false;
            trackingService = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }


    /**
     * Callback received when a permissions request has been completed.
     */
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
                /*//TODO: implement
                // Permission denied.
                setButtonsState(false);
                Snackbar.make(
                        findViewById(R.id.activity_main),
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
                        .show();*/
            }
        }
    }



    //TODO: move this to the presenter
    private void updateDistanceCovered(Location location) {
        if (currentLocationMarker != null) {
            Location previousLocation = new Location("");
            previousLocation.setLatitude(currentLocationMarker.getPosition().latitude);
            previousLocation.setLongitude(currentLocationMarker.getPosition().longitude);
            distanceCovered += previousLocation.distanceTo(location);
        }
    }

    @Override
    public void onNewLocationReceived(Location location) {
        if (location != null) {
            Log.d(TAG, "onReceive: " + location.getLatitude() + "  " + location.getLongitude());
            if (sharedPrefManager.isRideStarted()) {
                mPresenter.insertLocationUpdate(location, currentRideId);
                updateDistanceCovered(location);
            }
            setMarkers(location);
        }
    }

    private void setMarkers(Location location) {

        if (sharedPrefManager.isRideStarted()) {

            LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
            if (currentLocationMarker != null)
                currentLocationMarker.setPosition(coordinates);
            else
                currentLocationMarker = mMap.addMarker(new MarkerOptions().position(coordinates).title("Current"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));

        } else {
            //TODO: get current zoom level of the map and use that value in every moveCameraCall
            LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
            if (startLocationMarker != null)
                startLocationMarker.setPosition(coordinates);
            else
                startLocationMarker = mMap.addMarker(new MarkerOptions()
                        .position(coordinates)
                        .title("Start")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 14));
        }
    }

    private void startReceivingUpdates(){
        if (!checkPermissions())
            requestPermissions();
        else
            trackingService.requestLocationUpdates(TrackingActivity.this);
    }

    @Override
    public void onRideInserted(long insertedRowId) {
        currentRideId = insertedRowId;
        sharedPrefManager.setRideStarted(true);

        setButtonState(StateSelector.RIDE_START);
        setViewState(StateSelector.RIDE_START);

        startReceivingUpdates();

    }

    @Override
    public void onLocationUpdateInserted() {
        String distanceToDisplay = String.format("%.2f", distanceCovered);
        distanceCoveredTextView.setText(distanceToDisplay);
    }

    @Override
    public void onRideUpdated() {
        setViewState(StateSelector.RIDE_STOP);
        setButtonState(StateSelector.RIDE_STOP);
        sharedPrefManager.setRideStarted(false);

        Intent intent = new Intent(this, RideHistoryActivity.class);
        startActivity(intent);
    }

    private void setButtonState(StateSelector state) {
        switch(state){
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

            case DEFAULT:
        }
    }

    private void setViewState(StateSelector state) {
        switch(state){
            case RIDE_START:
                timerTextView.setVisibility(View.VISIBLE);
                distanceCoveredTextView.setVisibility(View.VISIBLE);
                break;

            case RIDE_STOP:
                timerTextView.setVisibility(View.GONE);
                distanceCoveredTextView.setVisibility(View.GONE);
                distanceCovered = 0;
                mMap.clear();
                currentLocationMarker = null;
                startLocationMarker = null;
                break;
        }
    }
}
