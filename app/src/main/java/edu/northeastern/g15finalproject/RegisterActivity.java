package edu.northeastern.g15finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    EditText reg_un;
    EditText reg_pass;
    EditText reg_conf_pass;
    EditText reg_full_name;
    EditText reg_dob;

    EditText reg_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reg_un = findViewById(R.id.reg_username_tv);
        reg_pass = findViewById(R.id.reg_password_tv);
        reg_conf_pass = findViewById(R.id.reg_confpassword_tv);
        reg_full_name = findViewById(R.id.reg_name_tv);
        reg_dob = findViewById(R.id.reg_dob_tv);
        reg_phone = findViewById(R.id.phone_number_ET);
    }

    public void onFullRegisterClick(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String username = reg_un.getText().toString();
        DocumentReference docRef = db.collection("users").document(username);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.i("BAD", "USER ALREADY EXISTS");
                } else {
                    User user_reg = new User();
                    user_reg.setUserName(username);
                    user_reg.setFullName(reg_full_name.getText().toString());
                    user_reg.setDateOfBirth(reg_dob.getText().toString());
                    user_reg.setPassword(reg_pass.getText().toString());
                    user_reg.setFriendsIds(new ArrayList<>());
                    user_reg.setPhoneNumber(reg_phone.getText().toString());

                    db.collection("users").document(username)
                            .set(user_reg);
                    startActivity(new Intent(this, ProfileActivity.class));
                }
            } else {
                Log.i("BAD", "get failed with ", task.getException());
            }
        });
    }
}