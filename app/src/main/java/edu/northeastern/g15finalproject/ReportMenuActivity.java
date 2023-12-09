package edu.northeastern.g15finalproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReportMenuActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private double lat = 42.361145;
    private double lng = -71.057083;

    private String zipcode = "02215";

    private List<Report> qualifiedObjectsList = new ArrayList<>();
    private List<Report> nearbyReports = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_overall);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        String testZip = "02215";
        CompletableFuture<List<Report>> futureReports = getNearbyReport(zipcode);
        executeAsyncTask(futureReports, new ReportNearbyActivity.TaskCallback() {
            @Override
            public void onTaskComplete(List<Report> result) {
                nearbyReports = result;
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    // Update global variables with precise location
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    Log.d("Location", "Latitude: " + lat + ", Longitude: " + lng);
                }
            }
        };

        checkAndRequestLocationPermission();
    }


    private void checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, request location updates
            requestLocationUpdates();
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    private void requestLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest()
                .setInterval(10000) // Set the desired interval for location updates in milliseconds
                .setFastestInterval(5000) // Set the fastest interval for location updates in milliseconds
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, request location updates
                requestLocationUpdates();
            } else {
                // Location permission denied, set default values
                Log.d("Location", "Location permission denied");
                lat = 42.361145; // Default latitude
                lng = -71.057083; // Default longitude
            }
        }
    }

    public void createReport(View view) {
        startActivity(new Intent(this, ReportActivity.class));
    }

    public void showReportNearby(View view) {
        startActivity(new Intent(this, ReportNearbyActivity.class));
    }

    public CompletableFuture<List<Report>>  getNearbyReport(String zipcode) {
        ///////////////////////////////////////
        //test pull out reports from db

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference placeReference = databaseReference.child("report");

//        String targetZipcode = "02186";
        Query query = placeReference.orderByChild("zipcode").equalTo(zipcode);

        CompletableFuture<List<Report>> future = new CompletableFuture<>();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the list to avoid duplicates if the method is called multiple times
                qualifiedObjectsList.clear();

                // Iterate through the results
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the object

                    System.out.println("snapshot");
                    System.out.println(snapshot);

                    Report archiveReport = snapshot.getValue(Report.class);

//                    YourObject yourObject = snapshot.getValue(YourObject.class);

                    // Add the qualified object to the list
                    qualifiedObjectsList.add(archiveReport);
                }
                future.complete(qualifiedObjectsList);

                System.out.println("I am here");
                // Do something with the list of qualified objects
                printQualifiedObjects(qualifiedObjectsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error: " + error.getMessage());
                future.completeExceptionally(error.toException());
            }
        });
        return future;

        /////////////////////////////////////////////////////////////////////////////
//        return qualifiedObjectsList;
    }

    private void printQualifiedObjects(List<Report> qualifiedObjectsList) {
        // Print the list of qualified objects to the Logcat
//        for (YourObject yourObject : qualifiedObjectsList) {
//            Log.d("YourActivity", "Name: " + yourObject.getName() + ", Zipcode: " + yourObject.getZipcode());
//        }
        System.out.println("Qualified object list: ");
        for (Report report : qualifiedObjectsList) {
            System.out.println(report.getCity() + " " + report.getZipcode());
//            Log.d("YourActivity", "Name: " + report.getName() + ", Zipcode: " + yourObject.getZipcode());
        }
    }

    public interface TaskCallback {
        void onTaskComplete(List<Report> result);
    }

    private void executeAsyncTask(CompletableFuture<List<Report>> futureReports, ReportNearbyActivity.TaskCallback callback) {
        new AsyncTask<Void, Void, List<Report>>() {
            @Override
            protected List<Report> doInBackground(Void... voids) {
                System.out.println("doInBackground result: " + futureReports.join());
                return futureReports.join();
            }

            @Override
            protected void onPostExecute(List<Report> result) {
                System.out.println("onPostExecute result: " + result);

                // Call the callback with the result
                callback.onTaskComplete(result);

                // Save the result to the nearbyReports variable
//                nearbyReports = result;

                ListView listView = findViewById(R.id.customListView);
//        NearbyReportAdapter nearbyReportAdapter =
//                new NearbyReportAdapter(getApplicationContext(), time, type, address);

                NearbyReportAdapter nearbyReportAdapter =
                        new NearbyReportAdapter(getApplicationContext(), result);
                listView.setAdapter(nearbyReportAdapter);
            }
        }.execute();
    }

}
