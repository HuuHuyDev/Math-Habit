package com.kidsapp.ui.parent.chat;

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
import com.kidsapp.databinding.FragmentParentChatAiBinding;
import com.kidsapp.ui.child.chat.AiChatAdapter;
import com.kidsapp.ui.child.chat.AiChatMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment chat với AI cho Phụ huynh
 * Mode: PARENT - AI sẽ tư vấn về cách dạy con, tâm lý giáo dục
 */
public class ParentChatWithAiFragment extends Fragment {

    private FragmentParentChatAiBinding binding;
    private AiChatAdapter adapter;
    private ApiService apiService;
    private String conversationId;

    public static ParentChatWithAiFragment newInstance() {
        return new ParentChatWithAiFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentParentChatAiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        conversationId = UUID.randomUUID().toString();
        setupApi();
        setupViews();
        addWelcomeMessage();
    }

    private void setupApi() {
        SharedPref sharedPref = new SharedPref(requireContext());
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
        String welcomeText = "Xin chào! 👋 Tôi là trợ lý AI chuyên tư vấn về giáo dục trẻ em.\n\n" +
                "Tôi có thể giúp bạn:\n" +
                "• Cách động viên con học tập\n" +
                "• Xử lý khi con không chịu làm bài\n" +
                "• Tâm lý trẻ em trong học tập\n" +
                "• Phương pháp dạy con hiệu quả\n\n" +
                "Hãy hỏi tôi bất cứ điều gì! 😊";
        
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

        // Gọi API với mode PARENT
        AiChatRequest request = new AiChatRequest(content, conversationId, "PARENT");
        
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
                "Xin lỗi, tôi gặp sự cố. Bạn thử hỏi lại nhé! 😊",
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
