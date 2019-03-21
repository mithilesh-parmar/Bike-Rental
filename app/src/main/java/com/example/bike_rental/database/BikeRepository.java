package com.example.bike_rental.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.bike_rental.models.Bike;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BikeRepository {

    private static final String TAG = "BikeRepository";

    public enum City {
        JAIPUR("Jaipur"),
        JODHPUR("Jodhpur");

        private String s;

        City(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }
    }

    public enum PickupLocations{

        JECRC_UNIVERSITY("Jecrc University") ,
        POORNIMA_COLLEGE("Poornima College") ,
        JECRC_COLLEGE("Jecrc College") ,
        MANIPAL_INSTITUTE_OF_TECHNOLOGY("Manipal institute of technology");

        private String location;
        PickupLocations(String location){
            this.location = location;
        }

        @Override
        public String toString() {
            return location;
        }
    }

    /*
     * All the fetching and requests to the server should be done in this class
     */

    private MutableLiveData<List<Bike>> availableBikes;
    private MutableLiveData<List<String>> pickupCities; // cities where service is currently available
    private MutableLiveData<List<String>> pickupLocations; // pickuplocations for each city

    public BikeRepository(Application application) {
        //TODO create a room database
        //TODO replace this initialization with dao implementation
        /*
         * get user location save it as user pref and show initial data acoordingly
         */
        availableBikes = new MutableLiveData<>();
        pickupCities = new MutableLiveData<>();
        pickupLocations = new MutableLiveData<>();
    }

    public LiveData<List<Bike>> getAvailableBikes() {

        List<Bike> bikes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {

            //TODO replace it with a async task to fecth data from server
            bikes.add(new Bike("Hero Honda"));

        }
        availableBikes.setValue(bikes);
        return availableBikes ;
    }

    public LiveData<List<Bike>> getAvailableBikesForLocation(PickupLocations location){
        //TODO get bikes according to location selected
        return  null;
    }

    public MutableLiveData<List<String>> getPickupCities() {
        List<String> cities = new ArrayList<>();
        for (City c :City.values()) {
            cities.add(c.toString());
        }
        pickupCities.setValue(cities);
        return pickupCities;
    }

    public MutableLiveData<List<String>> getPickupLocationsForCity(City city) {
        // search for pickuplocations on server
        return pickupLocations;
    }

    public MutableLiveData<List<String>> getPickupLocations() {
        List<String> list = new ArrayList<>();
        for (PickupLocations l : PickupLocations.values()) {
            list.add(l.toString());
        }
        pickupLocations.setValue(list);
        return pickupLocations;
    }

    /*
    TODO perform some tasks
     *  Async Tasks to perform fetching and handle requests
     */

}
