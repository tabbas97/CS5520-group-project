package edu.northeastern.g15finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MessageBoardsActivity extends AppCompatActivity {

    static RecyclerView postRecyclerView;
    static List<MessageBoardItem> postsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_boards);

        // Set of sample posts
        List<MessageBoardItem> posts = new ArrayList<>();
        posts.add(new MessageBoardItem("Title 1", "Headline 1", "Time 1", "Comments 1", "Plus One 1"));
        posts.add(new MessageBoardItem("Title 2", "Headline 2", "Time 2", "Comments 2", "Plus One 2"));
        posts.add(new MessageBoardItem("Title 3", "Headline 3", "Time 3", "Comments 3", "Plus One 3"));
        posts.add(new MessageBoardItem("Title 4", "Headline 4", "Time 4", "Comments 4", "Plus One 4"));
        posts.add(new MessageBoardItem("Title 5", "Headline 5", "Time 5", "Comments 5", "Plus One 5"));

        postRecyclerView = findViewById(R.id.message_boards_recycler_view);
        postRecyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        postRecyclerView.setAdapter(new PostAdapter(posts, this));

    }
}