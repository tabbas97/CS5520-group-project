package edu.northeastern.g15finalproject.DataClasses;

// FORMAT ON FIREBASE
/*
{
  "attached_report": [
    null,
    2
  ],
  "body": "b1",
  "comments": {
    "u1": "c1",
    "u2": "c2"
  },
  "testing": false,
  "time": "utc_timestamp",
  "title": "t1",
  "username": "u1"
}
 */
public class Post {

    public final String username;
    public final String title;
    public final String body;
    public final String postId;
    public final String attached_report;
    public final long time;
    public final boolean testing;

    public Post(String username, String title, String body, String postId, String attached_report, long time, boolean testing) {
        this.username = username;
        this.title = title;
        this.body = body;
        this.postId = postId;
        this.attached_report = attached_report;
        this.time = time;
        this.testing = testing;
    }

    static Post fromJson(String username, String title, String body, String postId, String attached_report, long time, boolean testing) {
        return new Post(username, title, body, postId, attached_report, time, testing);
    }
}
