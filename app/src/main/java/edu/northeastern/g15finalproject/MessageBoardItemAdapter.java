package edu.northeastern.g15finalproject;

import android.content.Context;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class MessageBoardItemAdapter extends RecyclerView.Adapter<MessageBoardItemHolder> {

    private final List<MessageBoardItem> posts;
    private final Context context;

    public MessageBoardItemAdapter(List<MessageBoardItem> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    @Override
    public MessageBoardItemHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        return new MessageBoardItemHolder(
                android.view.LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_board_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(MessageBoardItemHolder holder, int position) {
        // Set the values of the post at the given position
        holder.postHeadline.setText(posts.get(position).getPostHeadline());
        holder.postTitle.setText(posts.get(position).getPostTitle());
        // Convert the post time from UTC milliseconds to a readable format in miltary time
        holder.postTime.setText(
                new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                        .format(new java.util.Date(posts.get(position).getPostTime()))
        );
        holder.postNumComments.setText(posts.get(position).getPostNumComments());
        holder.postNumPlusOne.setText(posts.get(position).getPostNumPlusOne());

        holder.itemView.setOnClickListener(view -> {
            // Launch the post in activity own post activity
            android.content.Intent intent = new android.content.Intent(context, MessageBoardPostViewActivity.class);

            // Place the post id about the post in the intent
            intent.putExtra("postID", posts.get(position).getPostID());

            context.startActivity(intent);
        });

        // Launch a dialog to ask if the user wants to delete the post after checking the owner of the post is the current user
        holder.itemView.setOnLongClickListener( view -> {
                    // Owner of Post
                    String owner = posts.get(position).getPostUser();

                    // Current User from shared preferences
                    android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("userdata", android.content.Context.MODE_PRIVATE);
                    String currentUser = sharedPreferences.getString("currentUserName", null);

                    if (owner.equals(currentUser)) {
                        // Create a dialog to ask if the user wants to delete the post
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                        builder.setTitle("Delete Post");
                        builder.setMessage("Are you sure you want to delete this post?");
                        builder.setPositiveButton("Yes", (dialog, which) -> {
                            // Delete the post
                            deletePost(posts.get(position).getPostID());
                        });
                        builder.setNegativeButton("No", (dialog, which) -> {
                            // Do nothing
                        });
                        builder.show();
                        return true;
                    } else {
                        // Do nothing
                        Toast.makeText(context, "You can only delete your own posts", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
        );
    }

    // Delete the post with the given post id
    private void deletePost(String postID) {
        // Create a FirebaseRTDB instance
        com.google.firebase.database.FirebaseDatabase firebaseDatabase = com.google.firebase.database.FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReferenceFromUrl(R.string.firebase_database_url + "/post");

        // Delete the post with the given post id
        databaseReference.child(postID).removeValue().addOnSuccessListener(aVoid -> {
            // Delete the post from the list
            for (int i = 0; i < posts.size(); i++) {
                if (posts.get(i).getPostID().equals(postID)) {
                    posts.remove(i);
                    notifyItemRemoved(i);
                    break;
                }
            }
        }).addOnFailureListener(e -> {
            // Do nothing
            Toast.makeText(context, "Failed to delete post. Please try again.", Toast.LENGTH_SHORT).show();
        });
    }



    @Override
    public int getItemCount() {
        return posts.size();
    }
}
