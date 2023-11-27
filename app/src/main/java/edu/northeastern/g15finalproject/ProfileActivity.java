package edu.northeastern.g15finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    String currentUserName = null;
    User currentUser = null;
    TextView username_tv;
    TextView full_name_tv;
    TextView dob_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = getSharedPreferences("userdata", Context.MODE_PRIVATE);
        currentUserName = sharedPref.getString("currentUserName", null);

        setContentView(R.layout.login_activity);
        username_tv = findViewById(R.id.username_tv);
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
                } else {
                    Log.i("FUUUCK", "USER DOESNT EXIST");
                }
            } else {
                Log.i("FUUUCK", "get failed with ", task.getException());
            }
        });
    }

    public void onRegisterClick(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String username = username_tv.getText().toString();
        DocumentReference docRef = db.collection("users").document(username);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.i("FUUUCK", "USER ALREADY EXISTS");
                } else {
                    User user_reg = new User();
                    user_reg.setUserName(username);
                    user_reg.setFullName("First User");
                    user_reg.setDateOfBirth("1st Jan 1998");
                    user_reg.setPassword("password");
                    user_reg.setFriendsIds(new ArrayList<>());

                    db.collection("users").document(username)
                            .set(user_reg);
                    currentUser = user_reg;
                    loadProfile();
                }
            } else {
                Log.i("FUUUCK", "get failed with ", task.getException());
            }
        });
    }

    public void loadProfile(){
        if(currentUser != null){
            currentUserName = currentUser.getUserName();
            SharedPreferences sharedPref = getSharedPreferences("userdata", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("currentUserName", currentUserName);
            editor.commit();

            setContentView(R.layout.activity_profile);
            full_name_tv = findViewById(R.id.full_name_tv);
            dob_tv = findViewById(R.id.dob_tv);
            full_name_tv.setText(currentUser.getFullName());
            dob_tv.setText(currentUser.getDateOfBirth());
        }
    }

    public void editProfileClick(View view){
        startActivity(new Intent(this, EditProfileActivity.class));
    }

    public void goToFriends(View view){
        startActivity(new Intent(this, FriendsActivity.class));
    }
}