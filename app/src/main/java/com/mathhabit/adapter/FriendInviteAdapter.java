package com.kidsapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.databinding.ItemFriendInviteBinding;
import com.kidsapp.model.Friend;

import java.util.List;

public class FriendInviteAdapter extends RecyclerView.Adapter<FriendInviteAdapter.FriendViewHolder> {
    
    private List<Friend> friendsList;
    private OnFriendInviteClickListener listener;
    
    public interface OnFriendInviteClickListener {
        void onFriendInviteClick(Friend friend);
    }
    
    public FriendInviteAdapter(List<Friend> friendsList, OnFriendInviteClickListener listener) {
        this.friendsList = friendsList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFriendInviteBinding binding = ItemFriendInviteBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FriendViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friendsList.get(position);
        holder.bind(friend);
    }
    
    @Override
    public int getItemCount() {
        return friendsList.size();
    }
    
    public void updateList(List<Friend> newList) {
        this.friendsList = newList;
        notifyDataSetChanged();
    }
    
    class FriendViewHolder extends RecyclerView.ViewHolder {
        private ItemFriendInviteBinding binding;
        
        public FriendViewHolder(ItemFriendInviteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        
        public void bind(Friend friend) {
            binding.txtFriendName.setText(friend.getName());
            binding.txtLevel.setText(friend.getLevel());
            binding.txtStatus.setText(friend.getStatus());
            
            if (friend.isOnline()) {
                binding.txtStatus.setTextColor(itemView.getContext().getColor(com.kidsapp.R.color.status_success));
            } else {
                binding.txtStatus.setTextColor(itemView.getContext().getColor(com.kidsapp.R.color.text_secondary));
            }
            
            // Set click listener
            binding.btnInvite.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFriendInviteClick(friend);
                }
            });
            
            // TODO: Load avatar image using Glide or similar
            // Glide.with(itemView.getContext())
            //     .load(friend.getAvatarUrl())
            //     .placeholder(R.drawable.ic_child_avatar)
            //     .into(binding.imgAvatar);
        }
    }
}