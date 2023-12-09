package edu.northeastern.g15finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    String currentUserName = null;
    User currentUser = null;
    TextView username_tv;
    TextView full_name_tv;
    TextView dob_tv;
    boolean firstLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = getSharedPreferences("userdata", Context.MODE_PRIVATE);
        currentUserName = sharedPref.getString("currentUserName", null);
        firstLogin = getIntent().getBooleanExtra("firstLogin", false);

        if(currentUserName == null){
            setContentView(R.layout.login_activity);
            username_tv = findViewById(R.id.username_tv);
        }
        else{
            setContentView(R.layout.activity_profile);
            getLoggedInUser();
        }

        if(firstLogin){
            Toast.makeText(this, "Registration enables all features",
                    Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("firstLoginAttempted", true);
        }
    }

    public void onLoginClick(View view) {
        String username = username_tv.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(username);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    User user = document.toObject(User.class);
                    currentUser = user;
                    loadProfile();

                    SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username", user.getUserName());
                    editor.apply();

                } else {
                    Toast.makeText(this, "This User does not exist",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Issues getting this user, please try again later",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getLoggedInUser(){
        if(currentUserName!=null){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(currentUserName);

            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User user = document.toObject(User.class);
                        currentUser = user;
                        loadProfile();
                    } else {
                        Toast.makeText(this, "This User does not exist",
                                Toast.LENGTH_SHORT).show();
                        currentUserName = null;
                    }
                } else {
                    Toast.makeText(this, "Issues getting this user, please try again later",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void onRegisterClick(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void loadProfile(){
        if(currentUser != null){
            currentUserName = currentUser.getUserName();
            SharedPreferences sharedPref = getSharedPreferences("userdata", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("currentUserName", currentUserName);
            editor.commit();

            setContentView(R.layout.activity_profile);

            Toolbar toolbar = findViewById(R.id.pro_toolbar);
            toolbar.setTitle(currentUser.getUserName()+ " Profile");

            full_name_tv = findViewById(R.id.full_name_tv);
            dob_tv = findViewById(R.id.dob_tv);
            full_name_tv.setText(currentUser.getFullName());
            dob_tv.setText(currentUser.getDateOfBirth());

            List<String> emergencyContacts = currentUser.getEmergencyContacts();
            if(emergencyContacts != null){
                for(String contact : emergencyContacts){
                    System.out.println("Emergency contact: " + contact);
                }
            }

            // Get the phone numbers of the emergency contacts from the DB
            Map<String, String> emergencyContactsPhoneNumbers = new HashMap<String, String>();
            for (String emergencyContactName:emergencyContacts){
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("users").document(emergencyContactName);

                System.out.println("Emergency contact name: " + emergencyContactName);

                Task<DocumentSnapshot> task = docRef.get();
                while (!task.isComplete()) {
                    System.out.println("Waiting for task to complete");
                }
                DocumentSnapshot document = task.getResult();
                System.out.println("Document: " + document.toString());
                if (document.exists()) {
                    User user = document.toObject(User.class);
                    System.out.println("User: " + user.toString());
                    emergencyContactsPhoneNumbers.put(emergencyContactName, user.getPhoneNumber());
                    System.out.println("Emergency contact name: " + emergencyContactName);
                    System.out.println("Emergency contact phone number: " + user.getPhoneNumber());
                    System.out.println("Emergency contacts size: " + emergencyContactsPhoneNumbers.size());
                } else {
                    Toast.makeText(this, "The emergency contact does not exist in records",
                            Toast.LENGTH_SHORT).show();
                }
            }

            System.out.println("Emergency contacts Final size: " + emergencyContactsPhoneNumbers.size());

            if ((emergencyContacts != null) && (emergencyContacts.size() > 0) && (emergencyContactsPhoneNumbers.size() > 0)) {

                // Insert the emergency contacts into the shared preferences
                SharedPreferences.Editor editor2 = sharedPref.edit();
                Gson gson = new Gson();
                editor2.putString("emergencyContacts", gson.toJson(emergencyContactsPhoneNumbers));
                editor2.commit();

                System.out.println("Emergency contacts size: " + emergencyContactsPhoneNumbers.size());
            }
        }
    }

    public void editProfileClick(View view){
        startActivity(new Intent(this, EditProfileActivity.class));
    }

    public void goToFriends(View view){
        startActivity(new Intent(this, FriendsActivity.class));
    }

    public void logoutClick(View view){
        currentUserName = null;
        currentUser = null;
        SharedPreferences sharedPref = getSharedPreferences("userdata", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("currentUserName", currentUserName);
        editor.remove("emergencyContacts");
        editor.commit();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if(currentUserName == null){
            System.out.println("Back pressed in login");

            // Insert firstLoginAttempted into shared preferences
            SharedPreferences sharedPref = getSharedPreferences("userdata", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("firstLoginAttempted", true);
            editor.commit();

            // Go to main activity after clearing the stack
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            super.onBackPressed();
        } else {
            System.out.println("Back pressed in profile");
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            super.onBackPressed();
        }
    }
}