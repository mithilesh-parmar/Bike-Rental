package com.example.bike_rental.models;

import java.util.UUID;

public class Bike {

    private UUID id;
    private String companyName;
    private String modelNumber;
    private String description;
    private String hourlyPriceRate;
    private String average;



    public Bike(String companyName, String modelNumber, String description, String hourlyPriceRate, String average) {
        id = UUID.randomUUID();
        this.companyName = companyName;
        this.modelNumber = modelNumber;
        this.description = description;
        this.hourlyPriceRate = hourlyPriceRate;
        this.average = average;
    }

    //TODO remove this constructor
    public Bike(String s) { this.modelNumber = s; }

    public UUID getId() { return id; }

    public void setId(UUID id) { this.id = id; }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHourlyPriceRate() {
        return hourlyPriceRate;
    }

    public void setHourlyPriceRate(String hourlyPriceRate) { this.hourlyPriceRate = hourlyPriceRate; }

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }
}
