package com.kidsapp.ui.child.chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
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

import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.response.ChildSearchResponse;
import com.kidsapp.data.websocket.ChatRoomDto;
import com.kidsapp.databinding.FragmentChatListBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment hiển thị danh sách chat (dùng cho cả tab Phụ huynh và Bạn bè)
 * Tab Bạn bè có thêm thanh tìm kiếm để tìm bạn mới từ database
 */
public class ChatListFragment extends Fragment implements ConversationAdapter.OnConversationClickListener {

    private static final String TAG = "ChatListFragment";
    private static final String ARG_TYPE = "type";
    public static final int TYPE_PARENT = 0;
    public static final int TYPE_SIBLINGS = 1; // Đổi từ TYPE_FRIENDS sang TYPE_SIBLINGS

    private FragmentChatListBinding binding;
    private ConversationAdapter adapter;
    private int type;
    
    // API
    private ApiService apiService;
    private SharedPref sharedPref;
    
    // Debounce search
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DELAY = 500;

    public static ChatListFragment newInstance(int type) {
        ChatListFragment fragment = new ChatListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChatListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        type = getArguments() != null ? getArguments().getInt(ARG_TYPE, TYPE_PARENT) : TYPE_PARENT;
        sharedPref = new SharedPref(requireContext());
        
        // Sử dụng RetrofitClient với AuthInterceptor
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();
        
        setupRecyclerView();
        setupSearch();
        loadConversations();
    }

    private void setupRecyclerView() {
        adapter = new ConversationAdapter(this);
        binding.rvChats.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChats.setAdapter(adapter);
    }

    private void setupSearch() {
        // Ẩn thanh tìm kiếm - không cần tìm kiếm anh chị em
        binding.searchContainer.setVisibility(View.GONE);
    }

    private void searchFriends(String keyword) {
        if (keyword.isEmpty()) {
            // Nếu không có keyword, hiện lại danh sách chat cũ
            loadConversations();
            return;
        }
        
        showLoading(true);
        
        String currentChildId = getCurrentChildId();
        Log.d(TAG, "Searching friends: " + keyword);
        
        apiService.searchChildren(currentChildId, keyword)
                .enqueue(new Callback<ApiService.ApiResponseWrapper<List<ChildSearchResponse>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<List<ChildSearchResponse>>> call,
                                           @NonNull Response<ApiService.ApiResponseWrapper<List<ChildSearchResponse>>> response) {
                        showLoading(false);
                        
                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                            List<ChildSearchResponse> friends = response.body().data;
                            Log.d(TAG, "Found " + friends.size() + " friends");
                            
                            // Convert to Conversation list
                            List<Conversation> conversations = new ArrayList<>();
                            for (ChildSearchResponse friend : friends) {
                                conversations.add(new Conversation(
                                        friend.getId(),
                                        friend.getDisplayName(),
                                        friend.getAvatarUrl(),
                                        "Nhấn để bắt đầu chat",
                                        "",
                                        0,
                                        friend.isOnline(),
                                        Conversation.TYPE_FRIEND
                                ));
                            }
                            adapter.setConversations(conversations);
                        } else {
                            adapter.setConversations(new ArrayList<>());
                        }
                        updateEmptyState();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<List<ChildSearchResponse>>> call,
                                          @NonNull Throwable t) {
                        showLoading(false);
                        Log.e(TAG, "Search error: " + t.getMessage());
                        Toast.makeText(requireContext(), "Không thể tìm kiếm", Toast.LENGTH_SHORT).show();
                        updateEmptyState();
                    }
                });
    }

    private String getCurrentChildId() {
        String childId = sharedPref.getChildId();
        if (childId == null || childId.isEmpty()) {
            childId = sharedPref.getUserId();
        }
        if (childId == null || childId.isEmpty()) {
            Log.w(TAG, "Child ID not found in SharedPref");
            return "";
        }
        return childId;
    }

    private void showLoading(boolean show) {
        if (binding == null) return;
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void loadConversations() {
        showLoading(true);
        
        if (type == TYPE_PARENT) {
            // Load danh sách phụ huynh từ API
            loadParents();
        } else {
            // Load danh sách anh chị em từ API
            loadSiblings();
        }
    }

    /**
     * Load danh sách phụ huynh của child
     */
    private void loadParents() {
        Log.d(TAG, "=== LOADING PARENTS ===");
        Log.d(TAG, "Calling API: getMyParents()");
        
        apiService.getMyParents()
                .enqueue(new Callback<ApiService.ApiResponseWrapper<List<ApiService.ParentInfoResponse>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<List<ApiService.ParentInfoResponse>>> call,
                                           @NonNull Response<ApiService.ApiResponseWrapper<List<ApiService.ParentInfoResponse>>> response) {
                        showLoading(false);
                        
                        Log.d(TAG, "=== API RESPONSE ===");
                        Log.d(TAG, "Response code: " + response.code());
                        Log.d(TAG, "Request URL: " + call.request().url());
                        
                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                            List<ApiService.ParentInfoResponse> parents = response.body().data;
                            Log.d(TAG, "Parents count: " + parents.size());
                            
                            List<Conversation> conversations = new ArrayList<>();
                            
                            for (ApiService.ParentInfoResponse parent : parents) {
                                Log.d(TAG, "Parent: " + parent.name + ", userId: " + parent.userId);
                                // Sử dụng userId để chat
                                String chatUserId = parent.userId != null ? parent.userId : parent.id;
                                conversations.add(new Conversation(
                                        chatUserId,
                                        parent.name != null ? parent.name : "Phụ huynh",
                                        parent.avatarUrl,
                                        "Nhấn để bắt đầu chat",
                                        "",
                                        0,
                                        parent.isOnline,
                                        Conversation.TYPE_PARENT
                                ));
                            }
                            
                            if (conversations.isEmpty()) {
                                showEmptyMessage("Chưa có phụ huynh được liên kết");
                            }
                            adapter.setConversations(conversations);
                        } else {
                            Log.e(TAG, "Load parents failed: " + response.code());
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                                Log.e(TAG, "Error body: " + errorBody);
                            } catch (Exception e) {
                                Log.e(TAG, "Cannot read error body");
                            }
                            showEmptyMessage("Không thể tải danh sách phụ huynh");
                            adapter.setConversations(new ArrayList<>());
                        }
                        updateEmptyState();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<List<ApiService.ParentInfoResponse>>> call,
                                          @NonNull Throwable t) {
                        showLoading(false);
                        Log.e(TAG, "Load parents error: " + t.getMessage(), t);
                        Log.e(TAG, "Request URL: " + call.request().url());
                        showEmptyMessage("Lỗi kết nối: " + t.getMessage());
                        adapter.setConversations(new ArrayList<>());
                        updateEmptyState();
                    }
                });
    }

    /**
     * Load danh sách anh chị em (các con cùng phụ huynh)
     */
    private void loadSiblings() {
        Log.d(TAG, "=== LOADING SIBLINGS ===");
        Log.d(TAG, "Calling API: getMySiblings()");
        
        apiService.getMySiblings()
                .enqueue(new Callback<ApiService.ApiResponseWrapper<List<ChildSearchResponse>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<List<ChildSearchResponse>>> call,
                                           @NonNull Response<ApiService.ApiResponseWrapper<List<ChildSearchResponse>>> response) {
                        showLoading(false);
                        
                        Log.d(TAG, "=== API RESPONSE ===");
                        Log.d(TAG, "Response code: " + response.code());
                        
                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                            List<ChildSearchResponse> siblings = response.body().data;
                            Log.d(TAG, "Siblings count: " + siblings.size());
                            
                            List<Conversation> conversations = new ArrayList<>();
                            
                            for (ChildSearchResponse sibling : siblings) {
                                Log.d(TAG, "Sibling: " + sibling.getDisplayName() + ", id: " + sibling.getId());
                                conversations.add(new Conversation(
                                        sibling.getId(), // userId để chat
                                        sibling.getDisplayName(),
                                        sibling.getAvatarUrl(),
                                        "Nhấn để bắt đầu chat",
                                        "",
                                        0,
                                        sibling.isOnline(),
                                        Conversation.TYPE_FRIEND // Vẫn dùng TYPE_FRIEND cho anh chị em
                                ));
                            }
                            
                            if (conversations.isEmpty()) {
                                showEmptyMessage("Chưa có anh chị em trong gia đình");
                            }
                            adapter.setConversations(conversations);
                        } else {
                            Log.e(TAG, "Load siblings failed: " + response.code());
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                                Log.e(TAG, "Error body: " + errorBody);
                            } catch (Exception e) {
                                Log.e(TAG, "Cannot read error body");
                            }
                            showEmptyMessage("Không thể tải danh sách anh chị em");
                            adapter.setConversations(new ArrayList<>());
                        }
                        updateEmptyState();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<List<ChildSearchResponse>>> call,
                                          @NonNull Throwable t) {
                        showLoading(false);
                        Log.e(TAG, "Load siblings error: " + t.getMessage(), t);
                        showEmptyMessage("Lỗi kết nối: " + t.getMessage());
                        adapter.setConversations(new ArrayList<>());
                        updateEmptyState();
                    }
                });
    }

    /**
     * Load danh sách chat rooms với bạn bè
     */
    private void loadFriendChatRooms() {
        String currentUserId = sharedPref.getUserId();
        if (currentUserId == null || currentUserId.isEmpty()) {
            showLoading(false);
            showEmptyMessage("Vui lòng đăng nhập");
            updateEmptyState();
            return;
        }
        
        apiService.getChatRoomsByType(currentUserId, "CHILD_CHILD")
                .enqueue(new Callback<ApiService.ApiResponseWrapper<List<ChatRoomDto>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<List<ChatRoomDto>>> call,
                                           @NonNull Response<ApiService.ApiResponseWrapper<List<ChatRoomDto>>> response) {
                        showLoading(false);
                        
                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                            List<ChatRoomDto> rooms = response.body().data;
                            List<Conversation> conversations = new ArrayList<>();
                            
                            for (ChatRoomDto room : rooms) {
                                conversations.add(new Conversation(
                                        room.getOtherUserId(),
                                        room.getOtherUserName(),
                                        room.getOtherUserAvatar(),
                                        room.getLastMessage() != null ? room.getLastMessage() : "Nhấn để bắt đầu chat",
                                        formatTime(room.getLastMessageAt()),
                                        room.getUnreadCount(),
                                        room.isOnline(),
                                        Conversation.TYPE_FRIEND
                                ));
                            }
                            
                            adapter.setConversations(conversations);
                        } else {
                            Log.e(TAG, "Load friend rooms failed: " + response.code());
                            adapter.setConversations(new ArrayList<>());
                        }
                        updateEmptyState();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<List<ChatRoomDto>>> call,
                                          @NonNull Throwable t) {
                        showLoading(false);
                        Log.e(TAG, "Load friend rooms error: " + t.getMessage());
                        adapter.setConversations(new ArrayList<>());
                        updateEmptyState();
                    }
                });
    }

    private void showEmptyMessage(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private String formatTime(String isoTime) {
        if (isoTime == null || isoTime.isEmpty()) return "";
        try {
            java.text.SimpleDateFormat isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
            java.util.Date date = isoFormat.parse(isoTime);
            return new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(date);
        } catch (Exception e) {
            return "";
        }
    }

    private void updateEmptyState() {
        if (binding == null) return;
        
        boolean isEmpty = adapter.isEmpty();
        binding.layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.rvChats.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        if (isEmpty) {
            if (type == TYPE_PARENT) {
                binding.txtEmptyTitle.setText("Chưa có phụ huynh");
                binding.txtEmptyMessage.setText("Liên kết với phụ huynh để bắt đầu chat");
            } else {
                binding.txtEmptyTitle.setText("Chưa có anh chị em");
                binding.txtEmptyMessage.setText("Chưa có anh chị em trong gia đình");
            }
        }
    }

    @Override
    public void onConversationClick(Conversation conversation) {
        // Mở màn hình chat
        Bundle args = new Bundle();
        args.putString("chat_id", ""); // Sẽ được tạo khi gửi tin nhắn đầu tiên
        args.putString("receiver_id", conversation.getId()); // User ID của người nhận
        args.putString("chat_name", conversation.getName());
        args.putInt("chat_type", conversation.getType());

        ChatRoomFragment fragment = new ChatRoomFragment();
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.childHomeHost, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        binding = null;
    }
}
