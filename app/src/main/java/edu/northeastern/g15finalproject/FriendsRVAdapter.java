package edu.northeastern.g15finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class FriendsRVAdapter extends RecyclerView.Adapter<FriendsRVAdapter.FriendsViewHolder>  {
    private final List<User> friends;

    private final Context context;

    private OnAdapterClickListener listener;

    public FriendsRVAdapter(List<User> friends, Context context) {
        this.friends = friends;
        this.context = context;
    }

    public interface OnAdapterClickListener {
        void onItemClick(View friendsView, int position);
        void onAddSosClick(View friendsView, int position);
        void onDeleteClick(View friendsView, int position);
    }
    public void setOnItemClickListener(OnAdapterClickListener listener) {
        this.listener = listener;
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

        public ImageButton addsos;
        public ImageButton deleteFriend;

        public FriendsViewHolder(@NonNull View friendsView) {
            super(friendsView);
            fullName = friendsView.findViewById(R.id.friend_name);
            deleteFriend = friendsView.findViewById(R.id.delete_friend);
            addsos = friendsView.findViewById(R.id.sos_friend);

            friendsView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(friendsView, position);
                    }
                }
            });
            this.addsos.setOnClickListener(v -> {
                if(listener!=null){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onAddSosClick(friendsView, position);
                    }
                }
            });
            this.deleteFriend.setOnClickListener(v -> {
                if(listener!=null){
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(friendsView, position);
                    }
                }
            });
        }
    }

}
