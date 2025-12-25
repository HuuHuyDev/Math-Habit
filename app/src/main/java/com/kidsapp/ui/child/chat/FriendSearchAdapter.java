package com.kidsapp.ui.child.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.data.response.ChildSearchResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter hiển thị danh sách bạn bè tìm kiếm được
 */
public class FriendSearchAdapter extends RecyclerView.Adapter<FriendSearchAdapter.ViewHolder> {

    private List<ChildSearchResponse> friends = new ArrayList<>();
    private List<ChildSearchResponse> filteredFriends = new ArrayList<>();
    private OnFriendClickListener listener;

    public interface OnFriendClickListener {
        void onFriendClick(ChildSearchResponse friend);
    }

    public FriendSearchAdapter(OnFriendClickListener listener) {
        this.listener = listener;
    }

    public void setFriends(List<ChildSearchResponse> friends) {
        this.friends = friends != null ? friends : new ArrayList<>();
        this.filteredFriends = new ArrayList<>(this.friends);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        filteredFriends.clear();
        if (query == null || query.isEmpty()) {
            filteredFriends.addAll(friends);
        } else {
            String lowerQuery = query.toLowerCase(Locale.getDefault());
            for (ChildSearchResponse friend : friends) {
                String name = friend.getName() != null ? friend.getName().toLowerCase(Locale.getDefault()) : "";
                String nickname = friend.getNickname() != null ? friend.getNickname().toLowerCase(Locale.getDefault()) : "";
                if (name.contains(lowerQuery) || nickname.contains(lowerQuery)) {
                    filteredFriends.add(friend);
                }
            }
        }
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return filteredFriends.isEmpty();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChildSearchResponse friend = filteredFriends.get(position);
        holder.bind(friend);
    }

    @Override
    public int getItemCount() {
        return filteredFriends.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgAvatar;
        private final TextView txtName;
        private final TextView txtLevel;
        private final View statusOnline;
        private final TextView btnChat;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            txtLevel = itemView.findViewById(R.id.txtLevel);
            statusOnline = itemView.findViewById(R.id.statusOnline);
            btnChat = itemView.findViewById(R.id.btnChat);
        }

        void bind(ChildSearchResponse friend) {
            txtName.setText(friend.getDisplayName());
            txtLevel.setText("Level " + friend.getLevel());
            
            // Online status
            statusOnline.setVisibility(friend.isOnline() ? View.VISIBLE : View.GONE);
            
            // Avatar - có thể load từ URL nếu có
            imgAvatar.setImageResource(R.drawable.ic_avatar_default);
            
            // Click để chat
            btnChat.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFriendClick(friend);
                }
            });
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFriendClick(friend);
                }
            });
        }
    }
}
