package edu.northeastern.g15finalproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.core.Repo;

import org.w3c.dom.ls.LSException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import edu.northeastern.g15finalproject.DataClasses.Post;

public class AddPostActivity extends AppCompatActivity {

    private static int hourOfDay;
    private static int minute;
    private static int year;
    private static int month;
    private static int day;
    private Location knownLocation;

    private List<Report> selectedReports = new ArrayList<>();

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationResult locationResult;

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

            ((AddPostActivity)getActivity()).updateTime();
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

            ((AddPostActivity)getActivity()).updateDate();
        }
    }

    public void updateDate() {
        TextView dateText = findViewById(R.id.date_text);
        dateText.setText(new StringBuilder().append(month + 1).append("/").append(day).append("/").append(year).append(" "));
    }

    public void updateTime() {
        TextView timeText = findViewById(R.id.time_text);
        timeText.setText(new StringBuilder().append(hourOfDay).append(":").append(minute).append(" "));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        // Get intent
        Intent intent = getIntent();
        if (intent.hasExtra("location")) {
            // Set location
            knownLocation = intent.getParcelableExtra("location");
        }

        findViewById(R.id.time_text).setOnClickListener( v -> {
                    new TimePickerFragment().show(getSupportFragmentManager(), "timePicker");
                }
        );

        findViewById(R.id.date_text).setOnClickListener( v -> {
                    new DatePickerFragment().show(getSupportFragmentManager(), "datePicker");
                }
        );

        RadioButton gpsRadioButton = findViewById(R.id.add_post_location_gps);
        gpsRadioButton.setOnClickListener(v -> {
            // Check if location is known
            if (knownLocation == null) {
                // Disable GPS radio button and display error message
                gpsRadioButton.setEnabled(false);
                Toast.makeText(getApplicationContext(), "Location not available", Toast.LENGTH_SHORT).show();
            } else {
                // Enable GPS radio button
                gpsRadioButton.setEnabled(true);
                TextView locationText = findViewById(R.id.add_post_selected_location);
                locationText.setText("Selected Location : " + knownLocation.getLatitude() + ", " + knownLocation.getLongitude());
            }
        });

        // RadioButton None = findViewById(R.id.add_post_location_manual);

        // Set recyclerview showing the reports
        RecyclerView selectReportslistView = findViewById(R.id.add_post_attach_reports_recycler_view);
        List<Report> reportList = new ArrayList<>();

        // Use async task to get the reports
        AsyncTask.execute(() -> {
            // Get the reports
            List<Report> reportList1 = new ArrayList<>();

            // Create a FirebaseRTDB instance
            com.google.firebase.database.FirebaseDatabase firebaseDatabase = com.google.firebase.database.FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReferenceFromUrl(getString(R.string.firebase_database_url) + "/report");

            // Get current user from shared preferences
            SharedPreferences sharedPreferences = getSharedPreferences("userdata", MODE_PRIVATE);
            String currentUserName = sharedPreferences.getString("currentUserName", null);

            // Get the reports
            databaseReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Get the reports
                    for (com.google.firebase.database.DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        // Get the report
                        String street_address = dataSnapshot.child("street_address").getValue(String.class);
                        String city = dataSnapshot.child("city").getValue(String.class);
                        String state = dataSnapshot.child("state").getValue(String.class);
                        String zipcode = dataSnapshot.child("zipcode").getValue(String.class);
                        String detail = dataSnapshot.child("detail").getValue(String.class);
                        String type = dataSnapshot.child("type").getValue(String.class);
                        String username = dataSnapshot.child("username").getValue(String.class);
                        if (!username.equals(currentUserName)) {
                            // Skip the report if it is not the current user's report. They can only attach their own reports
                            continue;
                        }
                        Long time = dataSnapshot.child("time").getValue(Long.class);
                        Boolean isTesting = dataSnapshot.child("isTesting").getValue(Boolean.class);
                        double latitude = dataSnapshot.child("latitude").getValue(double.class);
                        double longitude = dataSnapshot.child("longitude").getValue(double.class);

                        // Create the report
                        Report report = new Report(
                                street_address,
                                city,
                                state,
                                zipcode,
                                detail,
                                type,
                                username,
                                time,
                                isTesting,
                                latitude,
                                longitude
                        );
                        reportList1.add(report);
                    }
                } else {
                    // Failed to get the reports
                    Toast.makeText(getApplicationContext(), "Failed to get reports", Toast.LENGTH_SHORT).show();
                }
            });

            while (reportList1.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            reportList.addAll(reportList1);

            runOnUiThread(() -> {
                selectReportslistView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
                selectReportslistView.setAdapter(new AttachReportAdapter(this, reportList));
                selectReportslistView.getAdapter().notifyDataSetChanged();
            });
        });

        /*
        public Report(String street_address, String city, String state, String zipcode, String detail,
                  String type, String username, Long time, Boolean isTesting,
                  double latitude, double longitude)
         */
        // reportList.add(
        //         new Report(
        //                 "123 Main St",
        //                 "Boston",
        //                 "MA",
        //                 "02115",
        //                 "This is a test report",
        //                 "People Loitering",
        //                 "tabbas97",
        //                 1234567890L,
        //                 true,
        //                 42.3601,
        //                 -71.0589
        //         )
        // );
        // reportList.add(
        //         new Report(
        //                 "123 Main St",
        //                 "Boston",
        //                 "MA",
        //                 "02115",
        //                 "This is a test report2",
        //                 "People Loitering",
        //                 "tabbas97",
        //                 1234567890L,
        //                 true,
        //                 42.3601,
        //                 -71.0589
        //         )
        // );
        //
        // runOnUiThread(() -> {
        //     selectReportslistView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        //     selectReportslistView.setAdapter(new AttachReportAdapter(this, reportList));
        //     selectReportslistView.getAdapter().notifyDataSetChanged();
        // });

        System.out.println("SET ADAPTER SUCCESSFUL");

        Button submitButton = findViewById(R.id.add_post_submit_button);
        submitButton.setOnClickListener(
                v -> {

                    if ((AttachReportAdapter)selectReportslistView.getAdapter() == null) {
                        Toast.makeText(getApplicationContext(), "No reports selected", Toast.LENGTH_SHORT).show();
                        selectedReports = new ArrayList<>();
                    } else {
                        selectedReports = ((AttachReportAdapter)selectReportslistView.getAdapter()).getSelectedReports();
                    }

                    System.out.println("SUBMIT BUTTON CLICKED");
                    System.out.println("Reports Selected : " + selectedReports.size());

                    // Validate Post Title
                    TextView postTitle = findViewById(R.id.add_post_title);

                    if (postTitle.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Post title cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // May contain only alphanumeric characters, spaces, and hyphens
                    if (!postTitle.getText().toString().matches("^[a-zA-Z0-9\\s-]*$")) {
                        Toast.makeText(getApplicationContext(), "Post title must contain only alphanumeric characters, spaces, and hyphens", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Validate Post Body
                    TextView postBody = findViewById(R.id.add_post_body);

                    if (postBody.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Post body cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    RadioButton manualRadioButton = findViewById(R.id.add_post_location_manual);

                    // Validate Post Location is selected
                    if ((!gpsRadioButton.isChecked()) && !manualRadioButton.isChecked()) {
                        Toast.makeText(getApplicationContext(), "Post location option must be selected", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // If GPS is selected, check if location is known
                    if (gpsRadioButton.isChecked() && knownLocation == null) {
                        Toast.makeText(getApplicationContext(), "Location not available. Please enter manually", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    TextView locationText = findViewById(R.id.add_post_selected_location);
                    // If Manual is selected, check if location is entered
                    if (manualRadioButton.isChecked()) {

                        if (locationText.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Location must be entered", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    // Check if location is valid in the form of latitude, longitude
                    // Remove the "Selected Location : " from the string
                    String[] location = locationText.getText().toString().split(":")[1].split(",");
                    if (location.length != 2) {
                        Toast.makeText(getApplicationContext(), "Location must be entered in the form of latitude, longitude", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double latitude;
                    double longitude;

                    try {
                        latitude = Double.parseDouble(location[0].strip());
                        longitude = Double.parseDouble(location[1].strip());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Location must be entered in the form of latitude, longitude as numbers", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (latitude < -90 || latitude > 90) {
                        Toast.makeText(getApplicationContext(), "Latitude must be between -90 and 90", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (longitude < -180 || longitude > 180) {
                        Toast.makeText(getApplicationContext(), "Longitude must be between -180 and 180", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (hourOfDay == 0 && minute == 0 && year == 0 && month == 0 && day == 0) {
                        Toast.makeText(getApplicationContext(), "Date and time must be entered", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Create Post

                        /*
                        Post Format (JSON)

                       {
                          "attached_report": [
                            null,
                            2
                          ] - can be empty list,
                          "body": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam in scelerisque sem. Mauris         volutpat, dolor id interdum ullamcorper, risus dolor egestas lectus, sit amet mattis purus         dui nec risus. Maecenas non sodales nisi, vel dictum dolor. Class aptent taciti sociosqu ad         litora torquent per conubia nostra, per inceptos himenaeos. Suspendisse blandit eleifend         diam, vel rutrum tellus vulputate quis. Aliquam eget libero aliquet, imperdiet nisl a,         ornare ex. Sed rhoncus est ut libero porta lobortis. Fusce in dictum tellus.\\n\\n         Suspendisse interdum ornare ante. Aliquam nec cursus lorem. Morbi id magna felis. Vivamus         egestas, est a condimentum egestas, turpis nisl iaculis ipsum, in dictum tellus dolor sed         neque. Morbi tellus erat, dapibus ut sem a, iaculis tincidunt dui. Interdum et malesuada         fames ac ante ipsum primis in faucibus. Curabitur et eros porttitor, ultricies urna vitae,         molestie nibh. Phasellus at commodo eros, non aliquet metus. Sed maximus nisl nec dolor         bibendum, vel congue leo egestas.\\n\\n         Sed interdum tortor nibh, in sagittis risus mollis quis. Curabitur mi odio, condimentum sit         amet auctor at, mollis non turpis. Nullam pretium libero vestibulum, finibus orci vel,         molestie quam. Fusce blandit tincidunt nulla, quis sollicitudin libero facilisis et. Integer         interdum nunc ligula, et fermentum metus hendrerit id. Vestibulum lectus felis, dictum at         lacinia sit amet, tristique id quam. Cras eu consequat dui. Suspendisse sodales nunc ligula,         in lobortis sem porta sed. Integer id ultrices magna, in luctus elit. Sed a pellentesque         est.\\n\\n         Aenean nunc velit, lacinia sed dolor sed, ultrices viverra nulla. Etiam a venenatis nibh.         Morbi laoreet, tortor sed facilisis varius, nibh orci rhoncus nulla, id elementum leo dui         non lorem. Nam mollis ipsum quis auctor varius. Quisque elementum eu libero sed commodo. In         eros nisl, imperdiet vel imperdiet et, scelerisque a mauris. Pellentesque varius ex nunc,         quis imperdiet eros placerat ac. Duis finibus orci et est auctor tincidunt. Sed non viverra         ipsum. Nunc quis augue egestas, cursus lorem at, molestie sem. Morbi a consectetur ipsum, a         placerat diam. Etiam vulputate dignissim convallis. Integer faucibus mauris sit amet finibus         convallis.\\n\\n         Phasellus in aliquet mi. Pellentesque habitant morbi tristique senectus et netus et         malesuada fames ac turpis egestas. In volutpat arcu ut felis sagittis, in finibus massa         gravida. Pellentesque id tellus orci. Integer dictum, lorem sed efficitur ullamcorper,         libero justo consectetur ipsum, in mollis nisl ex sed nisl. Donec maximus ullamcorper         sodales. Praesent bibendum rhoncus tellus nec feugiat. In a ornare nulla. Donec rhoncus         libero vel nunc consequat, quis tincidunt nisl eleifend. Cras bibendum enim a justo luctus         vestibulum. Fusce dictum libero quis erat maximus, vitae volutpat diam dignissim.",
                          "comments": {},
                          "plus_one": {},
                          "testing": false,
                          "time": 16993263732 - UTC time in milliseconds,
                          "title": "bhk",
                          "username": "u1" - currentUsername from shared preferences
                        }
                         */

                    SharedPreferences sharedPreferences = getSharedPreferences("userdata", MODE_PRIVATE);
                    String currentUserName = sharedPreferences.getString("currentUserName", null);

                    if (currentUserName == null) {
                        Toast.makeText(getApplicationContext(), "User not logged in", Toast.LENGTH_SHORT).show();

                        // Go to login activity
                        Intent loginIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                        // Clear the stack
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginIntent);
                    }

                    // Convert the date and time to UTC time in milliseconds
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day, hourOfDay, minute);
                    long time = calendar.getTimeInMillis();

                    // Get unique post id
                    String postID = java.util.UUID.randomUUID().toString();

                    // Make Location object
                    Location location1 = new Location("");
                    location1.setLatitude(latitude);
                    location1.setLongitude(longitude);

                    // Create post
                    Post post = new Post(
                                currentUserName,
                                postTitle.getText().toString(),
                                postBody.getText().toString(),
                                postID,
                                "",
                                time,
                                false,
                                location1
                        );

                    // Create a FirebaseRTDB instance
                    com.google.firebase.database.FirebaseDatabase firebaseDatabase = com.google.firebase.database.FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReferenceFromUrl(getString(R.string.firebase_database_url) + "/post");

                    // Create the ref to the post and add children of the post
                    DatabaseReference postRef = databaseReference.child(postID);
                    postRef.child("username").setValue(post.username);
                    postRef.child("title").setValue(post.title);
                    postRef.child("body").setValue(post.body);
                    postRef.child("postId").setValue(post.postId);
                    postRef.child("attached_report").setValue(selectedReports);
                    postRef.child("time").setValue(post.time);
                    postRef.child("testing").setValue(post.testing);
                    postRef.child("location").setValue(post.location);
                }
        );
    }
}