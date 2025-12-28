package com.kidsapp.ui.child.challenge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.R;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.Child;
import com.kidsapp.data.repository.ChallengeRepository;
import com.kidsapp.databinding.FragmentInviteFriendBinding;

import java.util.List;

/**
 * Fragment mời bạn bè thách đấu
 * Hiển thị danh sách bạn bè và cho phép chọn để mời
 * 
 * LUỒNG:
 * 1. Chọn chế độ trước (category + difficulty) ở SelectChallengeConfigFragment
 * 2. Chọn bạn ở đây
 * 3. Xác nhận và gửi lời mời
 */
public class InviteFriendFragment extends Fragment implements FriendAdapter.OnFriendClickListener {

    private FragmentInviteFriendBinding binding;
    private FriendAdapter friendAdapter;
    private ChallengeRepository repository;
    
    private Child selectedFriend;
    private String categoryId;
    private String categoryName;
    // Bỏ difficultyLevel vì không cần nữa

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        try {
            binding = FragmentInviteFriendBinding.inflate(inflater, container, false);
            return binding.getRoot();
        } catch (Exception e) {
            android.util.Log.e("InviteFriendFragment", "EXCEPTION in onCreateView", e);
            
            if (container != null && container.getContext() != null) {
                new android.app.AlertDialog.Builder(container.getContext())
                    .setTitle("Lỗi inflate layout")
                    .setMessage("Không thể tạo giao diện")
                    .setPositiveButton("OK", null)
                    .show();
            }
            throw e;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        try {
            repository = new ChallengeRepository(requireContext());
            
            // Check token and childId
            SharedPref sharedPref = new SharedPref(requireContext());
            String token = sharedPref.getAuthToken();
            String childId = sharedPref.getChildId();
            
            if (token == null) {
                new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Lỗi Token")
                    .setMessage("Token không tồn tại. Vui lòng đăng nhập lại.")
                    .setPositiveButton("OK", (dialog, which) -> requireActivity().onBackPressed())
                    .show();
                return;
            }
            
            if (childId == null) {
                new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Lỗi ChildId")
                    .setMessage("ChildId không tồn tại. Vui lòng đăng nhập lại.")
                    .setPositiveButton("OK", (dialog, which) -> requireActivity().onBackPressed())
                    .show();
                return;
            }
            
            // Get category + difficulty from arguments
            Bundle args = getArguments();
            
            if (args != null) {
                categoryId = args.getString("category_id");
                categoryName = args.getString("category_name");
                // Bỏ difficultyLevel
                
                if (categoryId == null || categoryName == null) {
                    new android.app.AlertDialog.Builder(requireContext())
                        .setTitle("Lỗi")
                        .setMessage("Thiếu thông tin chủ đề")
                        .setPositiveButton("OK", (dialog, which) -> requireActivity().onBackPressed())
                        .show();
                    return;
                }
            } else {
                new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Lỗi")
                    .setMessage("Không có arguments")
                    .setPositiveButton("OK", (dialog, which) -> requireActivity().onBackPressed())
                    .show();
                return;
            }
            
            setupViews();
            loadFriends();
            
        } catch (Exception e) {
            android.util.Log.e("InviteFriendFragment", "EXCEPTION in onViewCreated", e);
            
            new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Lỗi nghiêm trọng")
                .setMessage("Có lỗi xảy ra khi khởi tạo giao diện")
                .setPositiveButton("OK", (dialog, which) -> requireActivity().onBackPressed())
                .show();
        }
    }

    private void setupViews() {
        try {
            // Back button
            binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
            
            // Title
            binding.txtTitle.setText("Chọn bạn để mời");
            
            // Subtitle - chỉ hiển thị category
            binding.txtSubtitle.setText(categoryName);
            
            // RecyclerView
            friendAdapter = new FriendAdapter(this);
            binding.rvFriends.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.rvFriends.setAdapter(friendAdapter);
            
            // Send invite button
            updateSendButton();
            binding.btnSendInvite.setOnClickListener(v -> onSendInviteClick());
            
        } catch (Exception e) {
            android.util.Log.e("InviteFriendFragment", "EXCEPTION in setupViews", e);
            
            new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Lỗi Setup")
                .setMessage("Có lỗi khi thiết lập giao diện")
                .setPositiveButton("Quay lại", (dialog, which) -> requireActivity().onBackPressed())
                .show();
            
            throw e;
        }
    }

    private void loadFriends() {
        showLoading(true);
        
        repository.getFriendsList(new ChallengeRepository.ResultCallback<List<Child>>() {
            @Override
            public void onSuccess(List<Child> friends) {
                try {
                    showLoading(false);
                    
                    if (friends == null || friends.isEmpty()) {
                        binding.layoutEmpty.setVisibility(View.VISIBLE);
                        binding.rvFriends.setVisibility(View.GONE);
                        return;
                    }
                    
                    binding.layoutEmpty.setVisibility(View.GONE);
                    binding.rvFriends.setVisibility(View.VISIBLE);
                    friendAdapter.setFriends(friends);
                    
                } catch (Exception e) {
                    new android.app.AlertDialog.Builder(requireContext())
                        .setTitle("Lỗi hiển thị")
                        .setMessage("Có lỗi khi hiển thị danh sách bạn bè")
                        .setPositiveButton("Quay lại", (dialog, which) -> requireActivity().onBackPressed())
                        .show();
                }
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                loadMockData();
            }
        });
    }

    private void loadMockData() {
        // Không dùng mock data nữa - hiển thị empty state
        friendAdapter.setFriends(new java.util.ArrayList<>());
        binding.layoutEmpty.setVisibility(View.VISIBLE);
        binding.rvFriends.setVisibility(View.GONE);
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.layoutContent.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateSendButton() {
        boolean canSend = selectedFriend != null;
        binding.btnSendInvite.setEnabled(canSend);
        binding.btnSendInvite.setAlpha(canSend ? 1.0f : 0.5f);
        
        if (canSend) {
            binding.btnSendInvite.setText("Gửi lời mời đến " + selectedFriend.getNickname());
        } else {
            binding.btnSendInvite.setText("Chọn bạn để gửi lời mời");
        }
    }

    @Override
    public void onFriendClick(Child friend) {
        selectedFriend = friend;
        friendAdapter.setSelectedFriend(friend.getId());
        updateSendButton();
    }

    /**
     * Khi click "Gửi lời mời" → Navigate sang InviteFriendConfirmFragment
     * để xác nhận và gửi lời mời
     */
    private void onSendInviteClick() {
        try {
            if (selectedFriend == null) {
                Toast.makeText(requireContext(), "Vui lòng chọn bạn", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Navigate to InviteFriendConfirmFragment với tất cả thông tin
            Bundle args = new Bundle();
            args.putString("friend_id", selectedFriend.getId());
            args.putString("friend_name", selectedFriend.getNickname());
            args.putString("category_id", categoryId);
            args.putString("category_name", categoryName);
            // Bỏ difficulty_level
            
            InviteFriendConfirmFragment fragment = new InviteFriendConfirmFragment();
            fragment.setArguments(args);
            
            if (getActivity() == null) {
                return;
            }
            
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.childHomeHost, fragment)
                    .addToBackStack(null)
                    .commit();
            
        } catch (Exception e) {
            android.util.Log.e("InviteFriendFragment", "EXCEPTION in onSendInviteClick", e);
            Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
