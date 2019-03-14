package com.example.bike_rental;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bike_rental.databinding.ActivitySingleFragmentBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends SingleFragmentActivity implements
        NavigationView.OnNavigationItemSelectedListener , View.OnClickListener {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Button rentalToolbarButton,ridesTooblarButton;
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 2316;
    private Location lastKnownLocation;
    private FusedLocationProviderClient locationProviderClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initialize the location provider client
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        // set the title to null or else default nam eof app will show up
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setTitle("");


        // get a reference to buttons from toolbar for navigating between fragments
        rentalToolbarButton = toolbar.findViewById(R.id.rental_toolbar_button);
        ridesTooblarButton = toolbar.findViewById(R.id.rides_toolbar_button);

        //attach a listner to buttons
        rentalToolbarButton.setOnClickListener(this);
        ridesTooblarButton.setOnClickListener(this);


        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // request location
        getLocation();
    }


    /**
     * used to get the fragment that should be inflated in this activity
     * @return MainFragment Instance
     */
    @Override
    public Fragment createFragment() {
        return MainFragment.getInstance();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    /**
     * called when rentals and rides button from toolbar is clicked
     * performs switching of fragments in the main_container of activity
     * @param v
     */
    @Override
    public void onClick(View v) {
        //TODO switch fragments accoridingly in main_container
        if (v==ridesTooblarButton) Toast.makeText(this, "Rides", Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "Rentals", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION_CODE :
                // if permisison is grtanted then get the location else show a toasr
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // get location if permission granted
                    getLocation();
                }else {
                    Log.d(TAG, "onRequestPermissionsResult: Location Permission Denied");
                    Toast.makeText(this, "Location Permission Required", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation(){
        // check For permission
        if (!checkPermissionFor(Manifest.permission.ACCESS_FINE_LOCATION)){
            // if permission not granted
            Log.d(TAG, "getLocation: Permission not granted for fine location access");
            // request permission
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION_CODE);
        }else {
            // if permission is granted then
            //get the last known location form provider
            Log.d(TAG, "getLocation: Requesting last known location");
            locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null ){
                        lastKnownLocation = location;
                        Log.d(TAG, "onSuccess: LastKnownLocation: "+location);
                    }else
                        Toast.makeText(MainActivity.this, "Could not get last known location", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * check for runtime permissions if they are granted or not
     * @param permission - Manifest name of the permission
     * @return true if granted else false
     */
    private boolean checkPermissionFor(String permission){
        return ActivityCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED;
    }


}
