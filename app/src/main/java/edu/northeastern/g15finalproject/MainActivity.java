package edu.northeastern.g15finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.room.RoomDatabase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

// Import location services
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationCallback;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    Location deviceLocation;
    Location mapLocation;
    String locationLabel = "You are here";
    Boolean userLocationMapViewSync = true;

    EmergencyContactDatabase db = null;
    EmergencyContactDao emergencyContactDao = null;

    List<EmergencyContact> emergencyContacts = null;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check for location permission
        // If permission is not granted, request it
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

        // TODO: Add code to check for location permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            // Get the approximate location of the device
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

        }

        // Check for text message permission
        // If permission is not granted, request it
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.SEND_SMS},
                    1);
        }

        // Check for emergency call permission
        // If permission is not granted, request it
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CALL_PHONE},
                    1);
        }

        Fragment mapFragment = new MainScreenMapFragment();

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

                    ((MainScreenMapFragment) mapFragment).updateMapLocation(mapLocation);
                }
            };
        }

        // Set the map location to the mapLocation
        Bundle bundle = new Bundle();
        bundle.putParcelable("mapLocation", mapLocation);
        bundle.putString("locationLabel", locationLabel);
        mapFragment.setArguments(bundle);

        // How to get the fragment manager from an activity:
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_fragment, mapFragment).commit();

        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
        );

        // Add listener to the toggle button
        Button toggleButton = findViewById(R.id.toggle_button);
        toggleButton.setOnClickListener((buttonView) -> {
            ((MainScreenMapFragment) mapFragment).startLocationTracking();
            ((MainScreenMapFragment) mapFragment).syncUserViewLastLocation();
            buttonView.setVisibility(View.INVISIBLE);
        });

        FloatingActionButton fab = findViewById(R.id.floating_search_button);
        EditText searchEditText = findViewById(R.id.search_bar);
        fab.setOnClickListener(view -> {
            System.out.println("Floating action button clicked");
            System.out.println(searchEditText.getVisibility());
            if (searchEditText.getVisibility() == EditText.INVISIBLE || searchEditText.getVisibility() == EditText.GONE) {
                System.out.println("Setting visibility to visible");
                searchEditText.setVisibility(View.VISIBLE);
            } else {
                System.out.println("Setting visibility to invisible");
                searchEditText.setVisibility(View.INVISIBLE);
            }
        });

        try {
            // Initialize the database
            db = Room.databaseBuilder(getApplicationContext(),
                    EmergencyContactDatabase.class, "emergency-contacts").build();

            // Initialize the emergency contact DAO
            emergencyContactDao = db.EmergencyContactDao();

            if (emergencyContactDao != null) {
                // Get all the emergency contacts on a background thread
                Thread thread = new Thread(() -> {
                    emergencyContacts = emergencyContactDao.getAll();
                    if (emergencyContacts != null) {
                        for (EmergencyContact emergencyContact : emergencyContacts) {
                            System.out.println("Emergency contact: " + emergencyContact.name + ", " + emergencyContact.phoneNumber);
                        }
                    } else {
                        System.out.println("Emergency contacts is null");
                    }
                });
            } else {
                System.out.println("Emergency contact DAO is null");
            }

//            emergencyContacts = emergencyContactDao.getAll();
            if (emergencyContacts != null) {
                for (EmergencyContact emergencyContact : emergencyContacts) {
                    System.out.println("Emergency contact: " + emergencyContact.name + ", " + emergencyContact.phoneNumber);
                }
            } else {
                System.out.println("Emergency contacts is null");

                // Adding test contacts to the database on a background thread
                Thread thread = new Thread(() -> {
                    // Delet all the emergency contacts
                    List <EmergencyContact> emergencyContactOld = emergencyContactDao.getAll();
                    for (EmergencyContact emergencyContact : emergencyContactOld) {
                        emergencyContactDao.delete(emergencyContact);
                    }

                    emergencyContactDao.insertAll(
                            new EmergencyContact(1, "t1", "18573130768"),
                            new EmergencyContact(2, "t2", "16463394762"),
                            new EmergencyContact(3, "t3", "16073792745")
                    );
                });
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // SOS Button listener
        FloatingActionButton sosButton = findViewById(R.id.sos_button);
        // If either call or SMS permission is not granted, display a toast and,
        // change SOS button background to grey, and set a listener
        // to the SOS button to grant permissions activity
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.SEND_SMS, android.Manifest.permission.CALL_PHONE},
                    1);
            sosButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.grey));
            sosButton.setOnClickListener(view -> {
                // Launch the basic permission missing activity
                Intent intent = new Intent(this, BasicPermissionMissingActivity.class);
                startActivityForResult(intent, RequestCode.BASIC_PERMISSION_MISSING_ACTIVITY.getValue());
            });
        } else {
            sosButton.setOnClickListener(view -> {
                System.out.println("SOS Button clicked");
                BasicPermissionMissingActivity.getMissingPermissions(this).forEach(permission -> {
                    System.out.println("Permission Missing: " + permission);
                });

                // Start a background thread to send the SMSes
                Thread smsThread = new Thread(() -> {
                    // Send SMSes to the emergency contacts

                    List<EmergencyContact> emergencyContacts = emergencyContactDao.getAll();
                    System.out.println("Emergency contacts: " + emergencyContacts);
                    for (EmergencyContact emergencyContact : emergencyContacts) {
                        System.out.println("Sending SMS to: " + emergencyContact.phoneNumber);
                        // Send SMS to the emergency contact
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(emergencyContact.phoneNumber, null, "TEST: I need help", null, null);
                    }

                    runOnUiThread(() -> {
                        Toast.makeText(this, "SMSes sent", Toast.LENGTH_SHORT).show();
                    });
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
        }
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

            } else {
                System.out.println("Result not OK");
                System.out.println("Data received: " + data.toString());
                System.out.println("Result Code : " + resultCode);
            }
        } else {
            System.out.println("Request code not OK. Proceeding without SOS support");
        }
    }
}