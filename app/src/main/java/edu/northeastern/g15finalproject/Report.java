package edu.northeastern.g15finalproject;

public class Report {
    public String street_address;
    public String city;
    public String state;
    public String zipcode;
    public String detail;
    public double longitude;
    public double latitude;

    public Report() {

    }

    public Report(String street_address, String city, String state, String zipcode,String detail) {
        this.street_address = street_address;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.detail = detail;
        this.longitude = -71.057083;
        this.latitude = 42.361145;
    }
}
