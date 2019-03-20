package com.example.bike_rental.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.example.bike_rental.models.Bike;

import java.util.ArrayList;
import java.util.List;

public class BikeRepository {

    enum PickupLocations{

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

    public BikeRepository(Application application) {
        //TODO create a room database
        //TODO replace this initialization with dao implementation
        /*
         * get user location save it as user pref and show initial data acoordingly
         */
        availableBikes = new MutableLiveData<>();
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


    /*
    TODO perform some tasks
     *  Async Tasks to perform fetching and handle requests
     */

}
