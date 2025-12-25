package com.kidsapp.ui.child.chat;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.websocket.ChatMessageDto;
import com.kidsapp.data.websocket.ChatRoomDto;
import com.kidsapp.data.websocket.WebSocketManager;
import com.kidsapp.databinding.FragmentChatWithParentBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment chat giữa trẻ và phụ huynh
 * Sử dụng WebSocket để chat real-time
 */
public class ChatWithParentFragment extends Fragment implements WebSocketManager.WebSocketListener {

    private static final String TAG = "ChatWithParentFragment";

    private FragmentChatWithParentBinding binding;
    private ChatAdapter adapter;
    private WebSocketManager webSocketManager;
    private SharedPref sharedPref;
    private Gson gson;
    private ApiService apiService;

    // Chat info
    private String chatRoomId;
    private String parentUserId;  // User ID của phụ huynh
    private String parentName;
    private String currentUserId;

    // Typing indicator
    private Handler typingHandler = new Handler(Looper.getMainLooper());
    private Runnable hideTypingRunnable;
    private static final long TYPING_TIMEOUT = 3000;

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
            parentUserId = getArguments().getString("parent_user_id", "");
            parentName = getArguments().getString("parent_name", "Phụ huynh");
            chatRoomId = getArguments().getString("room_id", "");
        }
    }

    private void setupViews() {
        // Header
        binding.txtParentName.setText(parentName);
        
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

        // Typing indicator
        binding.edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && webSocketManager != null && webSocketManager.isConnected()) {
                    webSocketManager.sendTyping(parentUserId);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Emoji button
        binding.btnEmoji.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Emoji picker coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupWebSocket() {
        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.addListener(this);

        if (!webSocketManager.isConnected()) {
            webSocketManager.connect(currentUserId);
        }
    }

    private void loadOrCreateChatRoom() {
        if (chatRoomId != null && !chatRoomId.isEmpty()) {
            loadMessages();
            return;
        }

        if (parentUserId == null || parentUserId.isEmpty()) {
            Log.e(TAG, "Parent user ID is empty");
            return;
        }

        apiService.createOrGetChatRoom(currentUserId, parentUserId, "PARENT_CHILD")
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
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<ChatRoomDto>> call,
                                          @NonNull Throwable t) {
                        Log.e(TAG, "Error creating chat room: " + t.getMessage());
                    }
                });
    }

    private void loadMessages() {
        if (chatRoomId == null || chatRoomId.isEmpty()) return;

        apiService.getChatMessages(chatRoomId, currentUserId, 0, 50)
                .enqueue(new Callback<ApiService.ApiResponseWrapper<List<ChatMessageDto>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<List<ChatMessageDto>>> call,
                                           @NonNull Response<ApiService.ApiResponseWrapper<List<ChatMessageDto>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                            List<ChatMessage> messages = convertToMessages(response.body().data);
                            adapter.setMessages(messages);
                            scrollToBottom();

                            if (webSocketManager.isConnected()) {
                                webSocketManager.markAsRead(chatRoomId);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<List<ChatMessageDto>>> call,
                                          @NonNull Throwable t) {
                        Log.e(TAG, "Load messages error: " + t.getMessage());
                    }
                });
    }

    private List<ChatMessage> convertToMessages(List<ChatMessageDto> dtos) {
        List<ChatMessage> messages = new ArrayList<>();
        for (ChatMessageDto dto : dtos) {
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

    private void sendMessage() {
        String content = binding.edtMessage.getText().toString().trim();
        if (TextUtils.isEmpty(content)) return;

        ChatMessage localMessage = new ChatMessage(
                "temp_" + System.currentTimeMillis(),
                content,
                getCurrentTime(),
                true
        );
        localMessage.setStatus(ChatMessage.STATUS_SENDING);

        adapter.addMessage(localMessage);
        binding.edtMessage.setText("");
        scrollToBottom();

        if (webSocketManager.isConnected()) {
            webSocketManager.sendMessage(parentUserId, content);
        } else {
            localMessage.setStatus(ChatMessage.STATUS_SENT);
            adapter.notifyDataSetChanged();
        }
    }

    private void scrollToBottom() {
        if (adapter.getItemCount() > 0) {
            binding.rvMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
        }
    }

    private String getCurrentUserId() {
        String userId = sharedPref.getUserId();
        if (userId == null || userId.isEmpty()) {
            userId = sharedPref.getChildId();
        }
        if (userId == null || userId.isEmpty()) {
            Log.w(TAG, "User ID not found");
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
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "WebSocket disconnected");
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e(TAG, "WebSocket error: " + throwable.getMessage());
    }

    @Override
    public void onMessageReceived(String messageJson) {
        requireActivity().runOnUiThread(() -> {
            try {
                ChatMessageDto dto = gson.fromJson(messageJson, ChatMessageDto.class);

                if (dto.getSenderId().equals(parentUserId)) {
                    ChatMessage message = new ChatMessage(
                            dto.getId(),
                            dto.getContent(),
                            formatTime(dto.getCreatedAt()),
                            false
                    );
                    message.setSenderName(dto.getSenderName());

                    adapter.addMessage(message);
                    scrollToBottom();

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
        requireActivity().runOnUiThread(() -> {
            try {
                ChatMessageDto dto = gson.fromJson(messageJson, ChatMessageDto.class);
                adapter.updateMessageStatus("temp_", dto.getId(), ChatMessage.STATUS_SENT);

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
        // TODO: Show typing indicator
    }

    @Override
    public void onReadReceipt(String roomId) {
        if (roomId.equals(chatRoomId)) {
            requireActivity().runOnUiThread(() -> adapter.markAllAsRead());
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
