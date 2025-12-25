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
import com.kidsapp.databinding.FragmentFindFriendBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Fragment tìm bạn mới để chat
 */
public class FindFriendFragment extends Fragment implements FriendSearchAdapter.OnFriendClickListener {

    private static final String TAG = "FindFriendFragment";
    private FragmentFindFriendBinding binding;
    private FriendSearchAdapter adapter;
    private ApiService apiService;
    private SharedPref sharedPref;
    
    // Debounce search
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DELAY = 500; // 500ms delay

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFindFriendBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        sharedPref = new SharedPref(requireContext());
        setupRetrofit();
        setupViews();
        loadFriends("");
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    private void setupViews() {
        // Adapter
        adapter = new FriendSearchAdapter(this);
        binding.rvFriends.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFriends.setAdapter(adapter);

        // Search với debounce
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
                searchRunnable = () -> loadFriends(s.toString());
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void loadFriends(String keyword) {
        showLoading(true);
        
        // Lấy currentChildId từ SharedPref
        String currentChildId = sharedPref.getChildId();
        if (currentChildId == null || currentChildId.isEmpty()) {
            // Fallback: dùng userId nếu không có childId
            currentChildId = sharedPref.getUserId();
        }
        if (currentChildId == null || currentChildId.isEmpty()) {
            // Fallback: dùng mock ID nếu chưa login
            currentChildId = "00000000-0000-0000-0000-000000000000";
        }
        
        Log.d(TAG, "Searching friends with keyword: " + keyword + ", currentChildId: " + currentChildId);
        
        apiService.searchChildren(currentChildId, keyword.isEmpty() ? null : keyword)
                .enqueue(new Callback<ApiService.ApiResponseWrapper<List<ChildSearchResponse>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<List<ChildSearchResponse>>> call,
                                           @NonNull Response<ApiService.ApiResponseWrapper<List<ChildSearchResponse>>> response) {
                        showLoading(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            List<ChildSearchResponse> friends = response.body().data;
                            Log.d(TAG, "Found " + (friends != null ? friends.size() : 0) + " friends");
                            adapter.setFriends(friends);
                        } else {
                            Log.e(TAG, "API error: " + response.code());
                            adapter.setFriends(null);
                        }
                        updateEmptyState();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<List<ChildSearchResponse>>> call,
                                          @NonNull Throwable t) {
                        showLoading(false);
                        Log.e(TAG, "Network error: " + t.getMessage());
                        Toast.makeText(requireContext(), "Không thể kết nối server", Toast.LENGTH_SHORT).show();
                        adapter.setFriends(null);
                        updateEmptyState();
                    }
                });
    }

    private void showLoading(boolean show) {
        if (binding == null) return;
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.rvFriends.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateEmptyState() {
        if (binding == null) return;
        boolean isEmpty = adapter.isEmpty();
        binding.layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.rvFriends.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onFriendClick(ChildSearchResponse friend) {
        // Mở chat room với bạn được chọn
        Bundle args = new Bundle();
        args.putString("chat_id", friend.getId());
        args.putString("chat_name", friend.getDisplayName());
        args.putInt("chat_type", Conversation.TYPE_FRIEND);

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
