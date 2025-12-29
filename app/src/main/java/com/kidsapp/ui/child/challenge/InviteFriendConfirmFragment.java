package com.kidsapp.ui.child.challenge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kidsapp.R;
import com.kidsapp.data.repository.ChallengeRepository;
import com.kidsapp.databinding.FragmentInviteFriendConfirmBinding;

/**
 * Fragment xác nhận gửi lời mời thách đấu
 * Hiển thị thông tin đã chọn: bạn bè, chủ đề, độ khó
 * Gửi lời mời khi người dùng xác nhận
 */
public class InviteFriendConfirmFragment extends Fragment {

    private FragmentInviteFriendConfirmBinding binding;
    private ChallengeRepository repository;
    
    private String friendId;
    private String friendName;
    private String categoryId;
    private String categoryName;
    // Bỏ difficultyLevel

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        android.util.Log.d("InviteFriendConfirm", "onCreateView called");
        try {
            binding = FragmentInviteFriendConfirmBinding.inflate(inflater, container, false);
            android.util.Log.d("InviteFriendConfirm", "Binding inflated successfully");
            return binding.getRoot();
        } catch (Exception e) {
            android.util.Log.e("InviteFriendConfirm", "Error inflating layout", e);
            Toast.makeText(container.getContext(), "Lỗi tạo giao diện: " + e.getMessage(), Toast.LENGTH_LONG).show();
            throw e;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        android.util.Log.d("InviteFriendConfirm", "=== onViewCreated START ===");
        
        try {
            repository = new ChallengeRepository(requireContext());
            android.util.Log.d("InviteFriendConfirm", "Repository initialized");
            
            // Get data from arguments
            if (getArguments() != null) {
                friendId = getArguments().getString("friend_id");
                friendName = getArguments().getString("friend_name");
                categoryId = getArguments().getString("category_id");
                categoryName = getArguments().getString("category_name");
                // Bỏ difficultyLevel
                
                android.util.Log.d("InviteFriendConfirm", "Arguments received:");
                android.util.Log.d("InviteFriendConfirm", "  - friendId: " + friendId);
                android.util.Log.d("InviteFriendConfirm", "  - friendName: " + friendName);
                android.util.Log.d("InviteFriendConfirm", "  - categoryId: " + categoryId);
                android.util.Log.d("InviteFriendConfirm", "  - categoryName: " + categoryName);
            } else {
                android.util.Log.e("InviteFriendConfirm", "Arguments is NULL!");
            }
            
            setupViews();
            displayInfo();
            
        } catch (Exception e) {
            android.util.Log.e("InviteFriendConfirm", "EXCEPTION in onViewCreated", e);
            Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
            requireActivity().onBackPressed();
        }
    }

    private void setupViews() {
        try {
            // Back button
            binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
            
            // Send invite button
            binding.btnSendInvite.setOnClickListener(v -> sendInvite());
            
        } catch (Exception e) {
            android.util.Log.e("InviteFriendConfirm", "EXCEPTION in setupViews", e);
            Toast.makeText(requireContext(), "Lỗi setup: " + e.getMessage(), Toast.LENGTH_LONG).show();
            throw e;
        }
    }

    private void displayInfo() {
        try {
            // Display friend name
            binding.txtFriendName.setText(friendName != null ? friendName : "N/A");
            
            // Display category
            binding.txtCategory.setText(categoryName != null ? categoryName : "N/A");
            
            // Bỏ hiển thị difficulty
            binding.txtDifficulty.setText("Câu đố mẹo");
            binding.txtDifficultyStars.setText("🧩");
            
        } catch (Exception e) {
            android.util.Log.e("InviteFriendConfirm", "EXCEPTION in displayInfo", e);
            Toast.makeText(requireContext(), "Lỗi hiển thị: " + e.getMessage(), Toast.LENGTH_LONG).show();
            throw e;
        }
    }

    private void sendInvite() {
        android.util.Log.d("InviteFriendConfirm", "=== sendInvite START ===");
        android.util.Log.d("InviteFriendConfirm", "friendId: " + friendId);
        android.util.Log.d("InviteFriendConfirm", "categoryId: " + categoryId);
        android.util.Log.d("InviteFriendConfirm", "categoryName: " + categoryName);
        
        showLoading(true);
        
        // Bước 1: Tạo Challenge
        repository.createChallenge(categoryId, categoryName, new ChallengeRepository.ResultCallback<String>() {
            @Override
            public void onSuccess(String challengeId) {
                android.util.Log.d("InviteFriendConfirm", "=== createChallenge SUCCESS ===");
                android.util.Log.d("InviteFriendConfirm", "challengeId: " + challengeId);
                
                // Bước 2: Mời bạn
                repository.inviteChild(challengeId, friendId, new ChallengeRepository.ResultCallback<String>() {
                    @Override
                    public void onSuccess(String message) {
                        android.util.Log.d("InviteFriendConfirm", "=== inviteChild SUCCESS ===");
                        android.util.Log.d("InviteFriendConfirm", "message: " + message);
                        
                        showLoading(false);
                        
                        Toast.makeText(requireContext(), 
                            "Đã gửi lời mời thành công!", 
                            Toast.LENGTH_SHORT).show();
                        
                        // Navigate back to ChallengeHomeFragment
                        android.util.Log.d("InviteFriendConfirm", "Popping back stack...");
                        requireActivity().getSupportFragmentManager().popBackStack(null, 
                            androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }

                    @Override
                    public void onError(String error) {
                        android.util.Log.e("InviteFriendConfirm", "=== inviteChild ERROR ===");
                        android.util.Log.e("InviteFriendConfirm", "Error: " + error);
                        
                        showLoading(false);
                        Toast.makeText(requireContext(), 
                            "Lỗi gửi lời mời: " + error, 
                            Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("InviteFriendConfirm", "=== createChallenge ERROR ===");
                android.util.Log.e("InviteFriendConfirm", "Error: " + error);
                
                showLoading(false);
                Toast.makeText(requireContext(), 
                    "Lỗi tạo thách đấu: " + error, 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.layoutContent.setVisibility(show ? View.GONE : View.VISIBLE);
        binding.btnSendInvite.setEnabled(!show);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
