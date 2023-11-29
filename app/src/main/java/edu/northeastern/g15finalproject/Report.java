package edu.northeastern.g15finalproject;

public class Report {
    private String street_address;
    private String city;
    private String state;
    private String zipcode;
    private String detail;
    private double longitude;
    private double latitude;
    private String type;
    private String username;
    private String time;
    private Boolean isTesting;

    public Report() {

    }

    public Report(String street_address, String city, String state, String zipcode, String detail,
                  String type, String username, String time, Boolean isTesting,
                  double latitude, double longitude) {
        this.street_address = street_address;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.detail = detail;
        this.longitude = longitude;
        this.latitude = latitude;
        this.type = type;
        this.username = username;
        this.time = time;
        this.isTesting = isTesting;
    }

    public String getStreet_address() {
        return this.street_address;
    }

    public String getCity() {
        return this.city;
    }

    public String getState() {
        return this.state;
    }

    public String getZipcode() {
        return this.zipcode;
    }

    public String getDetail() {
        return this.detail;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public String getType() {
        return this.type;
    }

    public String getUsername() {
        return this.username;
    }

    public String getTime() {
        return this.time;
    }

    public Boolean getTesting() {
        return this.isTesting;
    }

    public String getFullAddress() {
        return street_address + ", " + city + ", " + state + ", " + zipcode;
    }
}
