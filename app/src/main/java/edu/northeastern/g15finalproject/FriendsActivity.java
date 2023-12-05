package edu.northeastern.g15finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {

    RecyclerView allFriendsRecyclerView;

    EditText friend_un;

    User currentUser;

    String currentUserName;

    List<User> friends;

    FriendsRVAdapter friendsRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        allFriendsRecyclerView = findViewById(R.id.friends_recycler_view);
        allFriendsRecyclerView.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));
        friend_un = findViewById(R.id.friend_un);

        SharedPreferences sharedPref = getSharedPreferences("userdata", Context.MODE_PRIVATE);
        currentUserName = sharedPref.getString("currentUserName", null);

        friends = new ArrayList<>();
        getUser();

        friendsRVAdapter = new FriendsRVAdapter(friends, this);

        FriendsRVAdapter.OnAdapterClickListener adapterClickListener = new FriendsRVAdapter.OnAdapterClickListener() {
            @Override
            public void onItemClick(View linkView, int position) {
                goToFriend(position);
            }

            @Override
            public void onAddSosClick(View linkView, int position) {
                addSosFriend(position);
            }

            @Override
            public void onDeleteClick(View linkView, int position) {
                deleteFriend(position);
            }
        };

        friendsRVAdapter.setOnItemClickListener(adapterClickListener);

        allFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        allFriendsRecyclerView.setAdapter(friendsRVAdapter);
    }


    private void getUser(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(currentUserName);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    User user = document.toObject(User.class);
                    currentUser = user;
                    getFriends();
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

    private void getFriends() {
        if (currentUser.getFriendsIds().size() == 0){
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference friendsRef = db.collection("users");
        Query query = friendsRef.whereIn("userName", currentUser.getFriendsIds());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot document = task.getResult();
                friends.clear();
                for (QueryDocumentSnapshot snap : document) {
                    User userTemp = snap.toObject(User.class);
                    friends.add(userTemp);
                }
                allFriendsRecyclerView.getAdapter().notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Issues getting your friends, please try again later",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addFriendClick(View view) {
        String friend_username = friend_un.getText().toString().trim();
        if(friend_username == ""){
            return;
        }
        for(User f:friends){
            if(f.getUserName().equals(friend_username)){
                Toast.makeText(this, "This User is already a friend",
                        Toast.LENGTH_SHORT).show();
            }
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(currentUserName);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    currentUser.getFriendsIds().add(friend_username);
                    FirebaseFirestore.getInstance().collection("users")
                            .document(currentUser.getUserName())
                            .set(currentUser);
                    getFriends();
                } else {
                    Toast.makeText(this, "This User does not exist",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Issues adding this friend, please try again later",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goToFriend(int position) {
        Intent intent = new Intent(this, FriendViewActivity.class);
        intent.putExtra("friendUserName", friends.get(position).getUserName());
        intent.putExtra("currUser", currentUser);
        startActivity(intent);
    }

    public void addSosFriend(int position) {
        String username = friends.get(position).getUserName();
        if(!currentUser.getEmergencyContacts().contains(username)){
            currentUser.getEmergencyContacts().add(username);
            FirebaseFirestore.getInstance().collection("users")
                    .document(currentUser.getUserName())
                    .set(currentUser);
        }
    }

    public void deleteFriend(int position) {
        String username = friends.get(position).getUserName();
        currentUser.getFriendsIds().remove(username);
        friends.remove(position);
        if(currentUser.getEmergencyContacts().contains(username)){
            currentUser.getEmergencyContacts().remove(username);
        }

        FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.getUserName())
                .set(currentUser);
        allFriendsRecyclerView.getAdapter().notifyDataSetChanged();
    }
}