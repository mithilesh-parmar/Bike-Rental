package com.example.bike_rental.homescreen;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.example.bike_rental.database.BikeRepository;
import com.example.bike_rental.models.Bike;

import java.util.List;

public class HomeFragmentViewModel extends AndroidViewModel {

    private static final String TAG = "HomeFragmentViewModel";
    private BikeRepository repository;
    private LiveData<List<Bike>> availableBikes;

    @SuppressLint("CommitPrefEdits")
    public HomeFragmentViewModel(@NonNull Application application) {
        super(application);
        repository = new BikeRepository(application);
        availableBikes = repository.getAvailableBikes();
    }

    public LiveData<List<Bike>> getAvailableBikes() {
        return availableBikes;
    }


}
