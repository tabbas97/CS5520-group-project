package edu.northeastern.g15finalproject;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .orderByChild("userName")
                .equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
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

    public void onRegisterClick(View view) {
        String username = username_tv.getText().toString();
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .orderByChild("userName")
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.i("FUUUCK", "USER ALREADY EXISTS");
                }
                else{
                    Log.i("FUUUCK", "HERE3");
                    User user_reg = new User();
                    user_reg.setUserName(username);
                    user_reg.setFullName("First User");
                    user_reg.setDateOfBirth("1st Jan 1998");
                    user_reg.setPassword("password");
                    user_reg.setFriendsIds(new ArrayList<>());
                    FirebaseDatabase.getInstance().getReference()
                            .child("users").child(user_reg.userName).setValue(user_reg);
                    currentUser = user_reg;
                    loadProfile();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        SharedPreferences sharedPref = getSharedPreferences("userdata", Context.MODE_PRIVATE);
        startActivity(new Intent(this, EditProfileActivity.class));
    }

    public void goToFriends(View view){

    }
}