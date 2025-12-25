package com.kidsapp.ui.child.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.databinding.ItemChatConversationBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị danh sách cuộc trò chuyện
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    private List<Conversation> conversations = new ArrayList<>();
    private OnConversationClickListener listener;

    public interface OnConversationClickListener {
        void onConversationClick(Conversation conversation);
    }

    public ConversationAdapter(OnConversationClickListener listener) {
        this.listener = listener;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return conversations.isEmpty();
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatConversationBinding binding = ItemChatConversationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ConversationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        holder.bind(conversations.get(position));
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatConversationBinding binding;

        ConversationViewHolder(ItemChatConversationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Conversation conversation) {
            binding.txtName.setText(conversation.getName());
            binding.txtLastMessage.setText(conversation.getLastMessage());
            binding.txtTime.setText(conversation.getLastMessageTime());

            // Avatar - dùng icon khác cho phụ huynh
            if (conversation.getType() == Conversation.TYPE_PARENT) {
                binding.imgAvatar.setImageResource(R.drawable.ic_parent_avatar);
            } else {
                binding.imgAvatar.setImageResource(R.drawable.ic_child_avatar);
            }

            // Online indicator
            binding.viewOnline.setVisibility(conversation.isOnline() ? View.VISIBLE : View.GONE);

            // Unread badge
            if (conversation.getUnreadCount() > 0) {
                binding.txtUnreadCount.setVisibility(View.VISIBLE);
                binding.txtUnreadCount.setText(String.valueOf(conversation.getUnreadCount()));
                binding.txtLastMessage.setTextColor(itemView.getContext().getColor(R.color.text_primary));
            } else {
                binding.txtUnreadCount.setVisibility(View.GONE);
                binding.txtLastMessage.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
            }

            // Click
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onConversationClick(conversation);
                }
            });
        }
    }
}
