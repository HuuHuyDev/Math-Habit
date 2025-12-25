package com.kidsapp.ui.child.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.R;
import com.kidsapp.databinding.FragmentChatWithParentBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment phòng chat - dùng chung cho cả chat với phụ huynh và bạn bè
 */
public class ChatRoomFragment extends Fragment {

    private FragmentChatWithParentBinding binding;
    private ChatAdapter adapter;
    
    private String chatId;
    private String chatName;
    private int chatType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChatWithParentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        loadArguments();
        setupViews();
        loadMessages();
    }

    private void loadArguments() {
        Bundle args = getArguments();
        if (args != null) {
            chatId = args.getString("chat_id", "");
            chatName = args.getString("chat_name", "Chat");
            chatType = args.getInt("chat_type", Conversation.TYPE_PARENT);
        }
    }

    private void setupViews() {
        // Header
        binding.txtParentName.setText(chatName);
        binding.txtStatus.setText("Đang hoạt động");
        
        // Avatar
        if (chatType == Conversation.TYPE_PARENT) {
            binding.imgParentAvatar.setImageResource(R.drawable.ic_parent_avatar);
        } else {
            binding.imgParentAvatar.setImageResource(R.drawable.ic_child_avatar);
        }

        // RecyclerView
        adapter = new ChatAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        binding.rvMessages.setLayoutManager(layoutManager);
        binding.rvMessages.setAdapter(adapter);

        // Buttons
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.btnSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String content = binding.edtMessage.getText().toString().trim();
        if (TextUtils.isEmpty(content)) return;

        ChatMessage message = new ChatMessage(
                String.valueOf(System.currentTimeMillis()),
                content,
                getCurrentTime(),
                true
        );

        adapter.addMessage(message);
        binding.edtMessage.setText("");
        binding.rvMessages.smoothScrollToPosition(adapter.getItemCount() - 1);

        // Demo: Auto reply
        simulateReply();
    }

    private void simulateReply() {
        binding.getRoot().postDelayed(() -> {
            if (binding == null) return;

            String replyText = chatType == Conversation.TYPE_PARENT 
                    ? "Bố/Mẹ đã nhận được tin nhắn của con! 💕"
                    : "Ok bạn ơi! 👍";

            ChatMessage reply = new ChatMessage(
                    String.valueOf(System.currentTimeMillis()),
                    replyText,
                    getCurrentTime(),
                    false
            );
            adapter.addMessage(reply);
            binding.rvMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
        }, 1500);
    }

    private void loadMessages() {
        List<ChatMessage> messages = new ArrayList<>();

        if (chatType == Conversation.TYPE_PARENT) {
            messages.add(new ChatMessage("1", "Con ơi, hôm nay học bài chưa?", "09:00", false));
            messages.add(new ChatMessage("2", "Dạ con học rồi ạ! 📚", "09:05", true));
            messages.add(new ChatMessage("3", "Giỏi lắm con!", "09:06", false));
        } else {
            messages.add(new ChatMessage("1", "Chào bạn! 👋", "10:00", false));
            messages.add(new ChatMessage("2", "Chào bạn! Bạn khỏe không?", "10:02", true));
            messages.add(new ChatMessage("3", "Mình khỏe! Đấu một trận không? 🎮", "10:03", false));
        }

        adapter.setMessages(messages);
        binding.rvMessages.scrollToPosition(messages.size() - 1);
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
