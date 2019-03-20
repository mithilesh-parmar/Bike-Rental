package com.example.bike_rental;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

public class MainActivityViewModel extends AndroidViewModel {

    private static final String TAG = "MainActivityViewModel";
    private MutableLiveData<Location> lastKnownLocation = new MutableLiveData<>(); // last known location of the device
    private FusedLocationProviderClient locationProviderClient; // location provider client which will give us the device location
    private Callback callback; // callback method for main activity

    /**
     * location callback called when new location is obtained
     */
    private LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            for (Location location : locationResult.getLocations()){
                Log.d(TAG, "onLocationResult: Got Location: "+location);
                lastKnownLocation.setValue(location);
            }
        }

    };

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        // initialize the location provider client
        locationProviderClient = LocationServices.getFusedLocationProviderClient(getApplication());
    }

    /**
     * called from mainactivity
     * gets the last known location
     * if no known location are present, creates a location request and starts listening to updates
     * else sets the @lastKnownLocation value to location present (last known)
     */
    @SuppressLint("MissingPermission")
    public void getDeviceLocation(){

        // get the last known location of the device if any of tha application has requested for it from the last boot
        locationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            Log.d(TAG, "onSuccess: Last known location received " + location);
            if (location == null ){
                // no last known location found
                Log.d(TAG, "getDeviceLocation: no last known location creating a location request");
                createLocationRequest();
            }else {
                // last known location found
                Log.d(TAG, "getDeviceLocation: setting last known location to live data variable ");
                lastKnownLocation.setValue(location);
            }
        }).addOnFailureListener(e-> {
            Log.d(TAG, "onFailure: " + e);
            Log.d(TAG, "getDeviceLocation: creating a location request");
            createLocationRequest();
        });


    }

    /**
     * used to unregister the location callback
     * when the activty is destroyed this is called to unregister the callback
     */
    public void stopTrackingLocation(){
        locationProviderClient.removeLocationUpdates(locationCallback);
    }


    /**
     * creates a location request
     * if the requirements are not matched then a task is scheduled
     * which is handled by the main activity by an interface callback
     */
    @SuppressLint("MissingPermission")
    private void createLocationRequest(){
        Log.d(TAG, "createLocationRequest: creating a location request");

        // create a location request
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // set priority for updates as high
        request.setInterval(10000); // time in seconds for which the app should receive location updates
        request.setMaxWaitTime(5000); // time in seconds for which app can delay location updates
        request.setNumUpdates(5); // stop location updates after 5 new locations have been received

        // get the settings client
        SettingsClient settingsClient = LocationServices.getSettingsClient(getApplication());
        // build a location setting request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(request);
        //check if location setting satisfy requirements
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());


        task.addOnSuccessListener(locationSettingsResponse -> {
            // location settings satisfy requirements
            Log.d(TAG, "onSuccess: location setting satisfy");
        }).addOnFailureListener(e -> {
            if (callback != null){
                // location settings do not satisfy
                Log.d(TAG, "createLocationRequest: location settings do not satisfy requirements");
                callback.locationSettingResolutionRequired(e);
            }
        });

        //register the locationcallback with provider client
        Log.d(TAG, "createLocationRequest: registering location provider client for location updates");
        locationProviderClient.requestLocationUpdates(request, locationCallback, null);
    }

    // getter
    public MutableLiveData<Location> getLastKnownLocation() {
        return lastKnownLocation;
    }

    //setter
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * callback interface used to specify the main activity about the location settings failure and handle it accordingly
     */
    interface Callback{
         void locationSettingResolutionRequired(Exception e);
    }
}

