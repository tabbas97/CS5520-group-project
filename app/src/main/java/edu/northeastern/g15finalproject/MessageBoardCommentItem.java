package edu.northeastern.g15finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.sql.Timestamp;

public class MessageBoardCommentItem {

    private final String commentContent;
    private final String commentTimeStamp;
    private final String commenterUsername;

    public MessageBoardCommentItem(String commentContent, String commentTimeStamp, String commenterUsername) {
        this.commentContent = commentContent;
        this.commentTimeStamp = commentTimeStamp;
        this.commenterUsername = commenterUsername;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public String getCommentTimeStamp() {
        return commentTimeStamp;
    }

    public String getCommenterUsername() {
        return commenterUsername;
    }

    @Override
    public String toString() {
        return "MessageBoardCommentItem{" +
                "commentContent='" + commentContent + '\'' +
                ", commentTimeStamp='" + commentTimeStamp + '\'' +
                ", commenterUsername='" + commenterUsername + '\'' +
                '}';
    }
}