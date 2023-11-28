package edu.northeastern.g15finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void testFB(View view) {
        startActivity(new Intent(this, TestActivity.class));

        //test distance matrix api, (latitude, longitude)

        String distance = "https://api.distancematrix.ai/maps/api/distancematrix/json";

        //sample origin lat long coordinates: boston 42.3601° N, 71.0589° W
        double ori_lat = 42.3601;
        double ori_lng = -71.0589;

        //sample destination lat, long coordinate: nyc 40.7128° N, 74.0060° W
        double des_lat = 40.7128;
        double des_lng = -74.0060;

//        TestThread testThread = new TestThread();
//        new Thread(testThread).start();



    }

    public void report(View view) {
        startActivity(new Intent(MainActivity.this, ReportMenuActivity.class));
    }


    // calculate distance between two sets of coordinates
    class TestThread implements Runnable {


        TestThread() {

        }

        @Override
        public void run() {
            String baseURL = "https://api.distancematrix.ai/maps/api/distancematrix/json";
            //sample origin lat long coordinates: boston 42.3601° N, 71.0589° W
            double ori_lat = 42.3601;
            double ori_lng = -71.0589;

            //sample destination lat, long coordinate: nyc 40.7128° N, 74.0060° W
            double des_lat = 40.7128;
            double des_lng = -74.0060;

            String APIKey = "9taq8wnFt2lMU3msWJItSOng1Rf3aoPXzb6T9TXkhQVqslGNleB0RCbj1nt01k7J";
            String urlString = String.format("%s?origins=%s,%s&destinations=%s,%s&key=%s",
                    baseURL, ori_lat, ori_lng, des_lat, des_lng, APIKey);
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                System.out.println("response code"+ responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream response = connection.getInputStream();
                    java.util.Scanner s = new java.util.Scanner(response).useDelimiter("\\A");
                    String responseString = s.hasNext() ? s.next() : "";
                    // Ensure to close the input stream
                    response.close();
                    // Ensure proper termination of the response string
                    responseString += "\0";
                    System.out.println("Response: " + responseString);

//

//                    System.out.println("final distance: " + distanceText);
                    JSONObject responseJSON = new JSONObject(responseString);
                    System.out.println("Respons JSON" + responseJSON);
                    JSONArray responseArray = responseJSON.getJSONArray("rows");
                    System.out.println(responseArray);
//                    responseArray.getJSONArray(0);

                    String distanceString = responseArray.getJSONObject(0)
                            .getJSONArray("elements")
                            .getJSONObject(0).getJSONObject("distance").getString("text");
                    System.out.println("distance string: " + distanceString);
                    double distancedouble = Double.parseDouble(distanceString.split(" ")[0]);
                    System.out.println("distance double: " + distancedouble);

//                    System.out.println(responseArray.getJSONObject(0)
//                            .getJSONArray("elements")
//                            .getJSONObject(0).getJSONObject("distance").getString("text"));


//                    System.out.println("test print distance: "
//                                    + responseJSON.getJSONObject("rows"));
////                            .getJSONObject("distance"));
//                    String distance = responseJSON.getJSONObject("rows").getJSONObject("elements")
//                            .getJSONObject("distance").getString("text");
//                    System.out.println("final distance: "+ distance);
                }


            } catch (Exception e) {
                Log.e("MainError","Error in testing distance API");
            }
        }
    }
}