package com.kidsapp.ui.child.challenge;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.data.model.ChatMessage;
import com.kidsapp.databinding.ItemBattleChatMessageBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatViewHolder> {

    private final List<ChatMessage> messages = new ArrayList<>();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void setMessages(List<ChatMessage> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBattleChatMessageBinding binding = ItemBattleChatMessageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ChatViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        private final ItemBattleChatMessageBinding binding;

        ChatViewHolder(ItemBattleChatMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChatMessage message) {
            binding.txtMessage.setText(message.getMessage());
            binding.txtTime.setText(timeFormat.format(new Date(message.getTimestamp())));

            if (message.isMe()) {
                // My message - align right
                binding.layoutMessage.setGravity(Gravity.END);
                binding.imgAvatar.setVisibility(View.GONE);
                binding.txtSenderName.setVisibility(View.GONE);
                binding.txtMessage.setBackgroundResource(R.drawable.bg_chat_bubble_me);
                binding.txtMessage.setTextColor(itemView.getContext().getColor(android.R.color.white));
            } else {
                // Opponent message - align left
                binding.layoutMessage.setGravity(Gravity.START);
                binding.imgAvatar.setVisibility(View.VISIBLE);
                binding.txtSenderName.setVisibility(View.VISIBLE);
                binding.txtSenderName.setText(message.getSenderName());
                binding.txtMessage.setBackgroundResource(R.drawable.bg_chat_bubble_opponent);
                binding.txtMessage.setTextColor(itemView.getContext().getColor(R.color.text_primary));
            }
        }
    }
}
