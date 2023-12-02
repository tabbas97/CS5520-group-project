package edu.northeastern.g15finalproject.Helpers;

import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public static HeatmapTileProvider transformReportsToHeatmapTileProvider(List<Report> reports) {
        HeatmapTileProvider heatmapTileProvider = null;

        if (reports == null) {
            System.out.println("Reports ret THREAD : No reports found");
            throw new IllegalArgumentException("Reports ret THREAD : No reports found");
        }

        List<WeightedLatLng> weightedLatLngs = new ArrayList<>();

        // Iterate through the reports hashmap
        for (Report report : reports) {
            // Try catch block to catch the case where the report is null or invalid format
            try {
                // Get the report attributes
                double latitude = report.latitude;
                double longitude = report.longitude;

                // Create a weighted latlng object
                WeightedLatLng weightedLatLng = new WeightedLatLng(
                        new com.google.android.gms.maps.model.LatLng(latitude, longitude), 3
                );

                // Add the weighted lat-lng to the list
                weightedLatLngs.add(weightedLatLng);

                System.out.println("Reports ret THREAD : Report: " + report);
            } catch (Exception e) {
                System.out.println("Reports ret THREAD : Failed to parse report");
                System.out.println("Reports ret THREAD : Failed Report: " + report);
                e.printStackTrace();
            }
        }

        // gradient for the heatmap
        int[] colors = {
                android.graphics.Color.rgb(102, 225, 0), // green
                android.graphics.Color.rgb(255, 0, 0)    // red
        };

        // start points for the gradient
        float[] startPoints = {
                0f, 3f
        };

        System.out.println("Returning heatmap tile provider");

        try {
            heatmapTileProvider = new HeatmapTileProvider.Builder()
                    .weightedData(weightedLatLngs)
                    .gradient(new Gradient(colors, startPoints))
                    .radius(30)
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
