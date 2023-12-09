package edu.northeastern.g15finalproject.Helpers;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.northeastern.g15finalproject.DataClasses.CustomWeightedLatLong;
import edu.northeastern.g15finalproject.DataClasses.Report;

public class HeatmapHelper {

    public static List<Report> transformReports(Map<String, Object> allReportsFromFirebase) {
        List<Report> reports = new ArrayList<>();

        if (allReportsFromFirebase == null) {
            System.out.println("Reports ret THREAD : No reports found");
            throw new IllegalArgumentException("Reports ret THREAD : No reports found");
        }

        // Iterate through the reports hashmap
        for (Map.Entry<String, Object> reportEntry : allReportsFromFirebase.entrySet()) {

            // Try catch block to catch the case where the report is null or invalid format
            try {

                System.out.println("Reports ret THREAD : Key: " + reportEntry.getKey());
                System.out.println("Reports ret THREAD : Value: " + reportEntry.getValue());

                // Get the report hashmap
                Map<String, Object> reportMap = (Map<String, Object>) reportEntry.getValue();

                // Get the report attributes
                String username = (String) reportMap.get("username");
                String type = (String) reportMap.get("type");
                String detail = (String) reportMap.get("detail");
                String street_address = (String) reportMap.get("street_address");
                String city = (String) reportMap.get("city");
                String state = (String) reportMap.get("state");
                String zipcode = (String) reportMap.get("zipcode");
                double latitude = (double) reportMap.get("latitude");
                double longitude = (double) reportMap.get("longitude");
                long time = (long) reportMap.get("time");
                boolean testing = (boolean) reportMap.get("testing");

                // Create a report object
                Report report = new Report(username, type, detail, street_address, city, state, zipcode, latitude, longitude, time, testing);

                // Add the report to the reports list
                reports.add(report);

                System.out.println("Reports ret THREAD : Report: " + report);
            } catch (Exception e) {
                System.out.println("Reports ret THREAD : Failed to parse report");
                System.out.println("Reports ret THREAD : Failed Report Key: " + reportEntry.getKey());
                System.out.println("Reports ret THREAD : Failed Report: " + reportEntry.getValue());
                e.printStackTrace();
            }
        }
        return reports;
    }

    private static float getDistance(LatLng latLng1, LatLng latLng2) {
        // Get the distance between two latlngs
        Location location1 = new Location("");
        location1.setLatitude(latLng1.latitude);
        location1.setLongitude(latLng1.longitude);

        Location location2 = new Location("");
        location2.setLatitude(latLng2.latitude);
        location2.setLongitude(latLng2.longitude);

        return location1.distanceTo(location2);
    }

    private static int getMatchingLatLng(List<CustomWeightedLatLong> prevSet, LatLng latLng) {
        // Iterate through the previous set of weighted latlngs
        for (int i = 0; i < prevSet.size(); i++) {
            // Get the weighted latlng
            CustomWeightedLatLong weightedLatLong = prevSet.get(i);

            // Get the latlng
            LatLng prevLatLng = new LatLng(weightedLatLong.getLatitude(), weightedLatLong.getLongitude());

            // Check if they are close enough
            if (getDistance(prevLatLng, latLng) < 0.0001) {
                // Return the index of the matching latlng
                return i;
            }
        }

        // Return -1 if no matching latlng was found
        return -1;
    }

    public static List<CustomWeightedLatLong> makeWeightedLatLongs(List<Report> allReports, List<CustomWeightedLatLong> previousSet) {
        List<CustomWeightedLatLong> weightedLatLngs = new ArrayList<>();

        if (allReports == null) {
            System.out.println("Reports ret THREAD : No reports found");
            throw new IllegalArgumentException("Reports ret THREAD : No reports found");
        }

        // Iterate through the reports hashmap
        for (Report report : allReports) {
            // Try catch block to catch the case where the report is null or invalid format
            try {
                // Get the report attributes
                double latitude = report.latitude;
                double longitude = report.longitude;

                // Get the matching latlng index
                int matchingLatLngIndex = getMatchingLatLng(previousSet, new LatLng(latitude, longitude));

                // Check if there is a matching latlng
                if (matchingLatLngIndex != -1) {
                    // Get the matching latlng
                    CustomWeightedLatLong matchingLatLng = previousSet.get(matchingLatLngIndex);

                    // Update the matching latlng's weight
                    matchingLatLng.setWeight(matchingLatLng.getWeight() + report.getIntensity());

                    // Update the matching latlng in the previous set
                    previousSet.set(matchingLatLngIndex, matchingLatLng);
                } else {
                    // Create a weighted latlng object
                    CustomWeightedLatLong weightedLatLng = new CustomWeightedLatLong(latitude, longitude, report.getIntensity());

                    // Add the weighted lat-lng to the list
                    weightedLatLngs.add(weightedLatLng);
                }
            }
            catch (Exception e) {
                System.out.println("Reports ret THREAD : Failed to parse report");
                System.out.println("Reports ret THREAD : Failed Report: " + report);
                e.printStackTrace();
            }

        }
        return weightedLatLngs;
    }

    private static List<WeightedLatLng> convertCustomWeightedLatLongs2WeightedLatLngs(List<CustomWeightedLatLong> customWeightedLatLongs) {
        List<WeightedLatLng> weightedLatLngs = new ArrayList<>();

        // Iterate through the custom weighted latlngs
        for (CustomWeightedLatLong customWeightedLatLong : customWeightedLatLongs) {
            // Create a weighted latlng object
            WeightedLatLng weightedLatLng = new WeightedLatLng(
                    new com.google.android.gms.maps.model.LatLng(customWeightedLatLong.getLatitude(), customWeightedLatLong.getLongitude()), customWeightedLatLong.getWeight());

            // Add the weighted lat-lng to the list
            weightedLatLngs.add(weightedLatLng);
        }

        return weightedLatLngs;
    }

    public static HeatmapTileProvider transformReportsToHeatmapTileProvider(List<Report> reports, List<CustomWeightedLatLong> previousSet) {
        HeatmapTileProvider heatmapTileProvider = null;

        if (reports == null) {
            System.out.println("Reports ret THREAD : No reports found");
            throw new IllegalArgumentException("Reports ret THREAD : No reports found");
        }

        if (previousSet == null) {
            // Computing for the first time or the previous set is expired
            previousSet = makeWeightedLatLongs(reports, new ArrayList<>());
        } else {
            // Update the previous set
            previousSet = makeWeightedLatLongs(reports, previousSet);
        }

        // Convert the custom weighted latlngs to weighted latlngs
        List<WeightedLatLng> weightedLatLngs = convertCustomWeightedLatLongs2WeightedLatLngs(previousSet);

        // gradient for the heatmap
        int[] colors = {
                android.graphics.Color.rgb(102, 225, 0), // green
                android.graphics.Color.rgb(255, 0, 0)    // red
        };

        // start points for the gradient
        float[] startPoints = {
                1f, 3f
        };

        System.out.println("Returning heatmap tile provider");

        try {
            heatmapTileProvider = new HeatmapTileProvider.Builder()
                    .weightedData(weightedLatLngs)
                    .opacity(1)
                    .maxIntensity(3)
                    .build();
            System.out.println("Created heatmap tile provider");
        } catch (Exception e) {
            System.out.println("Failed to create heatmap tile provider");
            e.printStackTrace();
        }

        // Create a heatmap tile provider
        return heatmapTileProvider;
    }
}
