package edu.northeastern.g15finalproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;


public class ReportActivity extends AppCompatActivity {
    private static final String API_KEY = "40c35a44baff4920bfcd47f63a9d247b";
    private static final String BASE_URL = "https://api.opencagedata.com/geocode/v1/json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = findViewById(R.id.toolbarNewReport);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void addReport(View view) throws ExecutionException, InterruptedException {
        String street_address = ((TextView)findViewById(R.id.street_address_input)).getText().toString();
        String city = ((TextView)findViewById(R.id.city_input)).getText().toString();
        String state = ((TextView)findViewById(R.id.state_input)).getText().toString();
        String zipcode = ((TextView)findViewById(R.id.zipcode_input)).getText().toString();
        String detail = ((TextView)findViewById(R.id.report_detail_input)).getText().toString();
        String type = ((TextView)findViewById(R.id.type_input)).getText().toString();
//        Long time = Long.valueOf(((TextView)findViewById(R.id.time_input)).getText().toString());
        String time = ((TextView)findViewById(R.id.time_input)).getText().toString();

        String utcTimePattern = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z";

        Boolean isTimeFormatCorrect = true;
        if (!time.matches(utcTimePattern)) {
            ((TextView)findViewById(R.id.time_input)).setText("");
            isTimeFormatCorrect = false;
            showToast("Time has to be in the following format: YYYY-MM-DDTHH:mm:ss.SSSZ");
            // Valid UTC time in the specified format
        }

        if (isInputEmpty(street_address) || isInputEmpty(detail) || isInputEmpty(city)
                || isInputEmpty(state) || isInputEmpty(zipcode)
                || isInputEmpty(type) || isInputEmpty(String.valueOf(time))) {
            showToast("Address and details cannot be empty");
        } else {
            if (isTimeFormatCorrect) {
                String username = getCurrentloginUsername();
                Boolean isTesting = true;

                String fullAddress = street_address + ", " + city + ", " + state + ", " + zipcode;

                //TestGetCoordinates
                String urlstring = String.format("%s?q=%s&key=%s", BASE_URL, fullAddress, API_KEY);
                GetCoordinates getCoordinates = new GetCoordinates();
                List<Double> coordinates = getCoordinates.execute(urlstring).get();
                System.out.println("Coordinates: " + coordinates);
                double lat = coordinates.get(0);
                double lng = coordinates.get(1);

                Long timestamp = convertUTCtoTimestampLong(time);

                Report newReport = new Report(street_address, city, state, zipcode,
                        detail, type, username, timestamp,
                        isTesting, lat, lng);
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
    }

    private boolean isInputEmpty(String input) {
        return input.trim().isEmpty();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private long convertUTCtoTimestampLong(String utcTimeString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));


        try {
            // Parse the UTC time string
            Date utcDate = sdf.parse(utcTimeString);

            // Convert Date to timestamp
            long timestamp = utcDate.getTime();

            System.out.println("UTC Time: " + utcTimeString);
            System.out.println("Timestamp: " + timestamp);
            return timestamp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long currentTimestamp = System.currentTimeMillis();
        return currentTimestamp;
    }

    private String getCurrentloginUsername() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String loggedInUsername = preferences.getString("username", "default_value_if_not_found");
        System.out.println("loggedin username: " +loggedInUsername);
        return loggedInUsername;
    }

}
