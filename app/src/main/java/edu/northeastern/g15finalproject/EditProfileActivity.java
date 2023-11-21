package edu.northeastern.g15finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

    private void getUser(){
        Log.i("FUUUCK", "HERE1");
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .orderByChild("userName")
                .equalTo(currentUserName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.i("FUUUCK", "HERE1");
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Log.i("FUUUCK", "HERE2");
                        User user = snap.getValue(User.class);
                        currentUser = user;
                        loadProfile();
                    }
                }
                else{
                    Log.i("FUUUCK", "NO SUCH USER EXISTS");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
            FirebaseDatabase.getInstance().getReference()
                    .child("users").child(currentUser.getUserName()).setValue(currentUser);
        }
        startActivity(new Intent(this, ProfileActivity.class));
    }
}