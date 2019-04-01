package com.example.bike_rental.rentalScreen;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bike_rental.R;
import com.example.bike_rental.databinding.FragmentRentalsBinding;

public class RentalsFragment extends Fragment {
    public static final String TAG = "RentalsFragment";

    public static RentalsFragment createInstance(){
        return new RentalsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentRentalsBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_rentals,
                container,
                false
        );
        return binding.getRoot();
    }
}
