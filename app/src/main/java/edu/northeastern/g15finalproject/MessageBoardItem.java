package edu.northeastern.g15finalproject;

public class MessageBoardItem {

    private final String postTitle;
    private final String postHeadline;
    private final String postTime;
    private final String postNumComments;
    private final String postNumPlusOne;

    public MessageBoardItem(String postTitle, String postHeadline, String postTime, String postNumComments, String postNumPlusOne) {
        this.postTitle = postTitle;
        this.postHeadline = postHeadline;
        this.postTime = postTime;
        this.postNumComments = postNumComments;
        this.postNumPlusOne = postNumPlusOne;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public String getPostHeadline() {
        return postHeadline;
    }

    public String getPostTime() {
        return postTime;
    }

    public String getPostNumComments() {
        return postNumComments;
    }

    public String getPostNumPlusOne() {
        return postNumPlusOne;
    }
}
