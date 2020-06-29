package com.example.maptest;

import com.google.android.gms.maps.model.LatLng;

public class OrderMarker {
    private String currentAddress;
    private String food;
    private String latitude;
    private String longitude;

    public OrderMarker(){

    }

    public OrderMarker(String currentAddress, String food, LatLng latLng){
        this.currentAddress = currentAddress;
        this.food = food;
        this.latitude = String.valueOf(latLng.latitude);
        this.longitude = String.valueOf(latLng.longitude);

    }

    public LatLng getLatLng(){
        return new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
    }


    public String getCurrentAddress() {
        return currentAddress;
    }
    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String destination) {this.food = destination;}

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }





}
