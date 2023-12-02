package edu.northeastern.g15finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.northeastern.g15finalproject.DataClasses.Post;

public class MessageBoardOwnPostActivity extends AppCompatActivity {

    FirebaseDatabase rootRef = null;
    DatabaseReference currentPostRef = null;

    TextView postBody = null;
    TextView postTitle = null;
    TextView postTime = null;
    TextView postNumComments = null;
    TextView postNumPlusOne = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board_own_post);

        postBody = findViewById(R.id.message_board_own_post_content);
        postTitle = findViewById(R.id.message_board_own_post_title);
        postTime = findViewById(R.id.message_board_own_post_date);
        postNumComments = findViewById(R.id.message_board_own_post_comments_count);
        postNumPlusOne = findViewById(R.id.message_board_own_post_plus_one_count);


        // Get the post id from the intent
        String postID = getIntent().getStringExtra("postID");

        System.out.println("MessageBoardOwnPostActivity");
        System.out.println("Post ID: " + postID);

        // Get the root reference
        rootRef = FirebaseDatabase.getInstance();

        // Check if the root reference is null
        if (rootRef == null) {
            System.out.println("Root reference is null");
            Toast.makeText(this, "Unable to initiate Backend Connection. \nPlease try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the reference to the current post
        currentPostRef = rootRef.getReference("post").child(postID);

        // Check if the current post reference is null
        if (currentPostRef == null) {
            Toast.makeText(this, "Post not found. It might have been deleted by the owner.", Toast.LENGTH_SHORT).show();

            // Set the result of activity to cancelled
            setResult(RESULT_CANCELED);
            finish();
        }

        // Print all keys in the current post reference
        System.out.println("Current Post Reference Keys");
        for (String key : currentPostRef.getKey().split("/")) {
            System.out.println(key);
        }

        // Retrieve the post from the database
        currentPostRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("TASK RES" + task.getResult());
                // Print the post snapshot including all the children
                System.out.println("Post Snapshot");
                for (com.google.firebase.database.DataSnapshot postSnapshot : task.getResult().getChildren()) {
                    System.out.println(postSnapshot.getKey() + " : " + postSnapshot.getValue());
                }

                DataSnapshot postSnapshot = task.getResult();

                // Check if the post exists
                if (postSnapshot.exists()) {
                    // Get the post elements
                    String username = postSnapshot.child("username").getValue(String.class);
                    String title = postSnapshot.child("title").getValue(String.class);
                    String body = postSnapshot.child("body").getValue(String.class).substring(0, 400);
                    Long timestamp = postSnapshot.child("time").getValue(Long.class);
                    // Convert the timestamp to a date
                    java.util.Date date = new java.util.Date(timestamp);
                    // Get number of comments as count of children of comments
                    String numComments = String.valueOf(postSnapshot.child("comments").getChildrenCount());
                    String numPlusOne = String.valueOf(postSnapshot.child("plus_one").getChildrenCount());

                    // Print the post elements
                    System.out.println("Post Elements");
                    System.out.println("Username: " + username);
                    System.out.println("Title: " + title);
                    System.out.println("Body: " + body);
                    System.out.println("Timestamp: " + timestamp);
                    System.out.println("Date: " + date);
                    System.out.println("Number of Comments: " + numComments);
                    System.out.println("Number of Plus Ones: " + numPlusOne);


                    // Set the post body
                    postBody.setText(body);
                    // Set the post title
                    postTitle.setText(title);
                    // Set the post time
                    postTime.setText(date.toString());
                    // Set the post number of comments
                    postNumComments.setText(numComments);
                    // Set the post number of plus ones
                    postNumPlusOne.setText(numPlusOne);


                } else {
                    Toast.makeText(this, "Snapshot does not exist. Post not found. It might have been deleted by the owner.", Toast.LENGTH_SHORT).show();

                    // Set the result of activity to cancelled
                    setResult(RESULT_CANCELED);
                    finish();
                }
            } else {
                Toast.makeText(this, "Post not found. It might have been deleted by the owner.", Toast.LENGTH_SHORT).show();

                // Set the result of activity to cancelled
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}