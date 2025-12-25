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

import com.kidsapp.databinding.FragmentChatWithParentBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment chat giữa trẻ và phụ huynh
 */
public class ChatWithParentFragment extends Fragment {

    private FragmentChatWithParentBinding binding;
    private ChatAdapter adapter;

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
        
        setupViews();
        loadMockMessages();
    }

    private void setupViews() {
        // RecyclerView
        adapter = new ChatAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        binding.rvMessages.setLayoutManager(layoutManager);
        binding.rvMessages.setAdapter(adapter);

        // Back button
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Send button
        binding.btnSend.setOnClickListener(v -> sendMessage());

        // Emoji button
        binding.btnEmoji.setOnClickListener(v -> {
            // TODO: Show emoji picker
        });
    }

    private void sendMessage() {
        String content = binding.edtMessage.getText().toString().trim();
        if (TextUtils.isEmpty(content)) return;

        // Tạo tin nhắn mới
        ChatMessage message = new ChatMessage(
                String.valueOf(System.currentTimeMillis()),
                content,
                getCurrentTime(),
                true // từ trẻ
        );

        adapter.addMessage(message);
        binding.edtMessage.setText("");
        
        // Scroll xuống tin nhắn mới
        binding.rvMessages.smoothScrollToPosition(adapter.getItemCount() - 1);

        // TODO: Gửi tin nhắn lên server
        // chatRepository.sendMessage(message);
        
        // Demo: Phụ huynh tự động trả lời sau 2s
        simulateParentReply();
    }

    private void simulateParentReply() {
        binding.getRoot().postDelayed(() -> {
            if (binding == null) return;
            
            ChatMessage reply = new ChatMessage(
                    String.valueOf(System.currentTimeMillis()),
                    "Con học giỏi lắm! Bố/Mẹ rất tự hào về con 💪",
                    getCurrentTime(),
                    false // từ phụ huynh
            );
            adapter.addMessage(reply);
            binding.rvMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
        }, 2000);
    }

    private void loadMockMessages() {
        List<ChatMessage> messages = new ArrayList<>();
        
        messages.add(new ChatMessage("1", "Con ơi, hôm nay học bài chưa?", "09:00", false));
        messages.add(new ChatMessage("2", "Dạ con học rồi ạ! 📚", "09:05", true));
        messages.add(new ChatMessage("3", "Giỏi lắm! Con làm được mấy bài?", "09:06", false));
        messages.add(new ChatMessage("4", "Con làm được 5 bài toán ạ 🎉", "09:10", true));
        messages.add(new ChatMessage("5", "Tuyệt vời! Cố gắng lên con nhé!", "09:12", false));

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
