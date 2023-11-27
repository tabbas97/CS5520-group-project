package edu.northeastern.g15finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends AppCompatActivity {

    String currentUserName = null;

    User currentUser = null;

    EditText edit_fullname;

    EditText edit_dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        edit_fullname = findViewById(R.id.edit_fullname);
        edit_dob = findViewById(R.id.edit_dob);

        SharedPreferences sharedPref = getSharedPreferences("userdata", Context.MODE_PRIVATE);
        currentUserName = sharedPref.getString("currentUserName", null);

        Log.i("FUUUCK", "EP US:" + currentUserName);

        if(currentUserName == null){
            Log.i("FUUUCK", "USER NOT FOUND ON EDIT PAGE");
        }
        else{
            getUser();
        }

    }

    private void getUser() {
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
                    Log.i("FUUUCK", "USER DOESNT EXIST");
                }
            } else {
                Log.i("FUUUCK", "get failed with ", task.getException());
            }
        });
    }

    private void loadProfile(){
        if(currentUser != null){
            Log.i("FUUUCK", "GOT USER");
            edit_fullname.setHint(currentUser.getFullName());
            edit_dob.setHint(currentUser.getDateOfBirth());
        }
    }

    public void saveProfile(View view) {
        String fullN = edit_fullname.getText().toString();
        String dob = edit_dob.getText().toString();
        if(fullN!=currentUser.getFullName() || dob!=currentUser.getDateOfBirth()){
            currentUser.setFullName(fullN);
            currentUser.setDateOfBirth(dob);

            FirebaseFirestore.getInstance().collection("users")
                    .document(currentUser.getUserName())
                    .set(currentUser);
        }
        startActivity(new Intent(this, ProfileActivity.class));
    }
}