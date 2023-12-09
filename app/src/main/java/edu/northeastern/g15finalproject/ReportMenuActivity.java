package edu.northeastern.g15finalproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

    private List<Report> qualifiedObjectsList = new ArrayList<>();
    private List<Report> nearbyReports = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_overall);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String testZip = "02148";
        CompletableFuture<List<Report>> futureReports = getNearbyReport(testZip);
        executeAsyncTask(futureReports, new ReportNearbyActivity.TaskCallback() {
            @Override
            public void onTaskComplete(List<Report> result) {
                nearbyReports = result;
            }
        });
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
