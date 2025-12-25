package com.kidsapp.ui.parent.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.databinding.FragmentParentChatRoomBinding;
import com.kidsapp.ui.child.chat.ChatAdapter;
import com.kidsapp.ui.child.chat.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment chat giữa phụ huynh và con
 */
public class ParentChatRoomFragment extends Fragment {

    private FragmentParentChatRoomBinding binding;
    private ChatAdapter adapter;
    
    private String childId;
    private String childName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentParentChatRoomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        loadArguments();
        setupViews();
        loadMockMessages();
    }

    private void loadArguments() {
        if (getArguments() != null) {
            childId = getArguments().getString("child_id", "");
            childName = getArguments().getString("child_name", "Con");
        }
    }

    private void setupViews() {
        // Header
        binding.txtChildName.setText(childName);
        
        // RecyclerView
        adapter = new ChatAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        binding.rvMessages.setLayoutManager(layoutManager);
        binding.rvMessages.setAdapter(adapter);

        // Back button - sử dụng Navigation
        binding.btnBack.setOnClickListener(v -> {
            try {
                Navigation.findNavController(requireView()).popBackStack();
            } catch (Exception e) {
                requireActivity().onBackPressed();
            }
        });

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

        // Tạo tin nhắn mới (từ phụ huynh)
        ChatMessage message = new ChatMessage(
                String.valueOf(System.currentTimeMillis()),
                content,
                getCurrentTime(),
                true // isFromCurrentUser = true (phụ huynh gửi)
        );

        adapter.addMessage(message);
        binding.edtMessage.setText("");
        
        // Scroll xuống tin nhắn mới
        binding.rvMessages.smoothScrollToPosition(adapter.getItemCount() - 1);

        // TODO: Gửi tin nhắn lên server qua WebSocket hoặc API
        // chatRepository.sendMessage(childId, message);
        
        // Demo: Con tự động trả lời sau 2s
        simulateChildReply();
    }

    private void simulateChildReply() {
        binding.getRoot().postDelayed(() -> {
            if (binding == null) return;
            
            ChatMessage reply = new ChatMessage(
                    String.valueOf(System.currentTimeMillis()),
                    "Dạ con hiểu rồi ạ! 😊",
                    getCurrentTime(),
                    false // isFromCurrentUser = false (con trả lời)
            );
            adapter.addMessage(reply);
            binding.rvMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
        }, 2000);
    }

    private void loadMockMessages() {
        List<ChatMessage> messages = new ArrayList<>();
        
        // false = tin nhắn từ con, true = tin nhắn từ phụ huynh
        messages.add(new ChatMessage("1", "Con ơi, hôm nay học bài chưa?", "09:00", true));
        messages.add(new ChatMessage("2", "Dạ con học rồi ạ! 📚", "09:05", false));
        messages.add(new ChatMessage("3", "Giỏi lắm! Con làm được mấy bài?", "09:06", true));
        messages.add(new ChatMessage("4", "Con làm được 5 bài toán ạ 🎉", "09:10", false));
        messages.add(new ChatMessage("5", "Tuyệt vời! Cố gắng lên con nhé!", "09:12", true));

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
