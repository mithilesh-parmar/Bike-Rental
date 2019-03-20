package com.example.bike_rental;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bike_rental.homescreen.HomeFragment;


public class MainActivity extends SingleFragmentActivity implements
        NavigationView.OnNavigationItemSelectedListener , View.OnClickListener {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Button rentalToolbarButton,ridesTooblarButton;
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 2316;
    private MainActivityViewModel viewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get the viewmodel for main activity
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        // observe for changes in location
        viewModel.getLastKnownLocation().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(@Nullable Location location) {
                Log.d(TAG, "onChanged: new location received "+location);
                Toast.makeText(MainActivity.this, "Location "+location, Toast.LENGTH_SHORT).show();
            }
        });


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

        // start location tracking
        startTrackingLocation();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // if the permissions dialog is not visible then check if the permissions from setting were granted
        if (!isPermissionDialogVisible() && !checkPermissionFor(Manifest.permission.ACCESS_FINE_LOCATION)){
            Log.d(TAG, "onResume: permissions not granted");
            showPermissionDialog(Manifest.permission.ACCESS_FINE_LOCATION);
        }

    }

    /**
     * used to get the fragment that should be inflated in this activity
     * @return MainFragment Instance
     */
    @Override
    public Fragment createFragment() {
        return HomeFragment.createInstance();
    }


    /**
     * called when drawer icon on toolbar is selected
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * called when user clicks on any navigation drawer item
     * change the fragment of the main container accordingly
     * TODO implementation pending
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    /**
     * called when rentals and rides button from toolbar is clicked
     * performs switching of fragments in the main_container of activity
     * TODO implementation pending
     * @param v
     */
    @Override
    public void onClick(View v) {
        //TODO switch fragments accoridingly in main_container
        if (v==ridesTooblarButton) Toast.makeText(this, "Rides", Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "Rentals", Toast.LENGTH_SHORT).show();
    }


    /**
     * called after the user chooses the response for permission
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION_CODE :
                // if permission is granted then get the location else show a alert
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "onRequestPermissionsResult: starting Location tracking");
                    startTrackingLocation();
                }else {
                    Log.d(TAG, "onRequestPermissionsResult: Location Permission Denied, Showing alert ");
                    showAlert();
                }
                break;
        }
    }

    /**
     * check for permissions
     * if not granted then show a dialogue for the same
     * if granted then request device location from viewmodel
     */
    @SuppressLint("MissingPermission")
    private void startTrackingLocation() {
        Log.d(TAG, "startTrackingLocation: ");
        // check For permission
        if (!checkPermissionFor(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // if permission not granted
            Log.d(TAG, "startTrackingLocation: Permission not granted for fine location access");
            showPermissionDialog(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            // if permission  granted
            Log.d(TAG, "startTrackingLocation: requesting last known location");
            viewModel.getUserLocation();
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        // remove location callback when the activity destroys
        viewModel.stopTrackingLocation();
    }


    /**
     * request permission helper method
     * @param permission
     */

    private void showPermissionDialog(String permission){
        ActivityCompat.requestPermissions(this,
                new String[]{permission},
                REQUEST_LOCATION_PERMISSION_CODE);
    }


    /**
     * check for runtime permissions if they are granted or not
     * @param permission - Manifest name of the permission
     * @return true if granted else false
     */
    private boolean checkPermissionFor(String permission){
        return ActivityCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * show alertdialog to user for required permissions
     *
     */

    private void showAlert(){
        if (!isPermissionDialogVisible()){
            //permission dialog is not displayed
            Log.d(TAG, "showAlert: permissions dialog is not visible ");
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Permissions Required")
                    .setMessage("You have forcefully denied some of the required permissions " +
                            "for application to work. Please open settings, go to permissions and allow them.")
                    .setPositiveButton("Settings", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Exit", (dialog, which) -> {
                        Log.d(TAG, "onClick: Exiting application user is stubborn");
                        finish();
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }else {
            Log.d(TAG, "showAlert: permissions dialog is visible");
            return;
        }
    }

    /**
     * helper method to check if permissions dialog is visible
     * @return
     */
    private boolean isPermissionDialogVisible(){
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return "com.android.packageinstaller.permission.ui.GrantPermissionsActivity".equals(cn.getClassName());
    }


}
