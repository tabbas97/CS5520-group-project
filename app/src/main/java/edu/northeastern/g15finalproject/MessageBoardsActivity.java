package edu.northeastern.g15finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MessageBoardsActivity extends AppCompatActivity {

    static RecyclerView postRecyclerView;
    static ProgressBar progressBar;

    static FloatingActionButton add_post_fab;
    static List<MessageBoardItem> postsList;

    private FirebaseDatabase rootNode;
    private DatabaseReference postsRef;

    private Location location = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_boards);

        // get intent
        android.content.Intent intent = getIntent();
        // check if intent has location
        if (intent.hasExtra("location")) {
            // get location from intent
            location = intent.getParcelableExtra("location");
        } else {
            // set location to null
            location = null;
            System.out.println("Location is null. Intent does not have location.");
        }

        progressBar = findViewById(R.id.progressBarMessageBoards);

        // Loading Mechanism
        // 1. Set visibility of spinner to visible
        progressBar.setVisibility(android.view.View.VISIBLE);

        List<MessageBoardItem> posts = new ArrayList<>();
        postsList = new ArrayList<>();
        // 2. Load data in background thread
        rootNode = FirebaseDatabase.getInstance();
        postsRef = rootNode.getReferenceFromUrl(getString(R.string.RTDB_ROOT)).child("post");

        System.out.println("POSTS REF: " + postsRef.toString());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                System.out.println("RUNNING THREAD");

                postsRef.get().addOnCompleteListener(task -> {
                    System.out.println("TASK: " + task.toString());
                    System.out.println("TASK RESULT: " + task.getResult().toString());
                    for (com.google.firebase.database.DataSnapshot post : task.getResult().getChildren()) {
                        System.out.println("POST: " + post.getValue().toString());
                        // Get the value of the post and convert it to a DataClasses.Post object
                        /* Post Format:
                            {
                              "attached_report": [
                                null,
                                2
                              ],
                              "body": "b1",
                              "comments": {
                                "u1": "c1",
                                "u2": "c2"
                              },
                              "plus_one": {
                                "u1": true,
                                "u2": true
                              },
                              "testing": false,
                              "time": "utc_timestamp",
                              "title": "t1",
                              "username": "u1"
                            }
                         */
                        String title = post.child("title").getValue().toString();
                        String body = post.child("body").getValue().toString();
                        System.out.println("Time : " + post.child("time").getValue().toString());
                        Long timeStamp = (Long)post.child("time").getValue();

                        if (timeStamp == null) {
                            System.out.println("Time stamp is null");
                            System.out.println("POST KEY : " + post.getKey());
                        }

                        String username = post.child("username").getValue().toString();

                        // Get number of comments from iterable
                        int numComments = 0;
                        for (com.google.firebase.database.DataSnapshot comment : post.child("comments").getChildren()) {
                            numComments++;
                        }

                        // Get number of plus ones from iterable
                        int numPlusOnes = 0;
                        for (com.google.firebase.database.DataSnapshot plusOne : post.child("plus_one").getChildren()) {
                            numPlusOnes++;
                        }

                        String postID = post.getKey();

                        boolean skipInsert = false;

                        // If postID matches, skip the post
                        for (int i = 0; i < postsList.size(); i++) {
                            if (postsList.get(i).getPostID().equals(postID)) {
                                skipInsert = true;
                            }
                        }

                        if (!skipInsert) {
                            postsList.add(new MessageBoardItem(title, body, timeStamp.toString(), Integer.toString(numComments), Integer.toString(numPlusOnes), username, postID));
                        }
                    }

                    // 3. Set visibility of spinner to invisible when data is loaded with runonuithread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(android.view.View.INVISIBLE);
                            postRecyclerView = findViewById(R.id.message_boards_recycler_view);
                            postRecyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(MessageBoardsActivity.this));
                            postRecyclerView.setAdapter(new MessageBoardItemAdapter(postsList, MessageBoardsActivity.this));
                        }
                    });
                });
            }
        });

        thread.start();

        // Set of sample posts
        postRecyclerView = findViewById(R.id.message_boards_recycler_view);
        postRecyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        postRecyclerView.setAdapter(new MessageBoardItemAdapter(posts, this));
        postRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );

        // Add an onChildAddedListener to the postsRef
        postsRef.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot snapshot, String previousChildName) {
                System.out.println("CHILD ADDED");
                System.out.println("PREVIOUS CHILD NAME: " + previousChildName);
                System.out.println("SNAPSHOT: " + snapshot.toString());

                // Get the value of the post and convert it to a DataClasses.Post object
                /* Post Format:
                    {
                      "attached_report": [
                        null,
                        2
                      ],
                      "body": "b1",
                      "comments": {
                        "u1": "c1",
                        "u2": "c2"
                      },
                      "plus_one": {
                        "u1": true,
                        "u2": true
                      },
                      "testing": false,
                      "time": "utc_timestamp",
                      "title": "t1",
                      "username": "u1"
                    }
                 */
                System.out.println("SNAPSHOT VAL: " + snapshot.getValue().toString());
                String title = snapshot.child("title").getValue().toString();
                String body = snapshot.child("body").getValue().toString();
                String timeStamp = snapshot.child("time").getValue().toString();
                String username = snapshot.child("username").getValue().toString();

                // Get number of comments from iterable
                int numComments = 0;
                for (com.google.firebase.database.DataSnapshot comment : snapshot.child("comments").getChildren()) {
                    numComments++;
                }

                // Get number of plus ones from iterable
                int numPlusOnes = 0;
                for (com.google.firebase.database.DataSnapshot plusOne : snapshot.child("plus_one").getChildren()) {
                    numPlusOnes++;
                }

                String postID = snapshot.getKey();

                postsList.add(new MessageBoardItem(title, body, timeStamp, Integer.toString(numComments), Integer.toString(numPlusOnes), username, postID));

                // Notify the adapter that the data has changed
                postRecyclerView.getAdapter().notifyDataSetChanged();

                // 3. Set visibility of spinner to invisible when data is loaded with runonuithread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(android.view.View.INVISIBLE);
                    }
                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Identify the post that has changed
                System.out.println("CHILD CHANGED");
                System.out.println("PREVIOUS CHILD NAME: " + previousChildName);
                System.out.println("SNAPSHOT: " + snapshot.toString());
                System.out.println("SNAPSHOT VALUE: " + snapshot.getValue().toString());

                // Get the value of the post and convert it to a DataClasses.Post object
                /* Post Format:
                    {
                      "attached_report": [
                        null,
                        2
                      ],
                      "body": "b1",
                      "comments": {
                        "u1": "c1",
                        "u2": "c2"
                      },
                      "plus_one": {
                        "u1": true,
                        "u2": true
                      },
                      "testing": false,
                      "time": "utc_timestamp",
                      "title": "t1",
                      "username": "u1"
                    }
                 */
                String title = snapshot.child("title").getValue().toString();
                String body = snapshot.child("body").getValue().toString();
                String timeStamp = snapshot.child("time").getValue().toString();
                String username = snapshot.child("username").getValue().toString();

                // Get number of comments from iterable
                int numComments = 0;
                for (com.google.firebase.database.DataSnapshot comment : snapshot.child("comments").getChildren()) {
                    numComments++;
                }

                // Get number of plus ones from iterable
                int numPlusOnes = 0;
                for (com.google.firebase.database.DataSnapshot plusOne : snapshot.child("plus_one").getChildren()) {
                    numPlusOnes++;
                }

                // Replace the post in the list with the new post
                for (int i = 0; i < postsList.size(); i++) {
                    // If postID matches, replace the post
                    if (postsList.get(i).getPostID().equals(snapshot.getKey())) {
                        postsList.set(i, new MessageBoardItem(title, body, timeStamp, Integer.toString(numComments), Integer.toString(numPlusOnes), username, snapshot.getKey()));
                    }
                }

                // Notify the adapter that the data has changed
                postRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Remove the post with the matching postID from the list
                System.out.println("CHILD REMOVED");
                System.out.println("SNAPSHOT: " + snapshot.toString());
                System.out.println("SNAPSHOT VALUE: " + snapshot.getValue().toString());

                // Remove the post with the matching postID from the list
                for (int i = 0; i < postsList.size(); i++) {
                    // If postID matches, remove the post
                    if (postsList.get(i).getPostID().equals(snapshot.getKey())) {
                        postsList.remove(i);
                    }
                }

                // Notify the adapter that the data has changed
                postRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Do nothing
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Do nothing
            }
        });

        // Add Post Button
        add_post_fab = findViewById(R.id.add_post_fab);
        add_post_fab.setOnClickListener(v -> {
            // Open the AddPostActivity
            Intent intent1 = new Intent(MessageBoardsActivity.this, AddPostActivity.class);
            if (location != null) {
                intent1.putExtra("location", location);
            }
            startActivity(intent1);
        });
    }

}