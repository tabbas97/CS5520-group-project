package edu.northeastern.g15finalproject;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentHolder extends RecyclerView.ViewHolder {

    public final TextView commentBody;
    public final TextView commentPoster;
    public final TextView commentTime;

    public CommentHolder(@NonNull View itemView) {
        super(itemView);

        this.commentBody = itemView.findViewById(R.id.commentContentTV);
        this.commentPoster = itemView.findViewById(R.id.commentPosterNameTV);
        this.commentTime = itemView.findViewById(R.id.commentTimeStampTV);
    }
}
