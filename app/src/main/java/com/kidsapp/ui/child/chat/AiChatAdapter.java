package com.kidsapp.ui.child.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.databinding.ItemChatAiMessageBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho RecyclerView hiển thị tin nhắn AI Chat
 */
public class AiChatAdapter extends RecyclerView.Adapter<AiChatAdapter.MessageViewHolder> {

    private List<AiChatMessage> messages = new ArrayList<>();

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatAiMessageBinding binding = ItemChatAiMessageBinding.inflate(
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

    public void addMessage(AiChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void setMessages(List<AiChatMessage> messages) {
        this.messages = new ArrayList<>(messages);
        notifyDataSetChanged();
    }

    public void clear() {
        messages.clear();
        notifyDataSetChanged();
    }

    /**
     * ViewHolder cho tin nhắn
     */
    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatAiMessageBinding binding;

        MessageViewHolder(ItemChatAiMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(AiChatMessage message) {
            if (message.isFromUser()) {
                // User message - hiển thị bên phải
                binding.layoutUserMessage.setVisibility(View.VISIBLE);
                binding.layoutAiMessage.setVisibility(View.GONE);
                
                binding.txtUserMessage.setText(message.getContent());
                binding.txtUserTime.setText(message.getTime());
            } else {
                // AI message - hiển thị bên trái
                binding.layoutAiMessage.setVisibility(View.VISIBLE);
                binding.layoutUserMessage.setVisibility(View.GONE);
                
                binding.txtAiMessage.setText(message.getContent());
                binding.txtAiTime.setText(message.getTime());
            }
        }
    }
}
