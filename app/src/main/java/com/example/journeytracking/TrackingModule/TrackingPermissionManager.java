package com.example.journeytracking.TrackingModule;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import com.example.journeytracking.Utils.CONSTANTS;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 *
 * base class to request for run time permissions
 * callbacks are handled in the derived class i.e. Tracking Activity
 */
public class TrackingPermissionManager extends FragmentActivity {

    static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    static final int REQUEST_CHECK_SETTINGS = 2;

    private static final String TAG = "PermissionManager";

    //check for runtime permissions
     boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    //if we don't have the permission this method is used to request them
     void requestPermissions() {

            Log.d(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    //function to check if location settings are disabled or not
     void checkLocationSettings() {

        //broadcast receiver for connectivity change getting triggered multiple time for a single switch
        //using a static static variable here to ensure that the settings are checked only once and not multiple times
        if (CONSTANTS.locationSettingRequestCount++ < 1) {
            //settings will be checked for this sample location request
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            //setAlwaysShow means is required when the location updates  are crucial to our app functionality
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().setAlwaysShow(true).addLocationRequest(locationRequest);

            SettingsClient client = LocationServices.getSettingsClient(this);
            //checking location setting returns a task which we can check later by attaching listeners to it
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            //this is called if the settings are up to our requirement
            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    CONSTANTS.locationSettingRequestCount = 0;
                    // All location settings are satisfied. The client can initialize
                    // location requests here.
                    // ...
                }
            });

            //this is called when the settings are not suitable
            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(TrackingPermissionManager.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                        }
                    }
                }
            });
        }
    }
}
