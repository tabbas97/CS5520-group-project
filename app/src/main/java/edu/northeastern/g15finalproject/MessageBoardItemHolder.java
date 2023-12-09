package edu.northeastern.g15finalproject;

import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageBoardItemHolder extends RecyclerView.ViewHolder {

    public TextView postTitle;
    public TextView postHeadline;
    public TextView postTime;
    public TextView postNumComments;
    public TextView postNumPlusOne;

    public boolean isSelect = false;

    public MessageBoardItemHolder(@NonNull android.view.View itemView) {
        super(itemView);
        this.postTitle = itemView.findViewById(R.id.post_title);
        this.postHeadline = itemView.findViewById(R.id.post_headline);
        this.postTime = itemView.findViewById(R.id.post_time);
        this.postNumComments = itemView.findViewById(R.id.post_num_comments);
        this.postNumPlusOne = itemView.findViewById(R.id.post_plus_one_count);
    }
}
