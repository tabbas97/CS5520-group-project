package edu.northeastern.g15finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FriendViewActivity extends AppCompatActivity {

    String friendUserName;

    User friend;

    User currentUser;

    TextView fv_full_name_tv;

    TextView fv_dob_tv;

    Button fv_add_friend_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_view);
        fv_full_name_tv = findViewById(R.id.fv_full_name_tv);
        fv_dob_tv = findViewById(R.id.fv_dob_tv);
        fv_add_friend_button = findViewById(R.id.fv_add_friend_button);
        this.friendUserName = getIntent().getStringExtra("friendUserName");
        this.currentUser = getIntent().getParcelableExtra("currUser");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(friendUserName);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    User user = document.toObject(User.class);
                    friend = user;
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
        fv_full_name_tv.setText(friend.getFullName());
        fv_dob_tv.setText(friend.getDateOfBirth());
        if(currentUser.getFriendsIds().contains(friendUserName)){
            fv_add_friend_button.setVisibility(View.INVISIBLE);
        }
    }

    public void fvAddFriendClick(View view){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users")
                .document(currentUser.getUserName());

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    currentUser.getFriendsIds().add(friendUserName);
                    FirebaseFirestore.getInstance().collection("users")
                            .document(currentUser.getUserName())
                            .set(currentUser);
                    startActivity(new Intent(this, FriendsActivity.class));
                } else {
                    Log.i("FUUUCK", "USER DOESNT EXIST");
                }
            } else {
                Log.i("FUUUCK", "get failed with ", task.getException());
            }
        });
    }
}