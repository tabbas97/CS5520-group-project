package edu.northeastern.g15finalproject.DataClasses;

public class CustomWeightedLatLong {
    private double latitude;
    private double longitude;
    private double weight;

    public CustomWeightedLatLong(double latitude, double longitude, double weight) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.weight = weight;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getWeight() {
        return this.weight;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
