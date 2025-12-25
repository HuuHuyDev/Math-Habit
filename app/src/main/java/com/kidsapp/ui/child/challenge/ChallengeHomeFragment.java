package com.kidsapp.ui.child.challenge;

import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kidsapp.R;
import com.kidsapp.databinding.FragmentChallengeHomeBinding;
import com.kidsapp.ui.challenge.JoinByChallengeCodeActivity;
import com.kidsapp.ui.challenge.QuickMatchActivity;

/**
 * Fragment trang chủ Thách đấu
 * Hiển thị các tùy chọn: Quick Match, Mời bạn, Tham gia bằng mã, Bảng xếp hạng, Lịch sử
 */
public class ChallengeHomeFragment extends Fragment {

    private FragmentChallengeHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChallengeHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        checkFirstTimeUser();
        setupClickListeners();
    }

    /**
     * Kiểm tra người dùng lần đầu vào tính năng
     * Nếu lần đầu → hiển thị Tutorial
     */
    private void checkFirstTimeUser() {
        // TODO: Implement SharedPreferences check
        // boolean isFirstTime = sharedPref.isFirstTimeChallenge();
        // if (isFirstTime) {
        //     showTutorial();
        // }
    }

    private void setupClickListeners() {
        // Back button - Cải thiện chức năng quay lại
        binding.btnBack.setOnClickListener(v -> handleBackPressed());

        // Quick Match - Thách đấu nhanh
        binding.cardQuickMatch.setOnClickListener(v -> {
            // Thêm haptic feedback
            try {
                Vibrator vibrator = (Vibrator) requireContext().getSystemService(android.content.Context.VIBRATOR_SERVICE);
                if (vibrator != null && vibrator.hasVibrator()) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(30);
                    }
                }
            } catch (Exception e) {
                // Ignore vibration errors
            }
            
            // Navigate to QuickMatchActivity instead of Fragment
            Intent intent = new Intent(requireContext(), QuickMatchActivity.class);
            startActivity(intent);
        });

        // Invite Friend - Mời bạn
        binding.cardInviteFriend.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Mời bạn bè - Đang phát triển", Toast.LENGTH_SHORT).show();
        });

        // Join by Code - Tham gia bằng mã
        binding.cardJoinByCode.setOnClickListener(v -> {
            showJoinByCodeDialog();
        });

        // Leaderboard - Bảng xếp hạng
        binding.cardLeaderboard.setOnClickListener(v -> {
            navigateToFragment(new LeaderboardFragment());
        });

        // History - Lịch sử
        binding.cardHistory.setOnClickListener(v -> {
            navigateToFragment(new ChallengeHistoryFragment());
        });
    }
    
    /**
     * Xử lý nút quay lại với animation và feedback
     */
    private void handleBackPressed() {
        // Thêm haptic feedback
        try {
            Vibrator vibrator = (Vibrator) requireContext().getSystemService(android.content.Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(50);
                }
            }
        } catch (Exception e) {
            // Ignore vibration errors
        }
        
        // Thêm animation cho nút
        binding.btnBack.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction(() -> {
                    binding.btnBack.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .start();
                })
                .start();
        
        // Delay một chút để animation hoàn thành
        new android.os.Handler().postDelayed(() -> {
            // Kiểm tra xem có fragment nào trong back stack không
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                // Nếu có fragment trong back stack, pop nó
                getParentFragmentManager().popBackStack();
            } else {
                // Nếu không có, quay về màn hình chính
                requireActivity().onBackPressed();
            }
        }, 150);
    }

    /**
     * Navigate to a fragment với animation
     */
    private void navigateToFragment(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // enter animation
                            R.anim.slide_out_left,  // exit animation
                            R.anim.slide_in_left,   // pop enter animation
                            R.anim.slide_out_right  // pop exit animation
                    )
                    .replace(R.id.childHomeHost, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Hiển thị dialog nhập mã thách đấu
     */
    private void showJoinByCodeDialog() {
        Intent intent = new Intent(requireContext(), JoinByChallengeCodeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
