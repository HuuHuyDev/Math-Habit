package com.kidsapp.ui.parent.chat;

import android.os.Bundle;
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

import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.databinding.FragmentParentChatListBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment hiển thị danh sách chat với các con
 * Load danh sách con từ API và hiển thị real-time với WebSocket
 */
public class ParentChatListFragment extends Fragment implements ChildChatAdapter.OnChildChatClickListener {

    private static final String TAG = "ParentChatListFragment";

    private FragmentParentChatListBinding binding;
    private ChildChatAdapter adapter;
    private SharedPref sharedPref;
    private ApiService apiService;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentParentChatListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPref = new SharedPref(requireContext());
        currentUserId = getCurrentUserId();

        // Sử dụng RetrofitClient với AuthInterceptor
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();
        
        setupRecyclerView();
        loadChildren();
    }

    private void setupRecyclerView() {
        adapter = new ChildChatAdapter(this);
        binding.rvChildren.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChildren.setAdapter(adapter);
    }

    /**
     * Load danh sách con từ API
     */
    private void loadChildren() {
        showLoading(true);

        // Gọi API lấy danh sách con của phụ huynh
        apiService.getParentChildren()
                .enqueue(new Callback<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>> call,
                                           @NonNull Response<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>> response) {
                        showLoading(false);

                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                            List<ApiService.ChildResponse> children = response.body().data;
                            List<ChildChatItem> chatItems = convertToChildChatItems(children);
                            adapter.setChildren(chatItems);
                            
                            if (chatItems.isEmpty()) {
                                showEmptyMessage("Chưa có con nào được liên kết");
                            }
                        } else {
                            Log.e(TAG, "Load children failed: " + response.code());
                            showEmptyMessage("Không thể tải danh sách con");
                        }
                        updateEmptyState();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>> call,
                                          @NonNull Throwable t) {
                        showLoading(false);
                        Log.e(TAG, "Load children error: " + t.getMessage());
                        showEmptyMessage("Lỗi kết nối: " + t.getMessage());
                        updateEmptyState();
                    }
                });
    }

    /**
     * Convert ChildResponse to ChildChatItem
     */
    private List<ChildChatItem> convertToChildChatItems(List<ApiService.ChildResponse> children) {
        List<ChildChatItem> chatItems = new ArrayList<>();

        for (ApiService.ChildResponse child : children) {
            // Sử dụng userId để chat (không phải childId)
            String chatUserId = child.userId != null ? child.userId : child.id;
            String displayName = child.nickname != null && !child.nickname.isEmpty() 
                    ? child.nickname 
                    : (child.name != null ? child.name : "Con");
            
            chatItems.add(new ChildChatItem(
                    chatUserId,
                    displayName,
                    child.avatarUrl,
                    "Nhấn để bắt đầu chat",
                    "",
                    0,
                    child.isOnline,
                    child.grade != null ? child.grade : 0
            ));
        }

        return chatItems;
    }

    private void showLoading(boolean show) {
        if (binding == null) return;
        // TODO: Add progress bar to layout if needed
    }

    private void showEmptyMessage(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEmptyState() {
        if (binding == null) return;

        boolean isEmpty = adapter.isEmpty();
        binding.layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.rvChildren.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private String getCurrentUserId() {
        String userId = sharedPref.getUserId();
        if (userId == null || userId.isEmpty()) {
            Log.w(TAG, "User ID not found in SharedPref");
            return "";
        }
        return userId;
    }

    @Override
    public void onChildChatClick(ChildChatItem child) {
        // Mở màn hình chat với con
        Bundle args = new Bundle();
        args.putString("child_id", child.getId());
        args.putString("child_name", child.getName());

        try {
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_chatHub_to_chatRoom, args);
        } catch (Exception e) {
            Log.e(TAG, "Navigation error: " + e.getMessage());
            // Fallback: dùng FragmentManager
            ParentChatRoomFragment fragment = new ParentChatRoomFragment();
            fragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.navHostFragment, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh danh sách khi quay lại
        loadChildren();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
