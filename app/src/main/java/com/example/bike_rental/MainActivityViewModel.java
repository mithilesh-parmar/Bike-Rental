package com.example.bike_rental;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivityViewModel extends AndroidViewModel {

    private static final String TAG = "MainActivityViewModel";
    private MutableLiveData<Location> lastKnownLocation = new MutableLiveData<>();
    private FusedLocationProviderClient locationProviderClient;

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

    @SuppressLint("MissingPermission")
    public void getUserLocation(){


        locationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            Log.d(TAG, "onSuccess: Last known location received " + location);
            if (location == null ){
                Log.d(TAG, "getUserLocation: no last known location creating a location request");
                createLocationRequest();
            }else {
                Log.d(TAG, "getUserLocation: setting last known location to live data variable ");
                lastKnownLocation.setValue(location);
            }
        }).addOnFailureListener(e-> {
            Log.d(TAG, "onFailure: " + e);
            Log.d(TAG, "getUserLocation: creating a location request");
            createLocationRequest();
        });

        Log.d(TAG, "getUserLocation: Outside lambda");

    }

//    @SuppressLint("MissingPermission")
//    public void startTrackingLocation() {
//        locationProviderClient.getLastLocation().addOnSuccessListener(location -> {
//
//            Log.d(TAG, "startTrackingLocation: Last Known location "+location.toString());
//            if (location == null ){
//                Log.d(TAG, "startTrackingLocation: createing location request as location is null");
//                createLocationRequest();
//            }
//            else {
//                Log.d(TAG, "startTrackingLocation: setting the value of location received");
//                lastKnownLocation.setValue(location);
//            }
//
//        }).addOnFailureListener(e -> {
//
//            Log.d(TAG, "onFailure: " + e);
//            createLocationRequest();
//        });
//
//
//    }

    public void stopTrackingLocation(){
        locationProviderClient.removeLocationUpdates(locationCallback);
    }


    @SuppressLint("MissingPermission")
    private void createLocationRequest(){
        Log.d(TAG, "createLocationRequest: creating a location request");

        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // set priority for updates as high
        request.setInterval(10000); // time in seconds for which the app should receive location updates
        request.setMaxWaitTime(5000); // time in seconds for which app can delay location updates
        request.setNumUpdates(5); // stop location updates after 5 new locations have been received
        //register the locationcallback with provider client
        locationProviderClient.requestLocationUpdates(request, locationCallback, null);
    }

    public MutableLiveData<Location> getLastKnownLocation() {
        return lastKnownLocation;
    }
}

