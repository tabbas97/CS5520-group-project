package edu.northeastern.g15finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.atomic.AtomicReference;

public class EditPostActivity extends AppCompatActivity {

    FirebaseDatabase rootRef = null;
    DatabaseReference currentPostRef = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        // Get the post id from the intent
        if (!getIntent().hasExtra("postID")) {
            setResult(MessageBoardPostViewActivity.EDITPOST_RESULT.FAILURE.ordinal());
            // If the intent does not have the post id, then return to the previous activity
            finish();
        }

        String postID = getIntent().getStringExtra("postID");

        // Get the root reference
        rootRef = FirebaseDatabase.getInstance();
        currentPostRef = rootRef.getReferenceFromUrl(getString(R.string.firebase_database_url)).child("post").child(postID);

        AtomicReference<String> postTitle = new AtomicReference<>("");
        AtomicReference<String> postBody = new AtomicReference<>("");

        // Get the whole of the post
        currentPostRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get the post
                DataSnapshot post = task.getResult();

                System.out.println("EDIT POST : " + post.toString());

                // Get the title and body of the post
                postTitle.set(post.child("title").getValue().toString());
                postBody.set(post.child("body").getValue().toString());


                System.out.println("EDIT POST : Got post");
                System.out.println("EDIT POST : Post title: " + postTitle.get());
                System.out.println("EDIT POST : Post body: " + postBody.get());

                // Set the title and body of the post
                runOnUiThread(() -> {
                    ((android.widget.EditText) findViewById(R.id.edit_post_title_et)).setText(postTitle.get());
                    ((android.widget.EditText) findViewById(R.id.edit_post_body_et)).setText(postBody.get());
                });
            } else {
                System.out.println("EDIT POST : Failed to get post");
                setResult(MessageBoardPostViewActivity.EDITPOST_RESULT.FAILURE.ordinal());
                finish();
            }
        });

        currentPostRef.get().addOnFailureListener(e -> {
            System.out.println("EDIT POST : Failed to get post");
            setResult(MessageBoardPostViewActivity.EDITPOST_RESULT.FAILURE.ordinal());
            finish();
        });


        // Set listener for the edit submit button
        findViewById(R.id.edit_post_submit_button).setOnClickListener(view -> {
            // Get the title and body of the post
            String newPostTitle = ((android.widget.EditText) findViewById(R.id.edit_post_title_et)).getText().toString();
            String newPostBody = ((android.widget.EditText) findViewById(R.id.edit_post_body_et)).getText().toString();

            // Update the post
            currentPostRef.child("title").setValue(newPostTitle);
            currentPostRef.child("body").setValue(newPostBody);

            // Set the result of the activity to success and updated value to intent
            setResult(MessageBoardPostViewActivity.EDITPOST_RESULT.SUCCESS.ordinal());
            finish();
        });


    }
}