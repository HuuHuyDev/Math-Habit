package com.kidsapp.ui.child.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.databinding.ItemChatMessageBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị tin nhắn chat
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<ChatMessage> messages = new ArrayList<>();

    public void setMessages(List<ChatMessage> messages) {
        this.messages = new ArrayList<>(messages);
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    /**
     * Cập nhật status của tin nhắn
     */
    public void updateMessageStatus(String tempIdPrefix, String newId, int status) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            ChatMessage msg = messages.get(i);
            if (msg.getId().startsWith(tempIdPrefix)) {
                msg.setId(newId);
                msg.setStatus(status);
                notifyItemChanged(i);
                break;
            }
        }
    }

    /**
     * Đánh dấu tất cả tin nhắn đã đọc
     */
    public void markAllAsRead() {
        for (int i = 0; i < messages.size(); i++) {
            ChatMessage msg = messages.get(i);
            if (msg.isFromCurrentUser() && msg.getStatus() != ChatMessage.STATUS_READ) {
                msg.setStatus(ChatMessage.STATUS_READ);
                notifyItemChanged(i);
            }
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatMessageBinding binding = ItemChatMessageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MessageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatMessageBinding binding;

        MessageViewHolder(ItemChatMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChatMessage message) {
            if (message.isFromCurrentUser()) {
                // Tin nhắn từ user hiện tại - hiển thị bên phải
                binding.layoutReceived.setVisibility(View.GONE);
                binding.layoutSent.setVisibility(View.VISIBLE);
                binding.txtSentMessage.setText(message.getContent());
                binding.txtSentTime.setText(message.getTime());
                
                // Hiển thị status
                if (binding.imgSentStatus != null) {
                    binding.imgSentStatus.setVisibility(View.VISIBLE);
                    switch (message.getStatus()) {
                        case ChatMessage.STATUS_SENDING:
                            binding.imgSentStatus.setImageResource(R.drawable.ic_clock);
                            break;
                        case ChatMessage.STATUS_SENT:
                            binding.imgSentStatus.setImageResource(R.drawable.ic_check);
                            break;
                        case ChatMessage.STATUS_DELIVERED:
                            binding.imgSentStatus.setImageResource(R.drawable.ic_check_double);
                            break;
                        case ChatMessage.STATUS_READ:
                            binding.imgSentStatus.setImageResource(R.drawable.ic_check_double_blue);
                            break;
                        case ChatMessage.STATUS_FAILED:
                            binding.imgSentStatus.setImageResource(R.drawable.ic_error);
                            break;
                    }
                }
            } else {
                // Tin nhắn từ người khác - hiển thị bên trái
                binding.layoutReceived.setVisibility(View.VISIBLE);
                binding.layoutSent.setVisibility(View.GONE);
                binding.txtReceivedMessage.setText(message.getContent());
                binding.txtReceivedTime.setText(message.getTime());
            }
        }
    }
}
