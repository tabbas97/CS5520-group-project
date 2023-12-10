package edu.northeastern.g15finalproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

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
    private static int hourOfDay;
    private static int minute;
    private static int year;
    private static int month;
    private static int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = findViewById(R.id.toolbarNewReport);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.hour_input).setOnClickListener( v -> {
                    new TimePickerFragment().show(getSupportFragmentManager(), "timePicker");
                }
        );

        findViewById(R.id.date_input).setOnClickListener( v -> {
                    new DatePickerFragment().show(getSupportFragmentManager(), "datePicker");
                }
        );
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker.
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it.
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay_p, int minute_p) {
            System.out.println("Time set");
            System.out.println(hourOfDay);
            System.out.println(minute);

            hourOfDay = hourOfDay_p;
            minute = minute_p;

            ((ReportActivity)getActivity()).updateTime();
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker.
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it.
            return new DatePickerDialog(requireContext(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year_p, int month_p, int day_p) {
            // Do something with the date the user picks.
            System.out.println("Date set");
            System.out.println(year);
            System.out.println(month);
            System.out.println(day);

            year = year_p;
            month = month_p;
            day = day_p;

            ((ReportActivity)getActivity()).updateDate();
        }
    }

    public void updateDate() {
        TextView dateText = findViewById(R.id.date_input);
        dateText.setText(new StringBuilder().append(month + 1).append("/").append(day).append("/").append(year).append(" "));
    }

    public void updateTime() {
        TextView timeText = findViewById(R.id.hour_input);
        timeText.setText(new StringBuilder().append(hourOfDay).append(":").append(minute).append(" "));
    }

    private static boolean isInitialized(Object... variables) {
        for (Object variable : variables) {
            if (variable == null) {
                return false;
            }
        }
        return true;
    }


    public void addReport(View view) throws ExecutionException, InterruptedException {
        String street_address = ((TextView)findViewById(R.id.street_address_input)).getText().toString();
        String city = ((TextView)findViewById(R.id.city_input)).getText().toString();
        String state = ((TextView)findViewById(R.id.state_input)).getText().toString();
        String zipcode = ((TextView)findViewById(R.id.zipcode_input)).getText().toString();
        String detail = ((TextView)findViewById(R.id.report_detail_input)).getText().toString();
        String type = ((TextView)findViewById(R.id.type_input)).getText().toString();
        String time = year + "-" + month + "-" +day + "T" + hourOfDay + ":" + minute + ":00.000Z";

        String utcTimePattern = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z";
        boolean timeAllInitialized = isInitialized(year, month, day, hourOfDay, minute);
        System.out.println("isTimeAllInitialized: " + timeAllInitialized);
        System.out.println(year+ month+ day+ hourOfDay+ minute);

        boolean yearInitialized;
        if (year + month + day == 0) {
            yearInitialized = false;
        } else {
            yearInitialized = true;
        }
        boolean hourInitialized;
        if (hourOfDay + minute == 0) {
            hourInitialized = false;
        } else {
            hourInitialized = true;
        }

        if (isInputEmpty(street_address) || isInputEmpty(detail) || isInputEmpty(city)
                || isInputEmpty(state) || isInputEmpty(zipcode)
                || isInputEmpty(type) || !yearInitialized || !hourInitialized) {
            showToast("Address and details cannot be empty");
        } else {

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
                ((TextView)findViewById(R.id.date_input)).setText("");
                ((TextView)findViewById(R.id.hour_input)).setText("");
                ((TextView)findViewById(R.id.type_input)).setText("");
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
