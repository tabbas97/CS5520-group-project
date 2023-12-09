package edu.northeastern.g15finalproject;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AggregateReport extends Report {
    private List<Report> reports;
    static final float MAX_RADIUS = 1000;

    public AggregateReport(Report startReport) {
        // Report constructor for reference:
        // public Report(String street_address, String city, String state, String zipcode, String detail,
        //                   String type, String username, Long time, Boolean isTesting,
        //                   double latitude, double longitude)

        super(
                startReport.getStreet_address(), startReport.getCity(),
                startReport.getState(), startReport.getZipcode(),
                startReport.getDetail(), startReport.getType(),
                startReport.getUsername(), startReport.getTime(),
                startReport.getTesting(), startReport.getLatitude(),
                startReport.getLongitude()
        );
        this.reports = new ArrayList<>();
        this.reports.add(startReport);
    }

    public void addReport(Report report) {

        if (!this.isAddable(report)) {
            throw new IllegalArgumentException("Report is not addable");
        }

        // Add the report to the list of reports
        this.reports.add(report);

        // Update the aggregate report's fields
        this.updateFields();
    }

    public boolean isAddable(Report report) {
        // Check if the report is within the radius of the aggregate report
        double distance = this.getDistanceFromReport(report);

        // Check if the report is within the radius of the aggregate report
        return distance <= MAX_RADIUS;
    }

    private float getDistanceFromReport(Report report) {
        // Get the distance from the report to the aggregate report
        return this.getDistance(report.getLatitude(), report.getLongitude());
    }

    private void updateFields() {
        // List of all latitudes and longitudes
        List<Double> latitudes = new ArrayList<>();
        List<Double> longitudes = new ArrayList<>();

        // Iterate through all the reports
        for (Report report : this.reports) {
            // Add the latitude and longitude to the lists
            latitudes.add(report.getLatitude());
            longitudes.add(report.getLongitude());
        }

        // Get the average latitude and longitude
        double averageLatitude = this.getAverage(latitudes);
        double averageLongitude = this.getAverage(longitudes);

        // Use the geocoding API to get the address from the average latitude and longitude


    }

    private JSONObject getAddressFromCoordinates(double latitude, double longitude) {
        // Get the address from the latitude and longitude
        String reqURL = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s", latitude, longitude, R.string.geocoding_api_key);

        // Make get request to the geocoding API
        JSONObject response = null;
        HttpURLConnection connection = null;
        try {

            URL url = new URL(reqURL);
            // Create the connection
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Get the response code
            int responseCode = connection.getResponseCode();

            // Check if the response code is OK
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Get the response
                InputStream responseStream = connection.getInputStream();
                java.util.Scanner s = new java.util.Scanner(responseStream).useDelimiter("\\A");
                String responseString = s.hasNext() ? s.next() : "";
                responseStream.close();
                responseString += "\0";

                // Convert the response to a JSON object
                response = new JSONObject(responseString);
            }
        } catch (Exception e) {
            // Print the stack trace
            e.printStackTrace();
        } finally {
            // Disconnect the connection
            connection.disconnect();
        }
        System.out.println("Response: " + response);

        return null;
    }

    private double getAverage(List<Double> values) {
        // Get the sum of all the values
        double sum = 0;
        for (double value : values) {
            sum += value;
        }

        // Get the average of the values
        return sum / values.size();
    }

}
