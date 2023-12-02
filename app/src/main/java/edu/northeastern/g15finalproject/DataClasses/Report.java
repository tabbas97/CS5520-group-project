package edu.northeastern.g15finalproject.DataClasses;

import androidx.annotation.NonNull;

// FORMAT ON FIREBASE
/*
{
  "city": "Milton",
  "detail": "TFMxmNHZcsWEXvCmoxmz IlmMuLlaQzHyJKDGKlgY hJuaDZwDjDTOkkAFbTLP sYfLxSCiJjpmcBKxFUCN BlVqClPvQYLsYcbmEufw bbTDwCUmnsFbIjlDWoom hczMsKLtzpdOOETuVFQo ",
  "latitude": 42.25390913887854,
  "longitude": -71.04962518673491,
  "state": "Massachusetts",
  "street_address": "Edge Hill Rd opp Westvale Rd, Milton, MA 02186, USA",
  "testing": true,
  "time": 1700742404,
  "type": "People loitering",
  "username": "user53",
  "zipcode": "02186"
}
 */
public class Report {

    /*
        1. People loitering
        2. Crime
        3. Lack of visibility/Darkness
     */
    public enum ReportType {
        PEOPLE_LOITERING,
        CRIME,
        LACK_OF_VISIBILITY
    }
    public final String username;
    public final String type;
    public final String detail;
    public final String street_address;
    public final String city;
    public final String state;
    public final String zipcode;
    public final double latitude;
    public final double longitude;
    public final long time;
    public final boolean testing;

    public Report(String username, String type, String detail, String street_address, String city, String state, String zipcode, double latitude, double longitude, long time, boolean testing) {
        this.username = username;
        this.type = type;
        this.detail = detail;
        this.street_address = street_address;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.testing = testing;
    }

    public int getIntensity() {
        switch (this.type) {
            case "Lack of visibility/Darkness":
                return 1;
            case "People loitering":
                return 2;
            case "Crime":
                return 3;
            default:
                return 0;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Report{" +
                "username='" + username + '\'' +
                ", type='" + type + '\'' +
                ", detail='" + detail + '\'' +
                ", street_address='" + street_address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipcode='" + zipcode + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", time=" + time +
                ", testing=" + testing +
                '}';
    }
}
