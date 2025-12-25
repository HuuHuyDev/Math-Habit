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
import com.kidsapp.data.api.ApiConfig;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.response.ChildSearchResponse;
import com.kidsapp.databinding.FragmentChatListBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Fragment hiển thị danh sách chat (dùng cho cả tab Phụ huynh và Bạn bè)
 * Tab Bạn bè có thêm thanh tìm kiếm để tìm bạn mới từ database
 */
public class ChatListFragment extends Fragment implements ConversationAdapter.OnConversationClickListener {

    private static final String TAG = "ChatListFragment";
    private static final String ARG_TYPE = "type";
    public static final int TYPE_PARENT = 0;
    public static final int TYPE_FRIENDS = 1;

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
        
        setupRetrofit();
        setupRecyclerView();
        setupSearch();
        loadConversations();
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    private void setupRecyclerView() {
        adapter = new ConversationAdapter(this);
        binding.rvChats.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChats.setAdapter(adapter);
    }

    private void setupSearch() {
        // Chỉ hiện thanh tìm kiếm cho tab Bạn bè
        if (type == TYPE_FRIENDS) {
            binding.searchContainer.setVisibility(View.VISIBLE);
            
            binding.edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Cancel previous search
                    if (searchRunnable != null) {
                        searchHandler.removeCallbacks(searchRunnable);
                    }
                    
                    // Schedule new search
                    searchRunnable = () -> searchFriends(s.toString());
                    searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
                }
                
                @Override
                public void afterTextChanged(Editable s) {}
            });
        } else {
            binding.searchContainer.setVisibility(View.GONE);
        }
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
            childId = "00000000-0000-0000-0000-000000000000";
        }
        return childId;
    }

    private void showLoading(boolean show) {
        if (binding == null) return;
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void loadConversations() {
        showLoading(true);
        
        String currentUserId = getCurrentChildId();
        
        // Load từ API
        String roomType = (type == TYPE_PARENT) ? "PARENT_CHILD" : "CHILD_CHILD";
        
        apiService.getChatRoomsByType(currentUserId, roomType)
                .enqueue(new Callback<ApiService.ApiResponseWrapper<List<com.kidsapp.data.websocket.ChatRoomDto>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<List<com.kidsapp.data.websocket.ChatRoomDto>>> call,
                                           @NonNull Response<ApiService.ApiResponseWrapper<List<com.kidsapp.data.websocket.ChatRoomDto>>> response) {
                        showLoading(false);
                        
                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                            List<com.kidsapp.data.websocket.ChatRoomDto> rooms = response.body().data;
                            List<Conversation> conversations = new ArrayList<>();
                            
                            for (com.kidsapp.data.websocket.ChatRoomDto room : rooms) {
                                conversations.add(new Conversation(
                                        room.getOtherUserId(),
                                        room.getOtherUserName(),
                                        room.getOtherUserAvatar(),
                                        room.getLastMessage() != null ? room.getLastMessage() : "Nhấn để bắt đầu chat",
                                        formatTime(room.getLastMessageAt()),
                                        room.getUnreadCount(),
                                        room.isOnline(),
                                        type == TYPE_PARENT ? Conversation.TYPE_PARENT : Conversation.TYPE_FRIEND
                                ));
                            }
                            
                            if (conversations.isEmpty()) {
                                // Nếu không có dữ liệu từ API, hiện mock data để test
                                loadMockConversations();
                            } else {
                                adapter.setConversations(conversations);
                            }
                        } else {
                            // Fallback to mock data
                            loadMockConversations();
                        }
                        updateEmptyState();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<List<com.kidsapp.data.websocket.ChatRoomDto>>> call,
                                          @NonNull Throwable t) {
                        showLoading(false);
                        Log.e(TAG, "Load conversations error: " + t.getMessage());
                        // Fallback to mock data
                        loadMockConversations();
                        updateEmptyState();
                    }
                });
    }

    private void loadMockConversations() {
        // Mock data với UUID giả để test UI (không gọi API)
        List<Conversation> conversations = new ArrayList<>();

        if (type == TYPE_PARENT) {
            // Dùng UUID format để tránh lỗi khi gọi API
            conversations.add(new Conversation(
                    "00000000-0000-0000-0000-000000000001", "Bố", null,
                    "Con học giỏi lắm! 💪", "10:30",
                    2, true, Conversation.TYPE_PARENT
            ));
            conversations.add(new Conversation(
                    "00000000-0000-0000-0000-000000000002", "Mẹ", null,
                    "Nhớ ăn cơm đúng giờ nhé con", "Hôm qua",
                    0, false, Conversation.TYPE_PARENT
            ));
        } else {
            conversations.add(new Conversation(
                    "00000000-0000-0000-0000-000000000003", "Minh Anh", null,
                    "Đấu một trận không? 🎮", "09:15",
                    3, true, Conversation.TYPE_FRIEND
            ));
            conversations.add(new Conversation(
                    "00000000-0000-0000-0000-000000000004", "Bảo Ngọc", null,
                    "Bài toán này khó quá!", "Hôm qua",
                    0, true, Conversation.TYPE_FRIEND
            ));
        }

        adapter.setConversations(conversations);
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
                binding.txtEmptyTitle.setText("Chưa có tin nhắn từ phụ huynh");
                binding.txtEmptyMessage.setText("Tin nhắn từ bố mẹ sẽ hiển thị ở đây");
            } else {
                binding.txtEmptyTitle.setText("Không tìm thấy bạn bè");
                binding.txtEmptyMessage.setText("Thử tìm kiếm với từ khóa khác");
            }
        }
    }

    @Override
    public void onConversationClick(Conversation conversation) {
        // Mở màn hình chat
        Bundle args = new Bundle();
        args.putString("chat_id", conversation.getId());
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
