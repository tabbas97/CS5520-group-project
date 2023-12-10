package edu.northeastern.g15finalproject.DataClasses;

import java.util.List;

// FORMAT ON FIREBASE
/*
{
  "comment": "c1",
  "commentLikes": {
    "u1": "",
    "u2": ""
  },
  "postId": 123,
  "poster": "User1",
  "timeStamp": 134753572747
}
 */
public class Comment {
    public final String poster;
    public final String comment;
    public final String commentId;
    public final long timeStamp;
    public final String postId;

    public Comment(String poster, String comment, String commentId, long timeStamp, String postId) {
        this.poster = poster;
        this.comment = comment;
        this.commentId = commentId;
        this.timeStamp = timeStamp;
        this.postId = postId;
    }

    public Comment(String username, String body, String commentId, Long timeStamp) {
        this.poster = username;
        this.comment = body;
        this.commentId = commentId;
        this.timeStamp = timeStamp;
        this.postId = "";
    }
}
