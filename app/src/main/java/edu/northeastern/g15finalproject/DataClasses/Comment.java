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
    public final List<String> commentLikes;
    public final long timeStamp;
    public final String postId;

    public Comment(String poster, String comment, String commentId, List<String> commentLikes, long timeStamp, String postId) {
        this.poster = poster;
        this.comment = comment;
        this.commentId = commentId;
        this.commentLikes = commentLikes;
        this.timeStamp = timeStamp;
        this.postId = postId;
    }
}
