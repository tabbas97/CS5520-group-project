package edu.northeastern.g15finalproject;

import android.os.Bundle;
import android.util.Log;
import android.util.MalformedJsonException;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReportActivity extends AppCompatActivity {
    private static final String API_KEY = "40c35a44baff4920bfcd47f63a9d247b";
    private static final String BASE_URL = "https://api.opencagedata.com/geocode/v1/json";
    private static final String Query = "New York";


    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
    }

    public void addReport(View view) throws IOException {
        String street_address = ((TextView)findViewById(R.id.street_address_input)).getText().toString();
        String city = ((TextView)findViewById(R.id.city_input)).getText().toString();
        String state = ((TextView)findViewById(R.id.state_input)).getText().toString();
        String zipcode = ((TextView)findViewById(R.id.zipcode_input)).getText().toString();
        String detail = ((TextView)findViewById(R.id.report_detail_input)).getText().toString();

        AThread aThread = new AThread(Query);
        new Thread(aThread).start();
//        System.out.println("long from report: " + longitude);

        if (isInputEmpty(street_address) || isInputEmpty(detail) || isInputEmpty(city)
                || isInputEmpty(state) || isInputEmpty(zipcode)) {
            showToast("Address and details cannot be empty");
        } else {
            Report newReport = new Report(street_address, city, state, zipcode, detail);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.getReference().child("report").push().setValue(newReport);
            ((TextView) findViewById(R.id.street_address_input)).setText("");
            ((TextView) findViewById(R.id.city_input)).setText("");
            ((TextView) findViewById(R.id.state_input)).setText("");
            ((TextView) findViewById(R.id.zipcode_input)).setText("");
            ((TextView)findViewById(R.id.report_detail_input)).setText("");
        }

    }

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
