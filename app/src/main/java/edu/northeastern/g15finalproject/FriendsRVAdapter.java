package edu.northeastern.g15finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class FriendsRVAdapter extends RecyclerView.Adapter<FriendsRVAdapter.FriendsViewHolder>  {
    private final List<User> friends;

    private final Context context;

    public FriendsRVAdapter(List<User> friends, Context context) {
        this.friends = friends;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendsViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.friend_row_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder holder, int position) {
        User friend = friends.get(position);
        holder.fullName.setText(friend.getFullName());
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }


    public class FriendsViewHolder extends RecyclerView.ViewHolder {
        public TextView fullName;
        public Button deleteFriend;

        public FriendsViewHolder(@NonNull View friendsView) {
            super(friendsView);
            fullName = friendsView.findViewById(R.id.friend_name);
            deleteFriend = friendsView.findViewById(R.id.delete_friend);
        }
    }

}
