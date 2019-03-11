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

import com.example.bike_rental.Model.Bike;
import com.example.bike_rental.databinding.FragmentMainBinding;
import com.example.bike_rental.databinding.ListItemBikeBinding;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    private List<Bike> bikes = new ArrayList<>();
    private FragmentMainBinding fragmentMainBinding;
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
        this.fragmentMainBinding = binding;
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        for (int i = 0; i < 10; i++) {
            bikes.add(new Bike("Bike no: "+i));
        }

        fragmentMainBinding.recyclerviewFragmentMain.setAdapter(new BikeAdapter());
        fragmentMainBinding.recyclerviewFragmentMain.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
    }

    class BikeAdapter extends RecyclerView.Adapter<BikeAdapter.BikeHolder>{



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
            bikeHolder.bindView(bikes.get(i));
        }

        @Override
        public int getItemCount() {
            return bikes == null ? 0 : bikes.size();
        }

        class BikeHolder extends RecyclerView.ViewHolder{
            ListItemBikeBinding bikeBinding;

            public BikeHolder(ListItemBikeBinding bikeBinding){
                super(bikeBinding.getRoot());
                this.bikeBinding = bikeBinding;
            }
            public void bindView(Bike b){

            }
        }
    }
}
