package edu.northeastern.g15finalproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

public class ReportNearbyActivity extends AppCompatActivity {
    private List<Report> qualifiedObjectsList = new ArrayList<>();
    private List<Report> nearbyReports = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_nearby);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //test list view

        String currentZip = "96768";

//        String time[] = {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight"};
//        String type[] = {"Type 1", "Type 2", "Type 3", "Type 4", "Type 5", "Type 6", "Type 7", "Type 8"};
//        String address[] = {"Address 1", "Address 2", "Address 3", "Address 4", "Address 5", "Address 6", "Address 7", "Address 8"};
//
//        List<Report> reportList = new ArrayList<>();
//        Report report1 = new Report("test", "test", "test", "test", "test",
//                "test", "test", "test", true, 40.000, -70.000);
//        Report report2 = new Report("test2", "test2", "test2", "test2", "test2",
//                "test2", "test2", "test2", true, 40.000, -70.000);
//        Report report3 = new Report("test3", "test3", "test3", "test3", "test3",
//                "test3", "test3", "test3", true, 40.000, -70.000);

//        List<Report> reports = getNearbyReport(currentZip);
        List<Report> reports = new ArrayList<>();
        CompletableFuture<List<Report>> futureReports = getNearbyReport(currentZip);
//        List<Report> reports = futureReports.join();

//        reportList.add(report1);
//        reportList.add(report2);
//        reportList.add(report3);
//        try {
//            reports = new AsyncTask<Void, Void, List<Report>>() {
//
//                @Override
//                protected List<Report> doInBackground(Void... voids) {
//                    System.out.println("future reports: " + futureReports.join());
//
//                    return futureReports.join();
//                }
//            }.execute().get();
//        } catch (Exception e) {
//            Log.e("ReportNearbyActivity", "error in converting reports");
//        }

//        catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

//        new AsyncTask<Void, Void, List<Report>>() {
//
//            @Override
//            protected List<Report> doInBackground(Void... voids) {
//
//                return futureReports.join();
//            }
//        }.execute();
//
//
//        executeAsyncTask(futureReports, new TaskCallback() {
//            @Override
//            public void onTaskComplete(List<Report> result) {
//                List<Report> nearbyReports = result;
//                System.out.println("reports from onTaskComplete: " + reports);
//                ListView listView = findViewById(R.id.customListView);
////        NearbyReportAdapter nearbyReportAdapter =
////                new NearbyReportAdapter(getApplicationContext(), time, type, address);
//
//                NearbyReportAdapter nearbyReportAdapter =
//                        new NearbyReportAdapter(getApplicationContext(), reports);
//                listView.setAdapter(nearbyReportAdapter);
//            }
//        });

//        System.out.println();

         executeAsyncTask(futureReports, new TaskCallback() {
            @Override
            public void onTaskComplete(List<Report> result) {
                nearbyReports = result;
            }
        });

//        new AsyncTask<Void, Void, List<Report>>() {
//                @Override
//                protected List<Report> doInBackground(Void... voids) {
//                    System.out.println("future reports: " + futureReports.join());
//                    nearbyReports = futureReports.join();
//                    return futureReports.join();
//                }
//
//                @Override
//                protected void onPostExecute(List<Report> result) {
//                    // Call the callback with the result
//                    callback.onTaskComplete(result);
//
//                    // Save the result to the nearbyReports variable
//                    nearbyReports = result;
//                }
//            }.execute();



        ////////////********************************************************
//        System.out.println("reports from main: " + nearbyReports);
////        System.out.println("reports address from main: " + reports.get(0).getFullAddress());
//        ListView listView = findViewById(R.id.customListView);
////        NearbyReportAdapter nearbyReportAdapter =
////                new NearbyReportAdapter(getApplicationContext(), time, type, address);
//
//        NearbyReportAdapter nearbyReportAdapter =
//                new NearbyReportAdapter(getApplicationContext(), reports);
//        listView.setAdapter(nearbyReportAdapter);
        ////////////********************************************************

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

    private void executeAsyncTask(CompletableFuture<List<Report>> futureReports, TaskCallback callback) {
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

//    private void executeAsyncTask(CompletableFuture<List<Report>> futureReports, TaskCallback callback) {
//        new AsyncTask<Void, Void, List<Report>>() {
//            @Override
//            protected List<Report> doInBackground(Void... voids) {
//                return futureReports.join();
//            }
//
//            @Override
//            protected void onPostExecute(List<Report> result) {
//                // Call the callback with the result
//                callback.onTaskComplete(result);
//            }
//        }.execute();
//    }

}
