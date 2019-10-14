package com.example.journeytracking.TrackingModule;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.example.journeytracking.AppController;
import com.example.journeytracking.R;
import com.example.journeytracking.Utils.AppLocationManager;
import com.example.journeytracking.Utils.DbManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

import static com.example.journeytracking.TrackingModule.TrackingService.ACTION_BROADCAST;
import static com.example.journeytracking.TrackingModule.TrackingService.CURRENT_LOCATION;
import static com.example.journeytracking.TrackingModule.TrackingService.END_LOCATION;


public class TrackingActivity extends FragmentActivity implements OnMapReadyCallback, TrackingContract.View {

    private static final String TAG = "TrackingActivity";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private TrackingPresenter mPresenter;
    private TrackingService trackingService;
    private Intent serviceIntent;
    private TrackingServiceConnection serviceConnection;
    private boolean mBound = false;
    private AppLocationManager appLocationManager;

    private MaterialButton startTrackingButton;
    private MaterialButton stopTrackingButton;

    private MyBroadcastReceiver myBroadcastReceiver;

    private Marker startLocationMarker;
    private Marker currentLocationMarker;

    private boolean locationUpdatesRequested = false;

    private long currentRideId;
    private double distanceCovered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        mPresenter = new TrackingPresenter(this, new DbManager(AppController.getInstance().getRideDatabaseInstance()));
        myBroadcastReceiver = new MyBroadcastReceiver();
        appLocationManager = new AppLocationManager(this);

        startTrackingButton = this.findViewById(R.id.buttonStartTracking);
        stopTrackingButton = this.findViewById(R.id.buttonStopTracking);

        startTrackingButton.setOnClickListener(new StartTrackingClickListener());
        stopTrackingButton.setOnClickListener(new StopTrackingClickListener());

        serviceIntent = new Intent(getApplicationContext(), TrackingService.class);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private class StartTrackingClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
//            locationUpdatesRequested = true;
            distanceCovered = 0;
            mPresenter.insertNewRide(startLocationMarker.getPosition());
            trackingService.removeLocationUpdates();
            if (!checkPermissions()) {
                requestPermissions();
            } else {
                trackingService.requestLocationUpdates();
            }
        }
    }

    private class StopTrackingClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            trackingService.removeLocationUpdates();
            mPresenter.updateCurrentRide(currentLocationMarker.getPosition(), 0, currentRideId);
            //locationUpdatesRequested = false;
        }
    }

    private class TrackingServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //TODO: throw class cast exception
            trackingService = ((TrackingService.LocalBinder) service).getService();
            mBound = true;

            if (!checkPermissions())
                requestPermissions();
            else
                trackingService.requestLocationUpdates();
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
        serviceConnection = new TrackingServiceConnection();
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(myBroadcastReceiver, new IntentFilter(ACTION_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myBroadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound && serviceConnection != null)
            unbindService(serviceConnection);
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


        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);
        //   mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //   mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    private boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            //TODO: implement
            /*Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();*/
        } else {
            Log.d(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(TrackingActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
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
                trackingService.requestLocationUpdates();

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

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            if (bundle.containsKey(CURRENT_LOCATION)) {
                Location location = intent.getParcelableExtra(CURRENT_LOCATION);
                if (location != null) {
                    Log.d(TAG, "onReceive: " + location.getLatitude() + "  " + location.getLongitude());
                    if (locationUpdatesRequested) {
                        mPresenter.insertLocationUpdate(location, currentRideId);
                        updateDistanceCovered(location);
                    }
                    setMarkers(location);
                }
            }
        }
    }


    //TODO: move this to the presenter
    private void updateDistanceCovered(Location location) {
        if(currentLocationMarker != null){
            Location previousLocation = new Location("");
            previousLocation.setLatitude(currentLocationMarker.getPosition().latitude);
            previousLocation.setLongitude(currentLocationMarker.getPosition().longitude);
            distanceCovered += previousLocation.distanceTo(location);
        }
    }

    private void setMarkers(Location location) {

        if (locationUpdatesRequested) {
            if (currentLocationMarker != null)
                currentLocationMarker.remove();

            LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
            currentLocationMarker = mMap.addMarker(new MarkerOptions().position(coordinates).title("Current"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));

        } else {
            //TODO: get current zoom level of the map and use that value in every moveCameraCall
            mMap.clear();
            LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
            startLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title("Start")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 14));
        }
    }

    @Override
    public void onRideInserted(long insertedRowId) {
        currentRideId = insertedRowId;
        locationUpdatesRequested = true;
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            trackingService.requestLocationUpdates();
        }

    }

    @Override
    public void onRideUpdated() {
        locationUpdatesRequested = false;
    }
}
