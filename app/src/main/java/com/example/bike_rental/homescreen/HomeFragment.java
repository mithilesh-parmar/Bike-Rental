package com.example.bike_rental.homescreen;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
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
    private final static int PICKUP_LOCATION_REQUESTCODE = 2314;

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


        viewModel.getAvailableBikes().observe(this, bikes -> {
            this.availableBikes = bikes;
            //TODO investigate if this is best practise or should I get a reference to adapter during first initialization
            fragmentHomeBinding.recyclerviewFragmentMain.getAdapter().notifyDataSetChanged();
        });


        fragmentHomeBinding.userPickupText.setOnClickListener(e->{
            Log.d(TAG, "onViewCreated: userPickUpText clicked");
            Intent intent =new Intent(getActivity(),PickupLocationActivity.class);
            startActivityForResult(intent,PICKUP_LOCATION_REQUESTCODE);
        });

        fragmentHomeBinding.fromCard.setOnClickListener(e->{
            Log.d(TAG, "onViewCreated: ");
        });
        fragmentHomeBinding.tillCard.setOnClickListener(e->{
            Log.d(TAG, "onViewCreated: ");
        });

        fragmentHomeBinding.recyclerviewFragmentMain.setAdapter(new BikeAdapter());
        fragmentHomeBinding.recyclerviewFragmentMain.setLayoutManager(
                new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false)
        );


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PICKUP_LOCATION_REQUESTCODE:
                if (resultCode == RESULT_OK){
                    fragmentHomeBinding.userPickupText.setText(data.getStringExtra(PickupLocationActivity.EXTRA_SELECTED_LOCATION));
                }
        }
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
