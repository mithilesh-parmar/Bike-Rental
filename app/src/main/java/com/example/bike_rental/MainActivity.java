package com.example.bike_rental;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import com.example.bike_rental.models.User;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationSettingsStatusCodes;


//TODO on configuration changes location permission is showed again from start
public class MainActivity extends SingleFragmentActivity implements
        NavigationView.OnNavigationItemSelectedListener , View.OnClickListener, MainActivityViewModel.Callback {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Button rentalToolbarButton,ridesTooblarButton;
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 2316;
    private static final int SETTINGS_PERMISSION_CODE = 216;
    private MainActivityViewModel viewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get the viewmodel for main activity
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        //set a callback for viewmodel
        viewModel.setCallback(this);

        // observe for changes in location
        viewModel.getLastKnownLocation().observe(this, location -> {
            Log.d(TAG, "onChanged: new location received "+location);
            User.getInstance().setLocation(location);
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


        //TODO stop this when device configuration changes
        getDeviceLocation();

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
     * TODO imnplementations required in this method
     * called after the user chooses the response for permission
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION_CODE :
                // if permission is granted then get the location else show a alert
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permission granted
                    Log.d(TAG, "onRequestPermissionsResult: starting Location tracking");
                    getDeviceLocation();
                }else {
                    // user rejected the permission
                    Log.d(TAG, "onRequestPermissionsResult: Location Permission Denied, Showing alert ");

                    // TODO implement this ( set the text to require location permission and get the permission when user click on it check for rationale )

//                    boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
//
//                    if (! showRationale){
//                        // user checked never show again box
//                        // start settings activity
//                        showAlert();
//
//                    }else if (showRationale){
//                        // user did not check "never ask again"
//                        // show alert why you need permission
//                        Toast.makeText(this, "We Need location Permission", Toast.LENGTH_SHORT).show();
//                        showPermissionDialog(Manifest.permission.ACCESS_FINE_LOCATION);
//                    }


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
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: ");
        // check For permission
        if (!checkPermissionFor(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // if permission not granted
            Log.d(TAG, "getDeviceLocation: Permission not granted for fine location access");
            showPermissionDialog(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            // if permission  granted
            Log.d(TAG, "getDeviceLocation: requesting last known location");
            viewModel.getDeviceLocation();
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
     */

    private void showAlert() {
        if (!isPermissionDialogVisible()) {
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
        } else {
            Log.d(TAG, "showAlert: permissions dialog is visible");
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


    /**
     * called when location settings are not satisfied as per our requirements
     * called as an callback method from viewmodel
     * show user actions to change for meeting with our requirements
     */

    @Override
    public void locationSettingResolutionRequired(Exception e) {
     // extract the status code
     int statusCode = ((ApiException)e).getStatusCode();

     switch (statusCode){
         case CommonStatusCodes.RESOLUTION_REQUIRED:
             // location setting don't satisfy the requirements but is resolvable by user
             try{
                 //Display a user dialog to resolve the requirements
                 ResolvableApiException exception = (ResolvableApiException) e;
                 exception.startResolutionForResult(this,SETTINGS_PERMISSION_CODE);
             } catch (IntentSender.SendIntentException e1) {
                 e1.printStackTrace();
                 Log.e(TAG, "locationSettingResolutionRequired: Location settings resolution failed ", e );
             }
             break;
         case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
             // location settings don't satisfy the requirements and cannot be resolved by user
             // yet starting listing to location updates
             break;
     }

    }


}
