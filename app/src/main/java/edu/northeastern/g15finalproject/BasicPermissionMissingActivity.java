package edu.northeastern.g15finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class BasicPermissionMissingActivity extends AppCompatActivity {

    // Required Permissions - SMS, Location, Call
    static List<String> RequiredPermissions = new ArrayList(){{
        add("android.permission.ACCESS_FINE_LOCATION");
        add("android.permission.ACCESS_COARSE_LOCATION");
        add("android.permission.SEND_SMS");
        add("android.permission.CALL_PHONE");
    }};

    static Map<String, String> PermissonNormalNames = new HashMap<String, String>(){{
        put("android.permission.ACCESS_FINE_LOCATION", "Fine Location");
        put("android.permission.ACCESS_COARSE_LOCATION", "Coarse Location");
        put("android.permission.SEND_SMS", "SMS");
        put("android.permission.CALL_PHONE", "Call");
    }};

    public static List<String> getMissingPermissions(AppCompatActivity mainActivity) {
        List<String> missingPermissionsList = new ArrayList<>();

        // Check for all the required permissions. If any missing, request them. else finish the activity
        for (String permission : RequiredPermissions) {
            if (ContextCompat.checkSelfPermission(mainActivity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted. Add to missing permissions
                missingPermissionsList.add(permission);
            }
        }

        return missingPermissionsList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_permission_missing);

        AtomicReference<List<String>> missingPermissionsList = new AtomicReference<>(getMissingPermissions(this));

        // Check for all the required permissions. If any missing, request them. else finish the activity
        String missingPermissions = "";
        for (String permission : missingPermissionsList.get()) {
            missingPermissions += PermissonNormalNames.get(permission) + "\n";
        }

        // If there are no missing permissions, finish the activity
        if (missingPermissionsList.get().size() == 0) {
            finish();
        }

        // Set the missing permissions text view
        TextView missingPermissionsTextView = findViewById(R.id.missing_permissions);
        missingPermissionsTextView.setText(missingPermissions);

        // Set the request permissions button listener
        findViewById(R.id.grant_permissions).setOnClickListener(v -> {
            // Request the missing permissions
            ActivityCompat.requestPermissions(this,
                    missingPermissionsList.get().toArray(new String[missingPermissionsList.get().size()]),
                    1);
            missingPermissionsList.set(getMissingPermissions(this));
            if (missingPermissionsList.get().size() == 0) {

                Intent newIntent = new Intent(this, MainActivity.class);
                newIntent.putExtra("all_permissions_granted", true);
                finish();

            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                System.out.println("BasicPermissionsActivity : Back pressed");

                // Update the missing permissions list
                missingPermissionsList.set(getMissingPermissions(BasicPermissionMissingActivity.this));

                if (missingPermissionsList.get().size() == 0) {
                    Intent newIntent = new Intent(BasicPermissionMissingActivity.this, MainActivity.class);
                    newIntent.putExtra("all_permissions_granted", true);
                    setResult(RESULT_OK, newIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Proceeding without all permissions. SOS will not function", Toast.LENGTH_SHORT).show();
                    // Insert the status of all_permissions_granted
                    Intent newIntent = new Intent(BasicPermissionMissingActivity.this, MainActivity.class);
                    newIntent.putExtra("all_permissions_granted", false);
                    setResult(RESULT_CANCELED, newIntent);
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}