package com.kidsapp.ui.child.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.databinding.ItemChatMessageBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị tin nhắn chat
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<ChatMessage> messages = new ArrayList<>();

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
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
            if (message.isFromChild()) {
                // Tin nhắn từ trẻ - hiển thị bên phải
                binding.layoutReceived.setVisibility(View.GONE);
                binding.layoutSent.setVisibility(View.VISIBLE);
                binding.txtSentMessage.setText(message.getContent());
                binding.txtSentTime.setText(message.getTime());
            } else {
                // Tin nhắn từ phụ huynh - hiển thị bên trái
                binding.layoutReceived.setVisibility(View.VISIBLE);
                binding.layoutSent.setVisibility(View.GONE);
                binding.txtReceivedMessage.setText(message.getContent());
                binding.txtReceivedTime.setText(message.getTime());
            }
        }
    }
}
