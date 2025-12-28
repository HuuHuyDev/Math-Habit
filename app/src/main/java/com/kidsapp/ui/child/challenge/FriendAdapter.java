package com.kidsapp.ui.child.challenge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.data.model.Child;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị danh sách bạn bè
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private List<Child> friends = new ArrayList<>();
    private String selectedFriendId;
    private OnFriendClickListener listener;

    public interface OnFriendClickListener {
        void onFriendClick(Child friend);
    }

    public FriendAdapter(OnFriendClickListener listener) {
        this.listener = listener;
    }

    public void setFriends(List<Child> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }

    public void setSelectedFriend(String friendId) {
        this.selectedFriendId = friendId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Child friend = friends.get(position);
        holder.bind(friend);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView imgAvatar;
        TextView txtName;
        TextView txtLevel;
        ImageView imgSelected;

        FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardFriend);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            txtLevel = itemView.findViewById(R.id.txtLevel);
            imgSelected = itemView.findViewById(R.id.imgSelected);
        }

        void bind(Child friend) {
            txtName.setText(friend.getNickname() != null ? friend.getNickname() : "Bạn bè");
            txtLevel.setText("Level " + friend.getCurrentLevel());
            
            // Show selected state
            boolean isSelected = friend.getId().equals(selectedFriendId);
            imgSelected.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            cardView.setCardElevation(isSelected ? 8f : 2f);
            
            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFriendClick(friend);
                }
            });
        }
    }
}
