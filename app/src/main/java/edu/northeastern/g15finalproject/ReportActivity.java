package edu.northeastern.g15finalproject;

import android.os.Bundle;
import android.util.Log;
import android.util.MalformedJsonException;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class ReportActivity extends AppCompatActivity {
    private static final String API_KEY = "40c35a44baff4920bfcd47f63a9d247b";
    private static final String BASE_URL = "https://api.opencagedata.com/geocode/v1/json";
    private static final String Query = "New York";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = findViewById(R.id.toolbarNewReport);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void addReport(View view) throws IOException, ExecutionException, InterruptedException {
        String street_address = ((TextView)findViewById(R.id.street_address_input)).getText().toString();
        String city = ((TextView)findViewById(R.id.city_input)).getText().toString();
        String state = ((TextView)findViewById(R.id.state_input)).getText().toString();
        String zipcode = ((TextView)findViewById(R.id.zipcode_input)).getText().toString();
        String detail = ((TextView)findViewById(R.id.report_detail_input)).getText().toString();
        String type = ((TextView)findViewById(R.id.type_input)).getText().toString();
        String time = ((TextView)findViewById(R.id.time_input)).getText().toString();

        //TestGetCoordinates
        String urlstring = String.format("%s?q=%s&key=%s", BASE_URL, Query, API_KEY);
        GetCoordinates getCoordinates = new GetCoordinates();
        List<Double> coordinates = getCoordinates.execute(urlstring).get();
        System.out.println("Coordinates: " + coordinates);



        ////////////////////




//THREAD is here

//        AThread aThread = new AThread(Query);
//        new Thread(aThread).start();


//        System.out.println("long from report: " + longitude);
///////////////////////////////////////
        //test pull out reports from db

//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference placeReference = databaseReference.child("report");
//
//        String targetZipcode = "02186";
//        Query query = placeReference.orderByChild("zipcode").equalTo(targetZipcode);
//        List<Report> qualifiedObjectsList = new ArrayList<>();
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Clear the list to avoid duplicates if the method is called multiple times
//                qualifiedObjectsList.clear();
//
//                // Iterate through the results
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    // Get the object
//
//                    Report archiveReport = snapshot.getValue(Report.class);
//
////                    YourObject yourObject = snapshot.getValue(YourObject.class);
//
//                    // Add the qualified object to the list
//                    qualifiedObjectsList.add(archiveReport);
//                }
//
//                System.out.println("I am here");
//                // Do something with the list of qualified objects
//                printQualifiedObjects(qualifiedObjectsList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("Firebase", "Error: " + error.getMessage());
//            }
//
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////                // Handle errors
////                Log.e("Firebase", "Error: " + databaseError.getMessage());
////            }
//        });

        /////////////////////////////////////////////////////////////////////////////

        //original
        if (isInputEmpty(street_address) || isInputEmpty(detail) || isInputEmpty(city)
                || isInputEmpty(state) || isInputEmpty(zipcode)
                || isInputEmpty(type) || isInputEmpty(time)) {
            showToast("Address and details cannot be empty");
        } else {
//            String type = "test type";
            String username = "test username";
//            String time = "hhmmss";
            Boolean isTesting = true;

            Report newReport = new Report(street_address, city, state, zipcode,
                                            detail, type, username, time, isTesting);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.getReference().child("report").push().setValue(newReport);
            ((TextView) findViewById(R.id.street_address_input)).setText("");
            ((TextView) findViewById(R.id.city_input)).setText("");
            ((TextView) findViewById(R.id.state_input)).setText("");
            ((TextView) findViewById(R.id.zipcode_input)).setText("");
            ((TextView)findViewById(R.id.report_detail_input)).setText("");
            ((TextView)findViewById(R.id.time_input)).setText("");
            ((TextView)findViewById(R.id.type_input)).setText("");
        }
    }


    private void printQualifiedObjects(List<Report> qualifiedObjectsList) {
        // Print the list of qualified objects to the Logcat
//        for (YourObject yourObject : qualifiedObjectsList) {
//            Log.d("YourActivity", "Name: " + yourObject.getName() + ", Zipcode: " + yourObject.getZipcode());
//        }
        System.out.println("Qualified object list: ");
        for (Report report : qualifiedObjectsList) {
            System.out.println(report.getCity() + report.getZipcode());
//            Log.d("YourActivity", "Name: " + report.getName() + ", Zipcode: " + yourObject.getZipcode());
        }
    }
//}




    private boolean isInputEmpty(String input) {
        return input.trim().isEmpty();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    class AThread implements Runnable {

        private String address;
        private double latitude;
        private double longitude;
//        private Callback callback;
        AThread(String address) throws MalformedJsonException, IOException {
            this.address = address;
        }

        @Override
        public void run() {
            Log.d("A thread", "Running on a different thread");

//            //test open cage with OKHTTPClient
//            OkHttpClient client = new OkHttpClient();
//            String url = String.format("%s?q=%s&key=%s", BASE_URL, Query, API_KEY);
//            Request request = new Request.Builder().url(url).build();
//
//            try {
//                Response response = client.newCall(request).execute();
//                Log.d("TAG", "Open Cage response: " + response);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }


            //test opencage with HTTPURLConnection
            String urlstring = String.format("%s?q=%s&key=%s", BASE_URL, Query, API_KEY);
            try {
                URL url = new URL(urlstring);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream response = connection.getInputStream();
                    java.util.Scanner s = new java.util.Scanner(response).useDelimiter("\\A");
                    String responseString = s.hasNext() ? s.next() : "";
                    // Ensure to close the input stream
                    response.close();
                    // Ensure proper termination of the response string
                    responseString += "\0";

                    System.out.println("RESPONSE : " + responseString);

                    JSONObject responseJSON = new JSONObject(responseString);
                    System.out.println("JSON: " + responseJSON);

                    JSONArray result = responseJSON.getJSONArray("results");
                    System.out.println(result);
                    System.out.println("lat: " +result.getJSONObject(0).getJSONObject("geometry").getDouble("lat"));
                    System.out.println("lng: " +result.getJSONObject(0).getJSONObject("geometry").getDouble("lng"));
                    double lat = result.getJSONObject(0).getJSONObject("geometry").getDouble("lat");
                    double lng = result.getJSONObject(0).getJSONObject("geometry").getDouble("lng");
                    this.latitude = lat;
                    this.longitude = lng;

                    Log.d("Tag", "Open cage response: " +response.toString());
//                    return response.toString();

                }

            } catch (Exception e) {
                Log.e("Error", "Open Cage response: " + e.getMessage());
            }


        }
    }
}
