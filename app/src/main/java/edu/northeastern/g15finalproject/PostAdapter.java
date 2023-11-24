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
        holder.postHeadline.setText("Headline");
        holder.postTitle.setText("Title");
        holder.postTime.setText("Time");
        holder.postNumComments.setText("Comments");
        holder.postNumPlusOne.setText("Plus One");
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
