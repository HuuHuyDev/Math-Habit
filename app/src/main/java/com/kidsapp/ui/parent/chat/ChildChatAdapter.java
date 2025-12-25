package com.kidsapp.ui.parent.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.databinding.ItemChildChatBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị danh sách con để chat
 */
public class ChildChatAdapter extends RecyclerView.Adapter<ChildChatAdapter.ViewHolder> {

    private List<ChildChatItem> children = new ArrayList<>();
    private OnChildChatClickListener listener;

    public interface OnChildChatClickListener {
        void onChildChatClick(ChildChatItem child);
    }

    public ChildChatAdapter(OnChildChatClickListener listener) {
        this.listener = listener;
    }

    public void setChildren(List<ChildChatItem> children) {
        this.children = children != null ? children : new ArrayList<>();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChildChatBinding binding = ItemChildChatBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(children.get(position));
    }

    @Override
    public int getItemCount() {
        return children.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemChildChatBinding binding;

        ViewHolder(ItemChildChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChildChatItem child) {
            binding.txtName.setText(child.getName());
            binding.txtLastMessage.setText(child.getLastMessage());
            binding.txtTime.setText(child.getLastMessageTime());
            binding.txtLevel.setText("Level " + child.getLevel());
            
            // Online status
            binding.statusOnline.setVisibility(child.isOnline() ? View.VISIBLE : View.GONE);
            
            // Unread badge
            if (child.getUnreadCount() > 0) {
                binding.txtUnreadCount.setVisibility(View.VISIBLE);
                binding.txtUnreadCount.setText(String.valueOf(child.getUnreadCount()));
            } else {
                binding.txtUnreadCount.setVisibility(View.GONE);
            }
            
            // Avatar - có thể load từ URL nếu có
            binding.imgAvatar.setImageResource(R.drawable.ic_avatar_default);
            
            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChildChatClick(child);
                }
            });
        }
    }
}
