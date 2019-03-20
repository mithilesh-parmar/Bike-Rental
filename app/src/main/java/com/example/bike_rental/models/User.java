package com.example.bike_rental.models;

import android.location.Location;

public class User {

    private static User instance;

    private Location userLocation;

    private  User() {
    }

    public static User getInstance() {
        if (instance == null ){
            synchronized (User.class){
                if (instance == null){
                    instance = new User();
                }
            }
        }
        return instance;
    }

    public void setLocation(Location location) {
        this.userLocation = location;
    }

    public Location getUserLocation() {
        return userLocation;
    }
}
