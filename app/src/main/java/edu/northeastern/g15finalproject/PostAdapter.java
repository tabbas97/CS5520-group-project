package edu.northeastern.g15finalproject;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<MessageBoardItemHolder> {

    private final List<MessageBoardItem> posts;
    private final Context context;

    public PostAdapter(List<MessageBoardItem> posts, Context context) {
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
        holder.postTime.setText(posts.get(position).getPostTime());
        holder.postNumComments.setText(posts.get(position).getPostNumComments());
        holder.postNumPlusOne.setText(posts.get(position).getPostNumPlusOne());

        holder.itemView.setOnClickListener(view -> {
            // Launch the post in activity own post activity
            android.content.Intent intent = new android.content.Intent(context, MessageBoardOwnPostActivity.class);

            // Place the post id about the post in the intent
            intent.putExtra("postID", posts.get(position).getPostID());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
