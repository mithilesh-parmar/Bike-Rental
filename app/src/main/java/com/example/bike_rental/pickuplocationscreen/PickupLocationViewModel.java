package com.example.bike_rental.pickuplocationscreen;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.bike_rental.database.BikeRepository;

import java.util.List;

public class PickupLocationViewModel extends AndroidViewModel {

    private BikeRepository bikeRepository;

    public PickupLocationViewModel(@NonNull Application application) {
        super(application);
        bikeRepository = new BikeRepository(application);
    }

    public LiveData<List<String>> getpickUpLocationsForCity(BikeRepository.City city){
        return bikeRepository.getPickupLocationsForCity(city);
    }

    public LiveData<List<String>> getPickupCities(){
        return bikeRepository.getPickupCities();
    }

    public LiveData<List<String>> getPickupLocations(){
        return bikeRepository.getPickupLocations();
    }
}
