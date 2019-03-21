package com.example.bike_rental.pickuplocationscreen;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bike_rental.R;
import com.example.bike_rental.database.BikeRepository;
import com.example.bike_rental.databinding.ActivityPickupLocationBinding;
import com.example.bike_rental.databinding.ListItemPickupBinding;

import java.util.ArrayList;
import java.util.List;

//TODO add pickuplocation for city currenlty the user is in
public class PickupLocationActivity extends AppCompatActivity implements PickupAdapter.Callback {

    private static final String TAG = "PickupLocationActivity";
    public static final String EXTRA_SELECTED_LOCATION = "PICKUP_LOCATION_SELECTED"; // key for location selected
    public static final String KEY_CITY_SELECTED_BOOLEAN = "IS_CITY_SELECTED"; //  key for is city selected
    public static final String KEY_SELECTED_CITY_STRING = "CITY_SELECTED_NAME"; // key for selected city name
    private PickupLocationViewModel viewModel; // view model for acitvity
    private boolean isCitySelected = false; // if selected then show pickuplocations
    private String selectedCity ;// selected city name
    private PickupAdapter adapter; // adapter for recyclerview
    private SharedPreferences.Editor editor;// editor for saving values
    private TextView toolbarTitleTextView; // textview which holds title for this activity

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityPickupLocationBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_pickup_location);
        setSupportActionBar(binding.toolbarPickupLocationActivity); // set custom toolbar
        getSupportActionBar().setTitle(""); // set the toolbar titile to null
        toolbarTitleTextView = binding.titleToolbarPickupLocationActivity;

        toolbarTitleTextView.setOnClickListener(e->{
            isCitySelected = false;
            adapter.setCitySelected(isCitySelected);
            selectedCity = "";
            savePref(selectedCity,isCitySelected);
            getPickupCities();
            setActivityTitle(selectedCity);
        });

        // get the default shared preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // get the editor of shared preference
        editor = sharedPreferences.edit();



        // get values from shared preferences
        isCitySelected = sharedPreferences.getBoolean(KEY_CITY_SELECTED_BOOLEAN,false);
        Log.d(TAG, "onCreate: isCitySelected "+isCitySelected);
        selectedCity = sharedPreferences.getString(KEY_SELECTED_CITY_STRING,"");
        Log.d(TAG, "onCreate: selectedCity "+selectedCity);

        // get values from saved instance state object if isCitySelected value is still false and selected city is still empty string
        if (savedInstanceState != null && !isCitySelected && selectedCity.equals("")){
            isCitySelected = savedInstanceState.getBoolean(KEY_CITY_SELECTED_BOOLEAN);
            Log.d(TAG, "onCreate: isCitySelected "+isCitySelected);
            selectedCity = savedInstanceState.getString(KEY_SELECTED_CITY_STRING);
            Log.d(TAG, "onCreate: selectedCity "+selectedCity);

        }

        // get reference to viewmodel
        viewModel = ViewModelProviders.of(this).get(PickupLocationViewModel.class);


        adapter = new PickupAdapter(this);
        adapter.setCitySelected(isCitySelected);

        binding.recyclerviewActivityPickupLocation.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerviewActivityPickupLocation.setAdapter(adapter);
        binding.recyclerviewActivityPickupLocation.setHasFixedSize(true);// to improve the performance
        binding.recyclerviewActivityPickupLocation.addItemDecoration(
                new DividerItemDecoration(this,DividerItemDecoration.VERTICAL)
        );



        if (!isCitySelected) getPickupCities(); // no city selected by user
        else getPickupLocationsForCity(selectedCity); // city is selected show pickuplocations for that city

        // set the title for the activity
        setActivityTitle(selectedCity);
    }

    /**
     * save cityselected name and boolean value of @isCitySelected
     * @param outState
     * @param outPersistentState
     */
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean(KEY_CITY_SELECTED_BOOLEAN,isCitySelected);
        outState.putString(KEY_SELECTED_CITY_STRING,selectedCity);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchview,menu);

        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: "+newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * TODO impelemtation pending
     * request pickup locations for city
     * @param city - city selected by user
     */
    private void getPickupLocationsForCity(String city){
        // request view model for pickup locations
        //TODO change the implementation with get a specific location
      if (!viewModel.getPickupLocations().hasObservers()){
          viewModel.getPickupLocations().observe(PickupLocationActivity.this,pickupLocations->{
              // if city is already selected
              Log.d(TAG, "getPickupLocationsForCity: requesting pickuplocations for city "+city);
              adapter.setItems(pickupLocations);
              adapter.notifyDataSetChanged();
          });
      }
    }

    /**
     * request serviceable cities
     */
    private void getPickupCities(){
        // request viewmodel for available cities
       if (!viewModel.getPickupCities().hasObservers()){
           viewModel.getPickupCities().observe(this, cities -> {
               // if city is not selected yet then
               Log.d(TAG, "getPickupCities: requesting serviceable cities "+cities);
               adapter.setItems(cities);
               adapter.notifyDataSetChanged();
           });
       }
    }


    /**
     * called from pickupadapter when user selects a pickuplocation
     * @param pickupLocation
     */
    @Override
    public void onPickupLocationSelected(String pickupLocation) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_SELECTED_LOCATION,pickupLocation);
        setResult(RESULT_OK,intent);
        finish();
    }

    /**
     * called from pickupadapter when user selects a serviceable city
     * @param city
     */

    @Override
    public void onCitySelected(String city) {
        isCitySelected = true;
        selectedCity = city;
        adapter.setCitySelected(isCitySelected);
        savePref(city,isCitySelected);
        setActivityTitle(city);
        getPickupLocationsForCity(city);
    }



    /**
     * save preferences
     * @param city
     * @param isCitySelected
     */

    private void savePref(String city,boolean isCitySelected){
        Log.d(TAG, "savePref: city "+city+" isCitySelected"+isCitySelected);
        editor.putString(KEY_SELECTED_CITY_STRING,city);
        editor.putBoolean(KEY_CITY_SELECTED_BOOLEAN,isCitySelected);
        editor.apply();
    }

    /**
     * set the title for the activity if the parameter passed has no value then set it to "Select city"
     * @param title
     */
    private void setActivityTitle(String title){
        if (title.equals("")) title = "Select city";
        toolbarTitleTextView.setText(title);
    }

}
