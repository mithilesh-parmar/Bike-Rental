package com.example.bike_rental.homescreen;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bike_rental.models.Bike;
import com.example.bike_rental.R;
import com.example.bike_rental.databinding.FragmentHomeBinding;
import com.example.bike_rental.databinding.ListItemBikeBinding;
import com.example.bike_rental.models.User;
import com.example.bike_rental.pickuplocationscreen.PickupLocationActivity;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

//TODO migrate all the location code from mainactivity and add a geocoder to get city of the user
public class HomeFragment  extends Fragment {

    /**
     * static method to get a fragment of this class
     * @return
     */
    public static HomeFragment createInstance(){ return new HomeFragment(); }

    private static final String TAG = "HomeFragment";
    private List<Bike> availableBikes = new ArrayList<>(); // available bikes
    private FragmentHomeBinding fragmentHomeBinding; // Binding for the layout inflated for the fragment
    private HomeFragmentViewModel viewModel; // view Model for this fragment
    private final static int PICKUP_LOCATION_REQUESTCODE = 2314; // requestcode
    private SharedPreferences.Editor editor; // editor instance for saving to prefd
    private static final String KEY_PICKUP_LOCATION_NAME = "Pickup location name "; // key for pref
    private String pickupLocation  = "Select pickup location";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editor = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentHomeBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_home,
                container,
                false
        );

        // get a reference to viewmodels  for the fragment
        viewModel = ViewModelProviders.of(this).get(HomeFragmentViewModel.class);


        // get a reference to binding
        this.fragmentHomeBinding = binding;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // if its a configuration change then extract the data
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_PICKUP_LOCATION_NAME)){
            pickupLocation = savedInstanceState.getString(KEY_PICKUP_LOCATION_NAME); // get the pickup location
        }

        if (!viewModel.getAvailableBikes().hasObservers()){
            // has no observers
            //TODO change the implementation to get avaialble bikes for a particular location
            viewModel.getAvailableBikes().observe(this, bikes -> {
                // request available bikes for selected pickup location
                this.availableBikes = bikes;
                //TODO investigate if this is best practise or should I get a reference to adapter during first initialization
                fragmentHomeBinding.recyclerviewFragmentMain.getAdapter().notifyDataSetChanged(); // notify the adapter
            });
        }

        // open the pickupactivity on click
        fragmentHomeBinding.userPickupText.setOnClickListener(e->{
            Log.d(TAG, "onViewCreated: userPickUpText clicked");
            Intent intent =new Intent(getActivity(),PickupLocationActivity.class);
            startActivityForResult(intent,PICKUP_LOCATION_REQUESTCODE);
        });

        //TODO show calendar view
        fragmentHomeBinding.fromCard.setOnClickListener(e->{
            Log.d(TAG, "onViewCreated: ");
        });

        fragmentHomeBinding.tillCard.setOnClickListener(e->{
            Log.d(TAG, "onViewCreated: ");
        });

        // setup recycler view
        fragmentHomeBinding.recyclerviewFragmentMain.setAdapter(new BikeAdapter());
        fragmentHomeBinding.recyclerviewFragmentMain.setLayoutManager(
                new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false)
        );


        fragmentHomeBinding.userPickupText.setText(pickupLocation); // set it to textview

    }

    /**
     * called after the user selects the pickup location from PickupLocation activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PICKUP_LOCATION_REQUESTCODE:
                if (resultCode == RESULT_OK){
                    // get the piockuplocation passed by the activity
                    pickupLocation = data.getStringExtra(PickupLocationActivity.EXTRA_SELECTED_LOCATION);
                    fragmentHomeBinding.userPickupText.setText(pickupLocation); // set the text
                    savePref(pickupLocation); // save to shared pref
                }
        }
    }


    /**
     * used to save pickuplocation to sharedpref
     * @param pickupLocation
     */
    private void savePref(String pickupLocation){
        editor.putString(KEY_PICKUP_LOCATION_NAME,pickupLocation);
        editor.apply();
    }

    /**
     * lifecycle call
     * @param outState
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(KEY_PICKUP_LOCATION_NAME,pickupLocation);
        super.onSaveInstanceState(outState);
    }

    /**
     * Recyclerview adapter for bikes available in region
     */

    class BikeAdapter extends RecyclerView.Adapter<BikeAdapter.BikeHolder>{

        private static final String TAG = "BikeAdapter";

        public BikeAdapter() {

        }

        @NonNull
        @Override
        public BikeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            ListItemBikeBinding bikeBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(viewGroup.getContext()),
                    R.layout.list_item_bike,
                    viewGroup,
                    false
            );
            return new BikeHolder(bikeBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull BikeHolder bikeHolder, int i) {
            bikeHolder.bindView(availableBikes.get(i));
        }

        @Override
        public int getItemCount() {
            return availableBikes == null ? 0 : availableBikes.size();
        }



        /**
         * Holder for bikes
         */
        class BikeHolder extends RecyclerView.ViewHolder{
            // Binding of the List Item used by recycler view
            ListItemBikeBinding bikeBinding;

            public BikeHolder(ListItemBikeBinding bikeBinding){
                super(bikeBinding.getRoot());
                this.bikeBinding = bikeBinding;
                //TODO impelement selection logic and highlight the selected accordingly

            }

            /**
             *
             * @param b - bike object that should be binded to the recycler view
             */

            public void bindView(Bike b){
                bikeBinding.setBike(b);
                bikeBinding.executePendingBindings();
            }
        }
    }


}
