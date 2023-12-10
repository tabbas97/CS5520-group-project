package edu.northeastern.g15finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.g15finalproject.DataClasses.Comment;

public class CommentAdapter extends RecyclerView.Adapter<CommentHolder> {

    final List<Comment> comments;
    private final Context context;

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comment_item_layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        holder.commentBody.setText(comments.get(position).comment);
        holder.commentPoster.setText(comments.get(position).poster);
        // Convert timestamp to readable format
        holder.commentTime.setText(
                new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                        .format(new java.util.Date(comments.get(position).timeStamp))
        );

        holder.itemView.setOnLongClickListener(v -> {
            Toast.makeText(this.context, "Cannot modify comments as of now", Toast.LENGTH_SHORT).show();
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(this.context, "Cannot modify comments as of now", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
