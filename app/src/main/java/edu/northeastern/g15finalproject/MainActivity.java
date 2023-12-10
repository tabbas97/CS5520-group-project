package edu.northeastern.g15finalproject;

import static edu.northeastern.g15finalproject.Helpers.HeatmapHelper.transformReports;
import static edu.northeastern.g15finalproject.Helpers.HeatmapHelper.transformReportsToHeatmapTileProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.room.RoomDatabase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

// Import location services
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationCallback;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.g15finalproject.DataClasses.Report;
import edu.northeastern.g15finalproject.Helpers.HeatmapHelper;

public class MainActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    Map<String, Boolean> permissions = new HashMap<>();

    Location deviceLocation;
    Location mapLocation;
    String locationLabel = "You are here";
    Boolean userLocationMapViewSync = true;

    EmergencyContactDatabase db = null;
    EmergencyContactDao emergencyContactDao = null;

    List<EmergencyContact> emergencyContacts = null;

    Fragment mapFragment = null;

    boolean isSOSButtonEnabled = false;
    boolean isLocationFullyEnabled = false;
    boolean isCommunicationFullyEnabled = false;

    SearchView searchLocation;
    FloatingActionButton sosButton;
    Thread tileMakerThread;

    enum RequestCode {
        BASIC_PERMISSION_MISSING_ACTIVITY(1);

        private final int value;

        RequestCode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private void enableLocationFeatures() {

        // Attempt to validate that we have location permissions

        // Get the approximate location of the device
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                // Use the location object to get the latitude and longitude
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                deviceLocation = location;
                System.out.println("Latitude: " + location.getLatitude());
                System.out.println("Longitude: " + location.getLongitude());
            }
        });
        fusedLocationProviderClient.getLastLocation().addOnFailureListener(this, e -> {
            System.out.println("Failed to get location");
        });

        mapFragment = new MainScreenMapFragment();

        // Setting listeners
        if (deviceLocation != null) {

            // Get the latitude and longitude of the device
            LatLng latLng = new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude());
            mapLocation = deviceLocation;

            locationLabel = "You are here";
        }
        else {
            // set map location to Northeastern University Boston Campus
            mapLocation = new Location("");
            mapLocation.setLatitude(42.3398);
            mapLocation.setLongitude(-71.0892);
            locationLabel = "Northeastern University Boston Campus";

            // Get the current location of the device
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(500);

            // Create a location callback
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    if (!userLocationMapViewSync) {
                        return;
                    }

                    if (locationResult == null) {
                        System.out.println("Location result is null");
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        // Update UI with location data
                        // ...
                        mapLocation = location;
                    }
                }
            };
        }

        // Set the map location to the mapLocation
        Bundle bundle = new Bundle();
        bundle.putParcelable("mapLocation", mapLocation);
        bundle.putString("locationLabel", locationLabel);
        mapFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_fragment, mapFragment).commit();

        // Start location updates if location permission is granted
        if (Boolean.TRUE.equals(permissions.get(Manifest.permission.ACCESS_FINE_LOCATION))) {
            fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );
        }

        // Add listener to the recenter button
        FloatingActionButton toggleButton = findViewById(R.id.location_recenter_button);
        toggleButton.setOnClickListener((buttonView) -> {
            ((MainScreenMapFragment)mapFragment).reCenterMap();
            buttonView.setVisibility(View.INVISIBLE);
        });

        System.out.println("TileMakerThread state: " + tileMakerThread.getState());

        if (tileMakerThread.getState() == Thread.State.NEW) {
            tileMakerThread.start();
        } else {
            // Only rerun tile creation
            new Thread(() -> {
                MakeTiles();
            }).start();
            // Validate if
        }

        isLocationFullyEnabled = true;
    }

    private void enableCommunicationFeatures() {
        // Update the SOS button and it's listener
        sosButton.setOnClickListener(view -> {

            // Emergency contacts load from shared preferences
            SharedPreferences sharedPref = getSharedPreferences("userdata", Context.MODE_PRIVATE);
            String emergencyContactsString = sharedPref.getString("emergencyContacts", null);

            System.out.println("SOS Button clicked");
            BasicPermissionMissingActivity.getMissingPermissions(this).forEach(permission -> {
                System.out.println("Permission Missing: " + permission);
            });

            // Start a background thread to send the SMSes
            Thread smsThread = new Thread(() -> {
                // Send SMSes to the emergency contacts

                Boolean anySMSsent = false;

                Gson gson = new Gson();

                Map<String, String> emContactMap = gson.fromJson(emergencyContactsString, Map.class);

                if (emergencyContacts == null) {
                    emergencyContacts = new ArrayList<>();
                }

                if (emContactMap == null) {
                    emContactMap = new HashMap<>();
                }

                for (Map.Entry<String, String> entry : emContactMap.entrySet()) {
                    emergencyContacts.add(
                            new EmergencyContact(
                                    entry.getKey().hashCode(),
                                    entry.getKey(),
                                    entry.getValue()
                            )
                    );
                }

                for (EmergencyContact emergencyContact : emergencyContacts) {
                    System.out.println("Sending SMS to: " + emergencyContact.phoneNumber);
                    // Send SMS to the emergency contact
                    SmsManager smsManager = SmsManager.getDefault();

                    // Send String
                    String sendString = "I need help";
                    if (mapLocation != null) {
                        sendString += " at " + mapLocation.getLatitude() + ", " + mapLocation.getLongitude();
                    }
                    smsManager.sendTextMessage(emergencyContact.phoneNumber, null, sendString, null, null);
                    anySMSsent = true;
                }

                if (anySMSsent) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "SMS sent to emergency contacts", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "No emergency contacts found", Toast.LENGTH_SHORT).show();
                    });
                }
            });

            smsThread.start();

            Intent intent = new Intent(Intent.ACTION_CALL);
            //                intent.setData(Uri.parse("tel:+16198970892")); // Test number
            intent.setData(Uri.parse("tel:911")); // Emergency number
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                this.startActivity(intent);

                // Get instance of Vibrator from current Context
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                // Initiate a long two pulse vibration
                VibrationEffect vibrationEffect = VibrationEffect.createWaveform(
                        new long[]{
                                0, 2000, 100, 2000, 100, 2000
                        }, -1
                );

                // Vibrate for 400 milliseconds
                v.vibrate(vibrationEffect);
            }
        });

        isCommunicationFullyEnabled = true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check which permissions are granted. Set the SOS button accordingly
        if (requestCode == 1) {
            List<String> unGrantedPermissions = new ArrayList<>();

            // Make the permissions map
            for (String permission : permissions) {
                this.permissions.put(permission, false);
            }

            // Check if all permissions are granted
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                    this.permissions.put(permissions[i], true);
                } else {
                    // Permission is not granted
                    this.permissions.put(permissions[i], false);
                    unGrantedPermissions.add(permissions[i]);
                }
            }

            if (this.permissions.get(Manifest.permission.ACCESS_FINE_LOCATION)) {
                enableLocationFeatures();
            } else if (
                    this.permissions.get(Manifest.permission.ACCESS_COARSE_LOCATION)
                            && !this.permissions.get(Manifest.permission.ACCESS_FINE_LOCATION)
            ) {
                // Do not enable location features - Map cannot center to user's location
                Toast.makeText(
                        this,
                        "Fine Location permission denied. App cannot function. App will exit",
                        Toast.LENGTH_SHORT
                ).show();
                // Exit the app
                finish();
            }

            if (this.permissions.get(Manifest.permission.SEND_SMS) &&
                    this.permissions.get(Manifest.permission.CALL_PHONE)) {
                enableCommunicationFeatures();
            } else {
                // Launch the basic permission missing activity
                Intent intent = new Intent(this, BasicPermissionMissingActivity.class);
                startActivityForResult(intent, RequestCode.BASIC_PERMISSION_MISSING_ACTIVITY.getValue());
            }

            if (unGrantedPermissions.size() > 0) {
                // Launch the basic permission missing activity
                Intent intent = new Intent(this, BasicPermissionMissingActivity.class);
                startActivityForResult(intent, RequestCode.BASIC_PERMISSION_MISSING_ACTIVITY.getValue());
            }
        } else {
            System.out.println("Request code unknown.");
            System.out.println("Request code: " + requestCode);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        // SOS Button listener
        sosButton = findViewById(R.id.sos_button);

        // Add default listener to the SOS button - BasicPermissionMissingActivity
        sosButton.setOnClickListener(view -> {
            // Launch the basic permission missing activity
            Intent intent = new Intent(this, BasicPermissionMissingActivity.class);
            startActivityForResult(intent, RequestCode.BASIC_PERMISSION_MISSING_ACTIVITY.getValue());
        });

        // This must be prepared in advance for the enableLocationFeatures() method to function
        // as expected on first launch of the app when permissions are being granted only then

        // Background thread to get all the reports, get the locations of the reports, and the types of the reports
        // From firebase rtdb
        tileMakerThread = new Thread(() -> {
            MakeTiles();
        });

        // Check for text message permission
        // Check for emergency call permission
        // If permission is not granted, request it
        if (
                (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        ) {
            // Permission is not granted
            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.SEND_SMS,
                            Manifest.permission.CALL_PHONE,
                    },
                    1);
        }

        // Wait on location activity permission
        while (
                ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Search bar listener
        searchLocation = findViewById(R.id.searchview_bar);
        searchLocation.setOnClickListener(v -> searchLocation.setIconified(false));
        searchLocation.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchLocation.getQuery().toString();
                List<Address> addressList = null;
                if(location != null){
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try{
                        addressList = geocoder.getFromLocationName(location,1);

                    }
                    catch (Exception e){
                        System.out.println("GEOCODER FAILURE :  Exception: " + e.getMessage());
                    }

                    if (addressList!=null && addressList.size()>0){
                        Address address = addressList.get(0);
                        LatLng latlng = new LatLng(address.getLatitude(), address.getLongitude());

                        // Check if the address has addressLines, if yes, extract first line
                        String addressLine = "";
                        if (address.getFeatureName() != null) {
                            addressLine = address.getFeatureName();

                            // regex to check if the addressLine is only a number
                            if (addressLine.matches("\\d+")) {
                                addressLine = address.getAddressLine(0);
                            }

                        } else if (address.getAddressLine(0) != null) {
                            addressLine = address.getAddressLine(0);
                        } else {
                            addressLine = address.toString();
                        }

                        ((MainScreenMapFragment) mapFragment).updateMapLocation(latlng, addressLine);
                    } else {
                        Toast.makeText(MainActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Do nothing. We are not doing autocomplete as of now.
                return false;
            }
        });

        // Add listener to the Message Boards button
        Button messageBoardsButton = findViewById(R.id.message_boards_button);
        messageBoardsButton.setOnClickListener(view -> {
            System.out.println("Message Boards Button clicked");

            SharedPreferences sharedPreferences = getSharedPreferences("userdata", Context.MODE_PRIVATE);
            String currentUserName = sharedPreferences.getString("currentUserName", null);

            if (currentUserName == null) {
                Toast.makeText(this, "Please login to view message boards", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, MessageBoardsActivity.class);
            intent.putExtra("location", mapLocation);
            startActivity(intent);
        });

        // Set Why Login button listener to a dialog
        Button why_login_button = findViewById(R.id.why_login);
        why_login_button.setOnClickListener(view -> {
            // Create a dialog to explain why login is needed
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Why Login?");
            builder.setMessage("Login is needed to use all the features of the app. \n" +
                    "Message boards, Profile Section, and the ability to report incidents are disabled. \n" +
                    "Login is also needed to use the SMS to emergency contacts feature of the SOS feature in the app.");
            builder.setPositiveButton("OK", (dialog, which) -> {
                // Do nothing
            });
            builder.show();
        });

        if(!isLocationFullyEnabled) {
            enableLocationFeatures();
        }
        if(!isCommunicationFullyEnabled) {
            enableCommunicationFeatures();
        }
    }

    private void MakeTiles() {
        // Get all the reports
        List<Report> reports = new ArrayList<Report>();

        // Get reportsref from firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reportsRef = firebaseDatabase.getReferenceFromUrl(getString(R.string.firebase_database_url) + "/report");

        System.out.println("Reports ret THREAD: " + reportsRef.toString());

        reportsRef.get().addOnSuccessListener(task -> {
            System.out.println("Reports ret THREAD : Reports: " + task.getValue());

            if (task.getValue() == null) {
                System.out.println("Reports ret THREAD : No reports");
                return;
            }

            // Get the reports hashmap
            Map<String, Object> allReportsMap = (Map<String, Object>) task.getValue();

            List<Report> allReports = HeatmapHelper.transformReports(allReportsMap);

            // Currently always recompute the weighted lat longs
            HeatmapTileProvider heatmapTileProvider = transformReportsToHeatmapTileProvider(allReports, null);

            System.out.println("HMT Data" + heatmapTileProvider.getTile(0, 0, 0).data.length);

            System.out.println("Reports ret THREAD : Ready to Update map fragment");

            ((MainScreenMapFragment) mapFragment).setHeatmap(heatmapTileProvider);

        }).addOnFailureListener(task -> {
            System.out.println("Reports ret BUILDER THREAD : Failed to get reports");
        });
        // Get the types of the reports
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    // Will never need to be called if permission is unavailable
    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Request code: " + requestCode);
        System.out.println("Result code: " + resultCode);
        if (requestCode == RequestCode.BASIC_PERMISSION_MISSING_ACTIVITY.getValue()) {
            // Basic permission missing activity result
            if (resultCode == RESULT_OK) {
                System.out.println("Result OK");
                if (data.getBooleanExtra("all_permissions_granted", false)) {
                    System.out.println("All permissions granted");
                }
                System.out.println("Data received: " + data.getBooleanExtra("all_permissions_granted", false));
                // Update the SOS button and it's listener
                FloatingActionButton sosButton = findViewById(R.id.sos_button);
                sosButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.sos_red));
                Toast.makeText(this, "SOS available", Toast.LENGTH_SHORT).show();
                sosButton.setOnClickListener(view -> {
                    System.out.println("SOS Button now clicked");
                });

            } else if (resultCode == BasicPermissionMissingActivity.PermissionStatus.LOCATION_DENIED.ordinal()){
                // Location permission denied - App cannot function
                System.out.println("Location permission denied");
                Toast.makeText(this, "Location permission denied. App cannot function", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            System.out.println("Request code not OK. Proceeding without SOS support");
        }
    }

    public void report(View view) {
        startActivity(new Intent(MainActivity.this, ReportMenuActivity.class));
    }

    public void profileClick(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check username in shared preferences
        SharedPreferences sharedPref = getSharedPreferences("userdata", Context.MODE_PRIVATE);
        String currentUserName = sharedPref.getString("currentUserName", null);
        Boolean firstLoginAttempted = sharedPref.getBoolean("firstLoginAttempted", false);

        if (currentUserName == null) {
            System.out.println("Current user name is null");
            if (!firstLoginAttempted) {
                System.out.println("First login attempted");
                // First time login
                // Launch the login activity
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("firstLogin", true);
                startActivity(intent);
            } else {
                // Make a toast to tell the user that many functions are disabled
                // because they are not logged in
                Toast.makeText(this, "Please login to use all the features as intended", Toast.LENGTH_SHORT).show();
            }
            Button why_login_button = findViewById(R.id.why_login);
            why_login_button.setVisibility(View.VISIBLE);
        } else {
            Button why_login_button = findViewById(R.id.why_login);
            why_login_button.setVisibility(View.GONE);
            System.out.println("Current user name is not null");
            // User is logged in
            // Make a toast to tell the user that they are logged in
            Toast.makeText(this, "Logged in as " + currentUserName, Toast.LENGTH_SHORT).show();
        }
    }
}