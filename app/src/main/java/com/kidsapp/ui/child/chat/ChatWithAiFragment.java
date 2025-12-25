package com.kidsapp.ui.child.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.request.AiChatRequest;
import com.kidsapp.data.response.AiChatResponse;
import com.kidsapp.databinding.FragmentChatAiTabBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment chat với AI trợ lý học toán
 * Dùng trong tab của ChatHubFragment
 */
public class ChatWithAiFragment extends Fragment {

    private FragmentChatAiTabBinding binding;
    private AiChatAdapter adapter;
    private ApiService apiService;
    private String conversationId;
    
    // Mode: CHILD hoặc PARENT
    private String chatMode = "CHILD";

    public static ChatWithAiFragment newInstance(String mode) {
        ChatWithAiFragment fragment = new ChatWithAiFragment();
        Bundle args = new Bundle();
        args.putString("mode", mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChatAiTabBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        loadArguments();
        setupApi();
        setupViews();
        addWelcomeMessage();
    }

    private void loadArguments() {
        if (getArguments() != null) {
            chatMode = getArguments().getString("mode", "CHILD");
        }
        conversationId = UUID.randomUUID().toString();
    }

    private void setupApi() {
        SharedPref sharedPref = new SharedPref(requireContext());
         // Lấy instance Retrofit để gọi API
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();
    }

    private void setupViews() {
        // RecyclerView
        adapter = new AiChatAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        binding.rvMessages.setLayoutManager(layoutManager);
        binding.rvMessages.setAdapter(adapter);

        // Send button
        binding.btnSend.setOnClickListener(v -> sendMessage());

        // Enter key to send
        binding.edtMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void addWelcomeMessage() {
        String welcomeText;
        if ("PARENT".equals(chatMode)) {
            welcomeText = "Xin chào! 👋 Tôi là trợ lý AI, sẵn sàng tư vấn cho bạn về cách dạy con học toán và các vấn đề tâm lý giáo dục. Hãy hỏi tôi bất cứ điều gì!";
        } else {
            welcomeText = "Xin chào bạn! 🌟 Mình là trợ lý AI, sẵn sàng giúp bạn học toán. Hãy hỏi mình bất cứ điều gì nhé! 🎉";
        }
        
        AiChatMessage welcome = new AiChatMessage(
                UUID.randomUUID().toString(),
                welcomeText,
                getCurrentTime(),
                false
        );
        adapter.addMessage(welcome);
    }

    private void sendMessage() {
        String content = binding.edtMessage.getText().toString().trim();
        if (TextUtils.isEmpty(content)) return;

        // Thêm tin nhắn user vào UI
        AiChatMessage userMessage = new AiChatMessage(
                UUID.randomUUID().toString(),
                content,
                getCurrentTime(),
                true
        );
        adapter.addMessage(userMessage);
        binding.edtMessage.setText("");
        scrollToBottom();

        // Hiển thị typing indicator
        showTyping(true);

        // Gọi API
        AiChatRequest request = new AiChatRequest(content, conversationId, chatMode);
        
        apiService.sendChatMessage(request).enqueue(new Callback<AiChatResponse>() {
            @Override
            public void onResponse(@NonNull Call<AiChatResponse> call, 
                                   @NonNull Response<AiChatResponse> response) {
                showTyping(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AiChatResponse aiResponse = response.body();
                    
                    if (aiResponse.isSuccess() && aiResponse.getData() != null) {
                        // Thêm tin nhắn AI vào UI
                        AiChatMessage aiMessage = new AiChatMessage(
                                UUID.randomUUID().toString(),
                                aiResponse.getData().getMessage(),
                                getCurrentTime(),
                                false
                        );
                        adapter.addMessage(aiMessage);
                        scrollToBottom();
                        
                        // Cập nhật conversationId nếu có
                        if (aiResponse.getData().getConversationId() != null) {
                            conversationId = aiResponse.getData().getConversationId();
                        }
                    } else {
                        showError("Có lỗi xảy ra. Vui lòng thử lại!");
                    }
                } else {
                    showError("Không thể kết nối với AI. Vui lòng thử lại!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AiChatResponse> call, @NonNull Throwable t) {
                showTyping(false);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void showTyping(boolean show) {
        if (binding != null) {
            binding.layoutTyping.setVisibility(show ? View.VISIBLE : View.GONE);
            if (show) scrollToBottom();
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
        
        // Thêm tin nhắn lỗi từ AI
        AiChatMessage errorMessage = new AiChatMessage(
                UUID.randomUUID().toString(),
                "Xin lỗi, mình gặp sự cố. Bạn thử hỏi lại nhé! 😊",
                getCurrentTime(),
                false
        );
        adapter.addMessage(errorMessage);
        scrollToBottom();
    }

    private void scrollToBottom() {
        if (binding != null && adapter.getItemCount() > 0) {
            binding.rvMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
        }
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
