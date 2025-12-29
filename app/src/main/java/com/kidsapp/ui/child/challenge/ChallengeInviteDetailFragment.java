package com.kidsapp.ui.child.challenge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.kidsapp.R;
import com.kidsapp.data.repository.ChallengeRepository;
import com.kidsapp.data.response.ChallengeInviteResponse;
import com.kidsapp.data.response.QuestionResponse;
import com.kidsapp.databinding.FragmentChallengeInviteDetailBinding;

import java.util.List;

/**
 * Fragment hiển thị chi tiết lời mời thách đấu
 * Cho phép xem thông tin đầy đủ và thực hiện hành động
 */
public class ChallengeInviteDetailFragment extends Fragment {

    private FragmentChallengeInviteDetailBinding binding;
    private ChallengeRepository repository;
    private String inviteId;
    private ChallengeInviteResponse invite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChallengeInviteDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        repository = new ChallengeRepository(requireContext());
        
        // Get invite ID from arguments
        if (getArguments() != null) {
            inviteId = getArguments().getString("invite_id");
        }
        
        if (inviteId == null) {
            Toast.makeText(requireContext(), "Lỗi: Không có ID lời mời", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return;
        }
        
        setupViews();
        loadInviteDetail();
    }

    private void setupViews() {
        // Back button
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        
        // Action buttons
        binding.btnAccept.setOnClickListener(v -> acceptInvite());
        binding.btnDecline.setOnClickListener(v -> declineInvite());
        
        // TEMPORARY: Add test button for debugging
        if (binding.btnAccept != null) {
            binding.btnAccept.setOnLongClickListener(v -> {
                // Navigate to test fragment on long click
                ApiTestFragment testFragment = new ApiTestFragment();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.childHomeHost, testFragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            });
        }
    }

    private void loadInviteDetail() {
        showLoading(true);
        
        repository.getInviteDetail(inviteId, new ChallengeRepository.ResultCallback<ChallengeInviteResponse>() {
            @Override
            public void onSuccess(ChallengeInviteResponse inviteResponse) {
                showLoading(false);
                invite = inviteResponse;
                displayInviteDetail();
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        });
    }

    private void displayInviteDetail() {
        if (invite == null) return;
        
        binding.txtCreatorName.setText(invite.getCreatorName() != null ? invite.getCreatorName() : "Bạn bè");
        binding.txtChallengeTitle.setText(invite.getChallengeTitle() != null ? invite.getChallengeTitle() : "Thách đấu");
        binding.txtCategoryName.setText(invite.getCategoryName() != null ? invite.getCategoryName() : "Câu đố mẹo");
        binding.txtDescription.setText(invite.getDescription() != null ? invite.getDescription() : "Thách đấu câu đố mẹo với anh chị em");
        binding.txtTimeAgo.setText(invite.getTimeAgo() != null ? invite.getTimeAgo() : "Vừa xong");
        
        // Show/hide action buttons based on status
        String status = invite.getStatus();
        if ("PENDING".equals(status)) {
            binding.layoutActions.setVisibility(View.VISIBLE);
        } else {
            binding.layoutActions.setVisibility(View.GONE);
            binding.txtStatus.setText("Trạng thái: " + getStatusText(status));
            binding.txtStatus.setVisibility(View.VISIBLE);
        }
    }

    private void acceptInvite() {
        if (invite == null) {
            android.util.Log.e("ChallengeInviteDetail", "Invite is null");
            Toast.makeText(requireContext(), "Lỗi: Thông tin lời mời không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        
        android.util.Log.d("ChallengeInviteDetail", "=== acceptInvite START ===");
        android.util.Log.d("ChallengeInviteDetail", "Invite ID: " + invite.getId());
        android.util.Log.d("ChallengeInviteDetail", "Invite Status: " + invite.getStatus());
        
        showLoading(true);
        binding.btnAccept.setEnabled(false);
        binding.btnDecline.setEnabled(false);
        
        repository.acceptInvite(invite.getId(), new ChallengeRepository.ResultCallback<com.kidsapp.data.response.ChallengeResponse>() {
            @Override
            public void onSuccess(com.kidsapp.data.response.ChallengeResponse challenge) {
                android.util.Log.d("ChallengeInviteDetail", "=== acceptInvite SUCCESS ===");
                android.util.Log.d("ChallengeInviteDetail", "Challenge received: " + (challenge != null ? "NOT NULL" : "NULL"));
                
                // CRITICAL: Check if fragment is still attached
                if (!isAdded() || getActivity() == null || getActivity().isFinishing()) {
                    android.util.Log.e("ChallengeInviteDetail", "Fragment not attached or activity finishing");
                    return;
                }
                
                // Ensure we're on UI thread
                getActivity().runOnUiThread(() -> {
                    try {
                        showLoading(false);
                        
                        if (challenge == null) {
                            android.util.Log.e("ChallengeInviteDetail", "Challenge response is null");
                            Toast.makeText(requireContext(), "Lỗi: Không nhận được thông tin thách đấu", Toast.LENGTH_SHORT).show();
                            binding.btnAccept.setEnabled(true);
                            binding.btnDecline.setEnabled(true);
                            return;
                        }
                        
                        android.util.Log.d("ChallengeInviteDetail", "Challenge ID: " + challenge.getId());
                        android.util.Log.d("ChallengeInviteDetail", "Challenge Status: " + challenge.getStatus());
                        android.util.Log.d("ChallengeInviteDetail", "Challenge Title: " + challenge.getTitle());
                        
                        Toast.makeText(requireContext(), "🎯 Đã chấp nhận! Đang vào thách đấu...", Toast.LENGTH_LONG).show();
                        
                        // Navigate immediately without delay to avoid fragment lifecycle issues
                        navigateToBattleImmediate(challenge);
                        
                    } catch (Exception e) {
                        android.util.Log.e("ChallengeInviteDetail", "Error in UI thread", e);
                        Toast.makeText(requireContext(), "Lỗi UI: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("ChallengeInviteDetail", "=== acceptInvite ERROR ===");
                android.util.Log.e("ChallengeInviteDetail", "Error: " + error);
                
                // Ensure we're on UI thread and fragment is still attached
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {
                            showLoading(false);
                            binding.btnAccept.setEnabled(true);
                            binding.btnDecline.setEnabled(true);
                            
                            Toast.makeText(requireContext(), "Lỗi chấp nhận: " + error, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            android.util.Log.e("ChallengeInviteDetail", "Error in error handling", e);
                        }
                    });
                }
            }
        });
    }

    private void declineInvite() {
        if (invite == null) return;
        
        repository.declineInvite(invite.getId(), new ChallengeRepository.ResultCallback<String>() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(requireContext(), "Đã từ chối lời mời", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToBattleImmediate(com.kidsapp.data.response.ChallengeResponse challenge) {
        android.util.Log.d("ChallengeInviteDetail", "=== navigateToBattleImmediate START ===");
        
        try {
            // Double check fragment state
            if (!isAdded() || getActivity() == null || getActivity().isFinishing() || getActivity().isDestroyed()) {
                android.util.Log.e("ChallengeInviteDetail", "Cannot navigate - fragment/activity not ready");
                return;
            }
            
            // Validate challenge
            if (challenge == null || challenge.getId() == null || challenge.getId().isEmpty()) {
                android.util.Log.e("ChallengeInviteDetail", "Invalid challenge data");
                Toast.makeText(requireContext(), "Lỗi: Dữ liệu thách đấu không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            
            android.util.Log.d("ChallengeInviteDetail", "Creating battle fragment for challenge: " + challenge.getId());
            
            // Create minimal bundle - only essential data
            Bundle args = new Bundle();
            args.putString("challenge_id", challenge.getId());
            args.putString("challenge_title", challenge.getTitle() != null ? challenge.getTitle() : "Thách đấu");
            args.putString("opponent_name", invite != null && invite.getCreatorName() != null ? invite.getCreatorName() : "Đối thủ");
            args.putBoolean("is_battle_mode", true);
            
            // Create fragment
            QuizBattleFragment battleFragment = new QuizBattleFragment();
            battleFragment.setArguments(args);
            
            android.util.Log.d("ChallengeInviteDetail", "Starting fragment transaction...");
            
            // Use the most reliable navigation method
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.childHomeHost, battleFragment)
                    .commitNowAllowingStateLoss(); // Use commitNow for immediate execution
                    
            android.util.Log.d("ChallengeInviteDetail", "=== navigateToBattleImmediate SUCCESS ===");
            
        } catch (IllegalStateException e) {
            android.util.Log.e("ChallengeInviteDetail", "IllegalStateException - fragment state issue", e);
            
            // Try alternative: finish current activity and start new one
            tryAlternativeNavigation(challenge);
            
        } catch (Exception e) {
            android.util.Log.e("ChallengeInviteDetail", "Unexpected error in navigation", e);
            
            // Show error and go back
            Toast.makeText(requireContext(), "Lỗi điều hướng. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
            
            // Go back to previous screen
            new android.os.Handler().postDelayed(() -> {
                if (isAdded() && getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }, 2000);
        }
    }
    
    private void tryAlternativeNavigation(com.kidsapp.data.response.ChallengeResponse challenge) {
        android.util.Log.d("ChallengeInviteDetail", "Trying alternative navigation...");
        
        try {
            // Alternative 1: Clear back stack and navigate
            if (getActivity() != null && getActivity().getSupportFragmentManager() != null) {
                getActivity().getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                
                // Create fragment again
                Bundle args = new Bundle();
                args.putString("challenge_id", challenge.getId());
                args.putBoolean("is_battle_mode", true);
                
                QuizBattleFragment battleFragment = new QuizBattleFragment();
                battleFragment.setArguments(args);
                
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.childHomeHost, battleFragment)
                        .commitAllowingStateLoss();
                        
                android.util.Log.d("ChallengeInviteDetail", "Alternative navigation successful");
            }
        } catch (Exception e) {
            android.util.Log.e("ChallengeInviteDetail", "Alternative navigation also failed", e);
            
            // Last resort: show message and let user navigate manually
            Toast.makeText(requireContext(), "Thách đấu đã sẵn sàng! Vui lòng vào mục 'Thách đấu đang diễn ra'", Toast.LENGTH_LONG).show();
            
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        }
    }
    
    private void testChallengeAccessibility(com.kidsapp.data.response.ChallengeResponse challenge) {
        android.util.Log.d("ChallengeInviteDetail", "=== testChallengeAccessibility START ===");
        android.util.Log.d("ChallengeInviteDetail", "Testing challenge: " + challenge.getId());
        
        // Test if we can load challenge questions first
        repository.getChallengeQuestions(challenge.getId(), new ChallengeRepository.ResultCallback<List<com.kidsapp.data.response.QuestionResponse>>() {
            @Override
            public void onSuccess(List<QuestionResponse> questions) {
                android.util.Log.d("ChallengeInviteDetail", "Challenge questions loaded successfully: " + questions.size() + " questions");
                
                // If questions load successfully, proceed to battle
                navigateToBattle(challenge);
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("ChallengeInviteDetail", "Failed to load challenge questions: " + error);
                
                // Show error but still try to navigate (maybe questions will load later)
                Toast.makeText(requireContext(), "Cảnh báo: " + error + ". Vẫn thử vào thách đấu...", Toast.LENGTH_LONG).show();
                
                // Try navigation anyway
                new android.os.Handler().postDelayed(() -> {
                    navigateToBattle(challenge);
                }, 2000);
            }
        });
    }
    
    private void navigateToBattle(com.kidsapp.data.response.ChallengeResponse challenge) {
        android.util.Log.d("ChallengeInviteDetail", "=== navigateToBattle START ===");
        android.util.Log.d("ChallengeInviteDetail", "Challenge ID: " + challenge.getId());
        android.util.Log.d("ChallengeInviteDetail", "Challenge Title: " + challenge.getTitle());
        android.util.Log.d("ChallengeInviteDetail", "Challenge Status: " + challenge.getStatus());
        
        try {
            // Validate challenge data
            if (challenge.getId() == null || challenge.getId().isEmpty()) {
                android.util.Log.e("ChallengeInviteDetail", "Challenge ID is null or empty");
                Toast.makeText(requireContext(), "Lỗi: Thông tin thách đấu không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Try simple navigation first - without any extras
            android.util.Log.d("ChallengeInviteDetail", "Attempting simple navigation...");
            
            // Create minimal bundle
            Bundle args = new Bundle();
            args.putString("challenge_id", challenge.getId());
            
            android.util.Log.d("ChallengeInviteDetail", "Bundle created with challenge_id: " + challenge.getId());
            
            // Validate fragment manager
            if (getActivity() == null) {
                android.util.Log.e("ChallengeInviteDetail", "Activity is null");
                Toast.makeText(requireContext(), "Lỗi: Activity không khả dụng", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (getActivity().getSupportFragmentManager() == null) {
                android.util.Log.e("ChallengeInviteDetail", "FragmentManager is null");
                Toast.makeText(requireContext(), "Lỗi: FragmentManager không khả dụng", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Create fragment
            android.util.Log.d("ChallengeInviteDetail", "Creating QuizBattleFragment...");
            QuizBattleFragment fragment = new QuizBattleFragment();
            fragment.setArguments(args);
            
            android.util.Log.d("ChallengeInviteDetail", "QuizBattleFragment created successfully");
            
            // Try the simplest possible navigation
            android.util.Log.d("ChallengeInviteDetail", "Starting fragment transaction...");
            
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.childHomeHost, fragment)
                    .commitAllowingStateLoss();
                    
            android.util.Log.d("ChallengeInviteDetail", "Fragment transaction committed");
            android.util.Log.d("ChallengeInviteDetail", "=== navigateToBattle SUCCESS ===");
            
        } catch (IllegalStateException e) {
            android.util.Log.e("ChallengeInviteDetail", "IllegalStateException in navigation", e);
            Toast.makeText(requireContext(), "Lỗi trạng thái: " + e.getMessage(), Toast.LENGTH_LONG).show();
            
            // Try alternative: go back to home and let user navigate manually
            try {
                requireActivity().onBackPressed();
            } catch (Exception backError) {
                android.util.Log.e("ChallengeInviteDetail", "Even onBackPressed failed", backError);
            }
            
        } catch (Exception e) {
            android.util.Log.e("ChallengeInviteDetail", "=== navigateToBattle ERROR ===", e);
            android.util.Log.e("ChallengeInviteDetail", "Error type: " + e.getClass().getSimpleName());
            android.util.Log.e("ChallengeInviteDetail", "Error message: " + e.getMessage());
            
            Toast.makeText(requireContext(), "Lỗi điều hướng: " + e.getMessage(), Toast.LENGTH_LONG).show();
            
            // Try alternative navigation - go back and let user try again
            try {
                requireActivity().onBackPressed();
            } catch (Exception backError) {
                android.util.Log.e("ChallengeInviteDetail", "Even onBackPressed failed", backError);
            }
        }
    }

    private void navigateToActiveChallenge(String challengeId) {
        Bundle args = new Bundle();
        args.putString("challenge_id", challengeId);
        
        ActiveChallengesFragment fragment = new ActiveChallengesFragment();
        fragment.setArguments(args);
        
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
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.layoutContent.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private String getStatusText(String status) {
        if (status == null) return "Không xác định";
        
        switch (status) {
            case "ACCEPTED":
                return "Đã chấp nhận";
            case "DECLINED":
                return "Đã từ chối";
            case "EXPIRED":
                return "Đã hết hạn";
            default:
                return status;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}