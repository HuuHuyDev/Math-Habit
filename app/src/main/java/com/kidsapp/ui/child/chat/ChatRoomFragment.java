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
import com.kidsapp.data.websocket.WebSocketManager;
import com.kidsapp.databinding.FragmentChatRoomBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment phòng chat realtime với WebSocket
 */
public class ChatRoomFragment extends Fragment implements WebSocketManager.WebSocketListener {

    private static final String TAG = "ChatRoomFragment";
    
    private FragmentChatRoomBinding binding;
    private ChatAdapter adapter;
    private WebSocketManager webSocketManager;
    private SharedPref sharedPref;
    private Gson gson;
    private ApiService apiService;
    
    // Chat info
    private String chatId;        // Room ID (nếu có)
    private String receiverId;    // User ID của người nhận
    private String chatName;
    private int chatType;
    private String currentUserId;
    
    // Typing indicator
    private Handler typingHandler = new Handler(Looper.getMainLooper());
    private Runnable hideTypingRunnable;
    private static final long TYPING_TIMEOUT = 3000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChatRoomBinding.inflate(inflater, container, false);
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
        loadMessages();
    }

    private void loadArguments() {
        Bundle args = getArguments();
        if (args != null) {
            chatId = args.getString("chat_id", "");
            receiverId = args.getString("receiver_id", chatId); // Fallback to chatId
            chatName = args.getString("chat_name", "Chat");
            chatType = args.getInt("chat_type", Conversation.TYPE_FRIEND);
        }
    }

    private void setupViews() {
        // Header
        binding.txtChatName.setText(chatName);
        updateConnectionStatus(false);
        
        // Avatar
        if (chatType == Conversation.TYPE_PARENT) {
            binding.imgAvatar.setImageResource(R.drawable.ic_parent_avatar);
        } else {
            binding.imgAvatar.setImageResource(R.drawable.ic_child_avatar);
        }

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
                    webSocketManager.sendTyping(receiverId);
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
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

    private void sendMessage() {
        String content = binding.edtMessage.getText().toString().trim();
        if (TextUtils.isEmpty(content)) return;

        // Tạo tin nhắn local để hiển thị ngay
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

        // Gửi qua WebSocket
        if (webSocketManager.isConnected()) {
            webSocketManager.sendMessage(receiverId, content);
        } else {
            // Fallback: Gửi qua REST API
            sendMessageViaRest(content, localMessage);
        }
    }

    private void sendMessageViaRest(String content, ChatMessage localMessage) {
        // TODO: Implement REST fallback
        Log.w(TAG, "WebSocket not connected, using REST fallback");
        localMessage.setStatus(ChatMessage.STATUS_SENT);
        adapter.notifyDataSetChanged();
    }

    private void loadMessages() {
        // Load tin nhắn cũ từ API
        // Nếu chưa có chatId, cần tạo room trước khi load messages
        if (chatId == null || chatId.isEmpty()) {
            // Tạo hoặc lấy chat room
            createOrGetChatRoom();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        
        apiService.getChatMessages(chatId, currentUserId, 0, 50)
                .enqueue(new Callback<ApiService.ApiResponseWrapper<List<ChatMessageDto>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<List<ChatMessageDto>>> call,
                                           @NonNull Response<ApiService.ApiResponseWrapper<List<ChatMessageDto>>> response) {
                        binding.progressBar.setVisibility(View.GONE);
                        
                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                            List<ChatMessage> messages = convertToMessages(response.body().data);
                            adapter.setMessages(messages);
                            scrollToBottom();
                            
                            // Đánh dấu đã đọc
                            if (webSocketManager.isConnected()) {
                                webSocketManager.markAsRead(chatId);
                            }
                        } else {
                            Log.e(TAG, "Load messages failed: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<List<ChatMessageDto>>> call,
                                          @NonNull Throwable t) {
                        binding.progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "Load messages error: " + t.getMessage());
                    }
                });
    }

    /**
     * Tạo hoặc lấy chat room với người nhận
     */
    private void createOrGetChatRoom() {
        String roomType = (chatType == Conversation.TYPE_PARENT) ? "PARENT_CHILD" : "CHILD_CHILD";
        
        apiService.createOrGetChatRoom(currentUserId, receiverId, roomType)
                .enqueue(new Callback<ApiService.ApiResponseWrapper<com.kidsapp.data.websocket.ChatRoomDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<com.kidsapp.data.websocket.ChatRoomDto>> call,
                                           @NonNull Response<ApiService.ApiResponseWrapper<com.kidsapp.data.websocket.ChatRoomDto>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                            chatId = response.body().data.getId();
                            Log.d(TAG, "Chat room created/found: " + chatId);
                            // Load messages sau khi có room
                            loadMessagesFromRoom();
                        } else {
                            Log.e(TAG, "Failed to create/get chat room: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<com.kidsapp.data.websocket.ChatRoomDto>> call,
                                          @NonNull Throwable t) {
                        Log.e(TAG, "Error creating chat room: " + t.getMessage());
                    }
                });
    }

    /**
     * Load messages sau khi đã có room ID
     */
    private void loadMessagesFromRoom() {
        if (chatId == null || chatId.isEmpty()) return;
        
        binding.progressBar.setVisibility(View.VISIBLE);
        
        apiService.getChatMessages(chatId, currentUserId, 0, 50)
                .enqueue(new Callback<ApiService.ApiResponseWrapper<List<ChatMessageDto>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<List<ChatMessageDto>>> call,
                                           @NonNull Response<ApiService.ApiResponseWrapper<List<ChatMessageDto>>> response) {
                        binding.progressBar.setVisibility(View.GONE);
                        
                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                            List<ChatMessage> messages = convertToMessages(response.body().data);
                            adapter.setMessages(messages);
                            scrollToBottom();
                            
                            if (webSocketManager.isConnected()) {
                                webSocketManager.markAsRead(chatId);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<List<ChatMessageDto>>> call,
                                          @NonNull Throwable t) {
                        binding.progressBar.setVisibility(View.GONE);
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
            binding.txtStatus.setTextColor(getResources().getColor(R.color.gray_500, null));
        }
    }

    private void showTypingIndicator() {
        if (binding == null) return;
        
        binding.layoutTyping.setVisibility(View.VISIBLE);
        
        // Ẩn sau timeout
        if (hideTypingRunnable != null) {
            typingHandler.removeCallbacks(hideTypingRunnable);
        }
        hideTypingRunnable = () -> {
            if (binding != null) {
                binding.layoutTyping.setVisibility(View.GONE);
            }
        };
        typingHandler.postDelayed(hideTypingRunnable, TYPING_TIMEOUT);
    }

    private String getCurrentUserId() {
        String userId = sharedPref.getUserId();
        if (userId == null || userId.isEmpty()) {
            userId = sharedPref.getChildId();
        }
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
            // Parse ISO time and format to HH:mm
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
                
                // Chỉ hiển thị tin nhắn từ người đang chat
                if (dto.getSenderId().equals(receiverId)) {
                    ChatMessage message = new ChatMessage(
                            dto.getId(),
                            dto.getContent(),
                            formatTime(dto.getCreatedAt()),
                            false
                    );
                    message.setSenderName(dto.getSenderName());
                    
                    adapter.addMessage(message);
                    scrollToBottom();
                    
                    // Đánh dấu đã đọc
                    if (chatId != null && !chatId.isEmpty()) {
                        webSocketManager.markAsRead(chatId);
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
                if (chatId == null || chatId.isEmpty()) {
                    chatId = dto.getRoomId();
                }
            } catch (Exception e) {
                Log.e(TAG, "Parse sent confirmation error: " + e.getMessage());
            }
        });
    }

    @Override
    public void onTyping(String senderId) {
        if (senderId.equals(receiverId)) {
            requireActivity().runOnUiThread(this::showTypingIndicator);
        }
    }

    @Override
    public void onReadReceipt(String roomId) {
        if (roomId.equals(chatId)) {
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
