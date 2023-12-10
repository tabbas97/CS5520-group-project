package edu.northeastern.g15finalproject;

import static com.google.common.primitives.Ints.min;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.northeastern.g15finalproject.DataClasses.Comment;

public class MessageBoardPostViewActivity extends AppCompatActivity {

    FirebaseDatabase rootRef = null;
    DatabaseReference currentPostRef = null;

    TextView postBody = null;
    TextView postTitle = null;
    TextView postTime = null;
    TextView postNumComments = null;
    TextView postNumPlusOne = null;
    TextView postUserTV = null;

    private String postUser = null;

    public enum REQUEST_CODE {
        EDIT_POST
    }

    public enum EDITPOST_RESULT {
        SUCCESS,
        FAILURE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board_post_view);

        postBody = findViewById(R.id.message_board_own_post_content);
        postTitle = findViewById(R.id.message_board_own_post_title);
        postTime = findViewById(R.id.message_board_own_post_date);
        postNumComments = findViewById(R.id.message_board_own_post_comments_count);
        postNumPlusOne = findViewById(R.id.message_board_own_post_plus_one_count);
        postUserTV = findViewById(R.id.message_board_own_post_user);


        // Get user from shared preferences
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("userdata", android.content.Context.MODE_PRIVATE);
        postUser = sharedPreferences.getString("currentUserName", null);

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
                    System.out.println("Username: " + username);
                    System.out.println("Post User: " + postUser);

                    postUserTV.setText(username);

                    // Disable the edit button if the post is not owned by the current user
                    if (!postUser.equals(username)) {
                        findViewById(R.id.message_board_own_post_edit_button).setVisibility(android.view.View.GONE);
                    }

                    String title = postSnapshot.child("title").getValue(String.class);
                    String body = postSnapshot.child("body").getValue(String.class).substring(0, min(
                            postSnapshot.child("body").getValue(String.class).length(), 400
                    ));
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

        // Set listener for the edit button
        findViewById(R.id.message_board_own_post_edit_button).setOnClickListener(view -> {
            // Launch the edit post activity
            android.content.Intent intent = new android.content.Intent(this, EditPostActivity.class);

            // Place the post id about the post in the intent
            intent.putExtra("postID", postID);

            startActivityForResult(intent, REQUEST_CODE.EDIT_POST.ordinal());
        });

        RecyclerView commentsRecyclerView = findViewById(R.id.message_board_own_post_comments_recycler_view);
        commentsRecyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        // Async retrieve the comments from the database
        currentPostRef.child("comments").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get the comments snapshot
                DataSnapshot commentsSnapshot = task.getResult();

                // Check if the comments snapshot exists
                if (commentsSnapshot.exists()) {
                    // Get the comments
                    java.util.ArrayList<Comment> comments = new java.util.ArrayList<>();
                    for (DataSnapshot commentSnapshot : commentsSnapshot.getChildren()) {
                        // Get the comment elements
                        String username = commentSnapshot.child("username").getValue(String.class);
                        String body = commentSnapshot.child("body").getValue(String.class);
                        Long timestamp = commentSnapshot.child("time").getValue(Long.class);

                        // Print the comment elements
                        System.out.println("Comment Elements");
                        System.out.println("Username: " + username);
                        System.out.println("Body: " + body);
                        System.out.println("Timestamp: " + timestamp);

                        // Add the comment to the comments list
                        comments.add(new Comment(username, body, commentSnapshot.getKey(), timestamp));
                    }

                    // Create a new comment adapter
                    CommentAdapter commentAdapter = new CommentAdapter(this, comments);

                    // Set the comment adapter to the recycler view
                    commentsRecyclerView.setAdapter(commentAdapter);
                } else {
                    // No comments have been made yet
                    // Create an empty comments list
                    java.util.ArrayList<Comment> comments = new java.util.ArrayList<>();

                    // Create a new comment adapter
                    CommentAdapter commentAdapter = new CommentAdapter(this, comments);

                    // Set the comment adapter to the recycler view
                    commentsRecyclerView.setAdapter(commentAdapter);
                }
            } else {
                // No comments have been made yet
                // Create an empty comments list
                java.util.ArrayList<Comment> comments = new java.util.ArrayList<>();

                // Create a new comment adapter
                CommentAdapter commentAdapter = new CommentAdapter(this, comments);

                // Set the comment adapter to the recycler view
                commentsRecyclerView.setAdapter(commentAdapter);
            }
        });
        currentPostRef = rootRef.getReferenceFromUrl(getString(R.string.firebase_database_url)).child("post").child(postID);
        DatabaseReference commentsRef1 = currentPostRef.child("comments");

        // Add on Child Added listener to the comments reference
        commentsRef1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    System.out.println(snapshot.getRef());

                    System.out.println("Key" +snapshot.getKey());

                    // Print all children of the snapshot
                    System.out.println("Snapshot Children");
                    for (com.google.firebase.database.DataSnapshot commentSnapshot : snapshot.getChildren()) {
                        System.out.println(commentSnapshot.getKey() + " : " + commentSnapshot.getValue());
                    }

                    System.out.println("Child Added");
                    System.out.println("Snapshot: " + snapshot.toString());

                    // Get the comment elements
                    String username = snapshot.child("username").getValue().toString();

                    if (snapshot.child("body").getValue() == null) {
                        return;
                    }
                    String body = snapshot.child("body").getValue().toString();

                    if (snapshot.child("time").getValue() == null) {
                        return;
                    }
                    Long timestamp1 = snapshot.child("time").getValue(Long.class);
                    // Long timestamp = Long.getLong(snapshot.child("time").getValue().toString());

                    // Print the comment elements
                    System.out.println("Comment Elements");
                    System.out.println("Username: " + username);
                    System.out.println("Body: " + body);
                    System.out.println("Timestamp: " + timestamp1);

                    // Create a new comment
                    Comment comment = new Comment(username, body, snapshot.getKey(), timestamp1);

                    // Get the comment adapter
                    CommentAdapter commentAdapter = (CommentAdapter) commentsRecyclerView.getAdapter();

                    // Add the comment to the comment adapter
                    commentAdapter.comments.add(comment);

                    // Notify the comment adapter that the data has changed
                    commentAdapter.notifyDataSetChanged();

                    // Set the post number of comments
                    postNumComments.setText(String.valueOf(commentAdapter.comments.size()));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Get the comment elements
                String username = snapshot.child("username").getValue(String.class);

                if (snapshot.child("body").getValue() == null) {
                    return;
                }
                String body = snapshot.child("body").getValue(String.class);

                if (snapshot.child("time").getValue() == null) {
                    return;
                }
                Long timestamp = snapshot.child("time").getValue(Long.class);

                // Print the comment elements
                System.out.println("Comment Elements");
                System.out.println("Username: " + username);
                System.out.println("Body: " + body);
                System.out.println("Timestamp: " + timestamp);

                // Create a new comment
                Comment comment = new Comment(username, body, snapshot.getKey(), timestamp);

                // Get the comment adapter
                CommentAdapter commentAdapter = (CommentAdapter) commentsRecyclerView.getAdapter();

                // Get matching comment
                Comment matchingComment = null;
                for (Comment c : commentAdapter.comments) {
                    if (c.commentId.equals(comment.commentId)) {
                        matchingComment = c;
                        break;
                    }
                }

                // Remove the matching comment
                commentAdapter.comments.remove(matchingComment);

                // Add the comment to the comment adapter
                commentAdapter.comments.add(comment);

                // Notify the comment adapter that the data has changed
                commentAdapter.notifyDataSetChanged();

                // Set the post number of comments
                postNumComments.setText(String.valueOf(commentAdapter.comments.size()));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Get the comment elements
                String username = snapshot.child("username").getValue(String.class);
                String body = snapshot.child("body").getValue(String.class);
                Long timestamp = snapshot.child("time").getValue(Long.class);

                // Print the comment elements
                System.out.println("Comment Elements");
                System.out.println("Username: " + username);
                System.out.println("Body: " + body);
                System.out.println("Timestamp: " + timestamp);

                // Match the comment to be removed
                Comment comment = new Comment(username, body, snapshot.getKey(), timestamp);

                // Get the comment adapter
                CommentAdapter commentAdapter = (CommentAdapter) commentsRecyclerView.getAdapter();

                // Remove the comment from the comment adapter
                commentAdapter.comments.remove(comment);

                // Notify the comment adapter that the data has changed
                commentAdapter.notifyDataSetChanged();
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

        // Add listener to Add comment button
        findViewById(R.id.message_board_post_comment_submit_button).setOnClickListener(view -> {
            System.out.println("Add comment button clicked");

            String newComment = ((android.widget.EditText) findViewById(R.id.message_board_own_post_comment_edit_text)).getText().toString();

            System.out.println("New Comment: " + newComment);

            // Check if the comment is empty
            if (newComment.isEmpty()) {
                Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            System.out.println("newComment not empty");
            System.out.println("Current Post Reference: " + currentPostRef);

            // Get the current time
            Long timestamp = System.currentTimeMillis();

            System.out.println("Timestamp: " + timestamp);

            // Get the username from shared preferences
            String username = sharedPreferences.getString("currentUserName", null);

            System.out.println("Username: " + username);

            // Retrieve the comments reference
            if (currentPostRef == null) {
                Toast.makeText(this, "Post not found. It might have been deleted by the owner.", Toast.LENGTH_SHORT).show();

                // Set the result of activity to cancelled
                setResult(RESULT_CANCELED);
                finish();
            }

            if (currentPostRef.child("comments") == null) {

                // No comments have been made yet
                // Make a child comments reference
                currentPostRef.child("comments");

                // Create a new comments reference
                DatabaseReference commentsRef = currentPostRef.push();
                DatabaseReference newCommentRef = commentsRef.push();

                newCommentRef.child("username").setValue(username);
                newCommentRef.child("body").setValue(newComment);
                newCommentRef.child("time").setValue(timestamp);

                // Clear the comment edit text
                ((android.widget.EditText) findViewById(R.id.message_board_own_post_comment_edit_text)).setText("");
            } else {
                // Comments have been made

                // Create a new comments reference
                DatabaseReference commentsRef = currentPostRef.child("comments");
                DatabaseReference newCommentRef = commentsRef.push();

                newCommentRef.child("username").setValue(username);
                newCommentRef.child("body").setValue(newComment);
                newCommentRef.child("time").setValue(timestamp);

                // Clear the comment edit text
                ((android.widget.EditText) findViewById(R.id.message_board_own_post_comment_edit_text)).setText("");
            }
        });

        // Add listener to Plus One button
        findViewById(R.id.message_board_own_post_plus_one_button).setOnClickListener(view -> {
            System.out.println("Plus One button clicked");

            // Get the username from shared preferences
            String username = sharedPreferences.getString("currentUserName", null);

            System.out.println("Username: " + username);

            // Retrieve the plus one reference
            if (currentPostRef == null) {
                Toast.makeText(this, "Post not found. It might have been deleted by the owner.", Toast.LENGTH_SHORT).show();

                // Set the result of activity to cancelled
                setResult(RESULT_CANCELED);
                finish();
            }

            if (currentPostRef.child("plus_one") == null) {

                // No plus ones have been made yet
                // Make a child plus one reference
                currentPostRef.child("plus_one");

                // Create a new plus one reference
                DatabaseReference plusOneRef = currentPostRef.push();
                DatabaseReference newPlusOneRef = plusOneRef.push();

                newPlusOneRef.child("username").setValue(username);

                // Set the post number of plus ones
                postNumPlusOne.setText(String.valueOf(1));
            } else {
                // Plus ones have been made

                // Create a new plus one reference
                DatabaseReference plusOneRef = currentPostRef.child("plus_one");

                DatabaseReference newPlusOneRef = plusOneRef.push();

                newPlusOneRef.child("username").setValue(username);

                // Set the post number of plus ones
                postNumPlusOne.setText(String.valueOf(Integer.parseInt(postNumPlusOne.getText().toString()) + 1));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE.EDIT_POST.ordinal()) {
            if (resultCode == EDITPOST_RESULT.SUCCESS.ordinal()) {
                System.out.println("EDIT POST : result" + EDITPOST_RESULT.SUCCESS.ordinal());

                // Update the post from the database
                currentPostRef.get().addOnCompleteListener(
                        v -> {
                            if (v.isSuccessful()) {
                                // Print the post snapshot including all the children
                                System.out.println("Post Snapshot");
                                for (com.google.firebase.database.DataSnapshot postSnapshot : v.getResult().getChildren()) {
                                    System.out.println(postSnapshot.getKey() + " : " + postSnapshot.getValue());
                                }

                                DataSnapshot postSnapshot = v.getResult();

                                // Check if the post exists
                                if (postSnapshot.exists()) {
                                    // Get the post elements
                                    String username = postSnapshot.child("username").getValue(String.class);
                                    System.out.println("Username: " + username);
                                    System.out.println("Post User: " + postUser);
                                    // Disable the edit button if the post is not owned by the current user
                                    if (!postUser.equals(username)) {
                                        findViewById(R.id.message_board_own_post_edit_button).setVisibility(android.view.View.GONE);
                                    }

                                    String title = postSnapshot.child("title").getValue(String.class);
                                    String body = postSnapshot.child("body").getValue(String.class).substring(0, min(
                                            postSnapshot.child("body").getValue(String.class).length(), 400
                                    ));
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
                                    runOnUiThread(() -> postBody.setText(body));
                                    // Set the post title
                                    runOnUiThread(() -> postTitle.setText(title));
                                    // Set the post time
                                    runOnUiThread(() -> postTime.setText(date.toString()));
                                    // Set the post number of comments
                                    runOnUiThread(() -> postNumComments.setText(numComments));
                                    // Set the post number of plus ones
                                    runOnUiThread(() -> postNumPlusOne.setText(numPlusOne));
                        }
                    }
                });
            } else {
                System.out.println("EDIT POST : Failed to edit post");
            }
        }
    }
}