package com.example.bike_rental;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bike_rental.databinding.FragmentMainBinding;
import com.example.bike_rental.databinding.ListItemBikeBinding;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    /**
     * @availableBikes - bikes available at a particualr location
     * @fragmentMainBinding - binding for this fragment
     *
     */
    private List<Bike> availableBikes = new ArrayList<>();
    private FragmentMainBinding fragmentMainBinding;

    // Static method to create a new instance of this fragment used by activity which contains this fragment
    public static MainFragment getInstance(){
        return new MainFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentMainBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_main,
                container,
                false
        );
        // get a reference to binding
        this.fragmentMainBinding = binding;
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //TODO get bike data from server
        availableBikes.add(new Bike("Hero Honda","Splendor Plus","Avg Of 20kms/l","12/hr",""));
        availableBikes.add(new Bike("Hero Honda","Splendor","Avg Of 20kms/l","12/hr",""));
        availableBikes.add(new Bike("Honda","CBZ","Avg Of 20kms/l","12/hr",""));
        availableBikes.add(new Bike("Hero","Activa 5g","Avg Of 20kms/l","12/hr",""));
        availableBikes.add(new Bike("Hero","Activa 125","Avg Of 20kms/l","12/hr",""));
        availableBikes.add(new Bike("Honda","trigger","Avg Of 20kms/l","12/hr",""));
        availableBikes.add(new Bike("Honda","Plus","Avg Of 20kms/l","12/hr",""));


        //set adapter and layoutmanager to recyclerview
        fragmentMainBinding.recyclerviewFragmentMain.setAdapter(new BikeAdapter());
        fragmentMainBinding.recyclerviewFragmentMain.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
    }

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



        class BikeHolder extends RecyclerView.ViewHolder{
            // Binding of the List Item used by recycler view
            ListItemBikeBinding bikeBinding;

            public BikeHolder(ListItemBikeBinding bikeBinding){
                super(bikeBinding.getRoot());
                this.bikeBinding = bikeBinding;
                //TODO impelement selection logic and highlight the selected accordingly
                bikeBinding.parentLayoutListItemBike.setOnClickListener(e->{
                    bikeBinding.parentLayoutListItemBike.setBackground(
                            getActivity().getDrawable(R.drawable.forground_selection_style)
                    );
                });
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
