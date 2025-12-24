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
import com.kidsapp.databinding.FragmentChallengeHomeBinding;

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
        // Back button
        binding.btnBack.setOnClickListener(v -> 
            requireActivity().onBackPressed());

        // Quick Match - Thách đấu nhanh
        binding.cardQuickMatch.setOnClickListener(v -> {
            navigateToFragment(new QuickMatchFragment());
        });

        // Invite Friend - Mời bạn
        binding.cardInviteFriend.setOnClickListener(v -> {
            navigateToFragment(new InviteFriendFragment());
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
     * Navigate to a fragment
     */
    private void navigateToFragment(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.childHomeHost, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Hiển thị dialog nhập mã thách đấu
     */
    private void showJoinByCodeDialog() {
        Toast.makeText(requireContext(), "Nhập mã thách đấu - Đang phát triển", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
