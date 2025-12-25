package com.kidsapp.ui.parent.chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.websocket.ChatMessageDto;
import com.kidsapp.data.websocket.ChatRoomDto;
import com.kidsapp.data.websocket.WebSocketManager;
import com.kidsapp.databinding.FragmentParentChatRoomBinding;
import com.kidsapp.ui.child.chat.ChatAdapter;
import com.kidsapp.ui.child.chat.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment chat real-time giữa phụ huynh và con
 * Sử dụng WebSocket (STOMP) để gửi/nhận tin nhắn
 */
public class ParentChatRoomFragment extends Fragment implements WebSocketManager.WebSocketListener {

    private static final String TAG = "ParentChatRoomFragment";

    private FragmentParentChatRoomBinding binding;
    private ChatAdapter adapter;
    private WebSocketManager webSocketManager;
    private SharedPref sharedPref;
    private Gson gson;
    private ApiService apiService;

    // Chat info
    private String chatRoomId;      // Room ID (nếu có)
    private String childId;         // ID của con
    private String childName;       // Tên con
    private String currentUserId;   // ID của phụ huynh (current user)

    // Typing indicator
    private Handler typingHandler = new Handler(Looper.getMainLooper());
    private Runnable hideTypingRunnable;
    private static final long TYPING_TIMEOUT = 3000;

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

        gson = new Gson();
        sharedPref = new SharedPref(requireContext());
        currentUserId = getCurrentUserId();

        // Sử dụng RetrofitClient với AuthInterceptor
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();
        
        loadArguments();
        setupViews();
        setupWebSocket();
        loadOrCreateChatRoom();
    }

    private void loadArguments() {
        if (getArguments() != null) {
            childId = getArguments().getString("child_id", "");
            childName = getArguments().getString("child_name", "Con");
            chatRoomId = getArguments().getString("room_id", "");
        }
    }

    private void setupViews() {
        // Header
        binding.txtChildName.setText(childName);
        updateConnectionStatus(false);

        // RecyclerView
        adapter = new ChatAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        binding.rvMessages.setLayoutManager(layoutManager);
        binding.rvMessages.setAdapter(adapter);

        // Back button
        binding.btnBack.setOnClickListener(v -> {
            try {
                Navigation.findNavController(requireView()).popBackStack();
            } catch (Exception e) {
                requireActivity().onBackPressed();
            }
        });

        // Send button
        binding.btnSend.setOnClickListener(v -> sendMessage());

        // Typing indicator - gửi khi đang gõ
        binding.edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && webSocketManager != null && webSocketManager.isConnected()) {
                    webSocketManager.sendTyping(childId);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Emoji button
        binding.btnEmoji.setOnClickListener(v -> {
            // TODO: Show emoji picker
            Toast.makeText(requireContext(), "Emoji picker coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupWebSocket() {
        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.addListener(this);

        // Kết nối nếu chưa kết nối
        if (!webSocketManager.isConnected()) {
            webSocketManager.connect(currentUserId);
        } else {
            updateConnectionStatus(true);
        }
    }

    /**
     * Tạo hoặc lấy phòng chat với con
     */
    private void loadOrCreateChatRoom() {
        if (chatRoomId != null && !chatRoomId.isEmpty()) {
            // Đã có room ID, load tin nhắn
            loadMessages();
            return;
        }

        // Tạo hoặc lấy phòng chat
        apiService.createOrGetChatRoom(currentUserId, childId, "PARENT_CHILD")
                .enqueue(new Callback<ApiService.ApiResponseWrapper<ChatRoomDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<ChatRoomDto>> call,
                                           @NonNull Response<ApiService.ApiResponseWrapper<ChatRoomDto>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                            chatRoomId = response.body().data.getId();
                            Log.d(TAG, "Chat room created/found: " + chatRoomId);
                            loadMessages();
                        } else {
                            Log.e(TAG, "Failed to create/get chat room: " + response.code());
                            showError("Không thể tạo phòng chat");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<ChatRoomDto>> call,
                                          @NonNull Throwable t) {
                        Log.e(TAG, "Error creating chat room: " + t.getMessage());
                        showError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    /**
     * Load tin nhắn từ server
     */
    private void loadMessages() {
        if (chatRoomId == null || chatRoomId.isEmpty()) {
            Log.w(TAG, "No chat room ID, cannot load messages");
            return;
        }

        apiService.getChatMessages(chatRoomId, currentUserId, 0, 50)
                .enqueue(new Callback<ApiService.ApiResponseWrapper<List<ChatMessageDto>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<List<ChatMessageDto>>> call,
                                           @NonNull Response<ApiService.ApiResponseWrapper<List<ChatMessageDto>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                            List<ChatMessage> messages = convertToMessages(response.body().data);
                            adapter.setMessages(messages);
                            scrollToBottom();

                            // Đánh dấu đã đọc
                            if (webSocketManager.isConnected()) {
                                webSocketManager.markAsRead(chatRoomId);
                            }
                        } else {
                            Log.e(TAG, "Load messages failed: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<List<ChatMessageDto>>> call,
                                          @NonNull Throwable t) {
                        Log.e(TAG, "Load messages error: " + t.getMessage());
                    }
                });
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private List<ChatMessage> convertToMessages(List<ChatMessageDto> dtos) {
        List<ChatMessage> messages = new ArrayList<>();
        for (ChatMessageDto dto : dtos) {
            // isFromMe = true nếu senderId == currentUserId (phụ huynh gửi)
            boolean isFromMe = dto.getSenderId().equals(currentUserId);
            ChatMessage message = new ChatMessage(
                    dto.getId(),
                    dto.getContent(),
                    formatTime(dto.getCreatedAt()),
                    isFromMe
            );
            message.setSenderName(dto.getSenderName());
            message.setSenderAvatar(dto.getSenderAvatar());
            messages.add(message);
        }
        return messages;
    }

    /**
     * Gửi tin nhắn
     */
    private void sendMessage() {
        String content = binding.edtMessage.getText().toString().trim();
        if (TextUtils.isEmpty(content)) return;

        // Tạo tin nhắn local để hiển thị ngay
        ChatMessage localMessage = new ChatMessage(
                "temp_" + System.currentTimeMillis(),
                content,
                getCurrentTime(),
                true // isFromMe = true (phụ huynh gửi)
        );
        localMessage.setStatus(ChatMessage.STATUS_SENDING);

        adapter.addMessage(localMessage);
        binding.edtMessage.setText("");
        scrollToBottom();

        // Gửi qua WebSocket
        if (webSocketManager.isConnected()) {
            webSocketManager.sendMessage(childId, content);
        } else {
            // Fallback: Gửi qua REST API
            sendMessageViaRest(content, localMessage);
        }
    }

    private void sendMessageViaRest(String content, ChatMessage localMessage) {
        Log.w(TAG, "WebSocket not connected, using REST fallback");
        // TODO: Implement REST fallback
        localMessage.setStatus(ChatMessage.STATUS_SENT);
        adapter.notifyDataSetChanged();
    }

    private void scrollToBottom() {
        if (adapter.getItemCount() > 0) {
            binding.rvMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
        }
    }

    private void updateConnectionStatus(boolean connected) {
        if (binding == null) return;

        if (connected) {
            binding.txtStatus.setText("Đang hoạt động");
            binding.txtStatus.setTextColor(getResources().getColor(R.color.green_500, null));
        } else {
            binding.txtStatus.setText("Đang kết nối...");
            binding.txtStatus.setTextColor(getResources().getColor(R.color.gray_400, null));
        }
    }

    private void showTypingIndicator() {
        if (binding == null) return;

        binding.txtStatus.setText(childName + " đang nhập...");

        // Ẩn sau timeout
        if (hideTypingRunnable != null) {
            typingHandler.removeCallbacks(hideTypingRunnable);
        }
        hideTypingRunnable = () -> {
            if (binding != null && webSocketManager.isConnected()) {
                binding.txtStatus.setText("Đang hoạt động");
            }
        };
        typingHandler.postDelayed(hideTypingRunnable, TYPING_TIMEOUT);
    }

    private String getCurrentUserId() {
        String userId = sharedPref.getUserId();
        if (userId == null || userId.isEmpty()) {
            Log.w(TAG, "User ID not found in SharedPref");
            return "";
        }
        return userId;
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    private String formatTime(String isoTime) {
        if (isoTime == null) return "";
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = isoFormat.parse(isoTime);
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
        } catch (Exception e) {
            return isoTime;
        }
    }

    // ==================== WebSocket Callbacks ====================

    @Override
    public void onConnected() {
        Log.d(TAG, "WebSocket connected");
        requireActivity().runOnUiThread(() -> updateConnectionStatus(true));
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "WebSocket disconnected");
        requireActivity().runOnUiThread(() -> updateConnectionStatus(false));
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e(TAG, "WebSocket error: " + throwable.getMessage());
        requireActivity().runOnUiThread(() -> {
            updateConnectionStatus(false);
            Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onMessageReceived(String messageJson) {
        Log.d(TAG, "Message received: " + messageJson);

        requireActivity().runOnUiThread(() -> {
            try {
                ChatMessageDto dto = gson.fromJson(messageJson, ChatMessageDto.class);

                // Chỉ hiển thị tin nhắn từ con đang chat
                if (dto.getSenderId().equals(childId)) {
                    ChatMessage message = new ChatMessage(
                            dto.getId(),
                            dto.getContent(),
                            formatTime(dto.getCreatedAt()),
                            false // isFromMe = false (con gửi)
                    );
                    message.setSenderName(dto.getSenderName());

                    adapter.addMessage(message);
                    scrollToBottom();

                    // Đánh dấu đã đọc
                    if (chatRoomId != null && !chatRoomId.isEmpty()) {
                        webSocketManager.markAsRead(chatRoomId);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Parse message error: " + e.getMessage());
            }
        });
    }

    @Override
    public void onMessageSent(String messageJson) {
        Log.d(TAG, "Message sent confirmation: " + messageJson);

        requireActivity().runOnUiThread(() -> {
            try {
                ChatMessageDto dto = gson.fromJson(messageJson, ChatMessageDto.class);

                // Cập nhật tin nhắn local với ID từ server
                adapter.updateMessageStatus("temp_", dto.getId(), ChatMessage.STATUS_SENT);

                // Cập nhật roomId nếu chưa có
                if (chatRoomId == null || chatRoomId.isEmpty()) {
                    chatRoomId = dto.getRoomId();
                }
            } catch (Exception e) {
                Log.e(TAG, "Parse sent confirmation error: " + e.getMessage());
            }
        });
    }

    @Override
    public void onTyping(String senderId) {
        // Hiển thị typing indicator nếu con đang gõ
        if (senderId.equals(childId)) {
            requireActivity().runOnUiThread(this::showTypingIndicator);
        }
    }

    @Override
    public void onReadReceipt(String roomId) {
        if (roomId.equals(chatRoomId)) {
            requireActivity().runOnUiThread(() -> {
                adapter.markAllAsRead();
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        webSocketManager.removeListener(this);
        if (hideTypingRunnable != null) {
            typingHandler.removeCallbacks(hideTypingRunnable);
        }
        binding = null;
    }
}
