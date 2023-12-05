package edu.northeastern.g15finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

        if(currentUserName == null){
            Toast.makeText(this, "This User does not exist",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ProfileActivity.class));
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
                    Toast.makeText(this, "This User does not exist",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Issues getting user, please try again later",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProfile(){
        if(currentUser != null){
            edit_fullname.setHint(currentUser.getFullName());
            edit_dob.setHint(currentUser.getDateOfBirth());
        }
    }

    public void saveProfile(View view) {
        String fullN = edit_fullname.getText().toString();
        String dob = edit_dob.getText().toString();
        boolean toChange = false;
        if(!fullN.equals("") && fullN!=currentUser.getFullName()){
            currentUser.setFullName(fullN);
            toChange = true;
        }

        if(!dob.equals("") && dob!=currentUser.getDateOfBirth()){
            currentUser.setDateOfBirth(dob);
            toChange = true;
        }



        if(toChange){
            FirebaseFirestore.getInstance().collection("users")
                    .document(currentUser.getUserName())
                    .set(currentUser);
        }
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}