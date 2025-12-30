package com.kidsapp.ui.child.reward;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.Badge;
import com.kidsapp.databinding.FragmentRewardBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Reward Fragment - Hiển thị thành tựu/badges
 */
public class RewardFragment extends Fragment {
    private FragmentRewardBinding binding;
    private SharedPref sharedPref;
    private ApiService apiService;
    
    private List<Badge> earnedBadges = new ArrayList<>();
    private List<Badge> inProgressBadges = new ArrayList<>();
    private List<Badge> lockedBadges = new ArrayList<>();
    
    private BadgeRewardAdapter earnedAdapter;
    private BadgeRewardAdapter inProgressAdapter;
    private BadgeRewardAdapter lockedAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRewardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        sharedPref = new SharedPref(requireContext());
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();

        setupBackButton();
        setupRecyclerViews();
        loadBadges();
    }
    
    private void setupBackButton() {
        binding.btnBackAchievement.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }
    
    private void setupRecyclerViews() {
        // Earned badges - Grid 2 columns
        earnedAdapter = new BadgeRewardAdapter(earnedBadges, BadgeRewardAdapter.DisplayMode.EARNED);
        binding.rvEarnedBadges.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.rvEarnedBadges.setAdapter(earnedAdapter);
        
        // In progress badges - Grid 2 columns
        inProgressAdapter = new BadgeRewardAdapter(inProgressBadges, BadgeRewardAdapter.DisplayMode.IN_PROGRESS);
        binding.rvInProgressBadges.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.rvInProgressBadges.setAdapter(inProgressAdapter);
        
        // Locked badges - Grid 2 columns
        lockedAdapter = new BadgeRewardAdapter(lockedBadges, BadgeRewardAdapter.DisplayMode.LOCKED);
        binding.rvLockedBadges.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.rvLockedBadges.setAdapter(lockedAdapter);
    }
    
    private void loadBadges() {
        // Gọi API lấy tất cả badges với progress
        apiService.getMyBadges().enqueue(new Callback<ApiService.ApiResponseWrapper<List<Badge>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<List<Badge>>> call,
                                   @NonNull Response<ApiService.ApiResponseWrapper<List<Badge>>> response) {
                if (getActivity() == null) return;
                
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    List<Badge> allBadges = response.body().data;
                    categorizeBadges(allBadges);
                    updateUI();
                } else {
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<List<Badge>>> call, @NonNull Throwable t) {
                if (getActivity() == null) return;
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                showEmptyState();
            }
        });
    }
    
    /**
     * Phân loại badges thành 3 nhóm: earned, in-progress, locked
     */
    private void categorizeBadges(List<Badge> allBadges) {
        earnedBadges.clear();
        inProgressBadges.clear();
        lockedBadges.clear();
        
        for (Badge badge : allBadges) {
            if (badge.isEarned()) {
                // Đã đạt được
                earnedBadges.add(badge);
            } else if (badge.getProgressPercent() > 0) {
                // Đang tiến hành (có progress > 0)
                inProgressBadges.add(badge);
            } else {
                // Chưa mở khóa (progress = 0)
                lockedBadges.add(badge);
            }
        }
    }
    
    private void updateUI() {
        // Update counts
        binding.txtAchievedCount.setText(String.valueOf(earnedBadges.size()));
        binding.txtInProgressCount.setText(String.valueOf(inProgressBadges.size()));
        binding.txtLockedCount.setText(String.valueOf(lockedBadges.size()));
        
        // Update earned section
        if (earnedBadges.isEmpty()) {
            binding.tvEmptyEarned.setVisibility(View.VISIBLE);
            binding.rvEarnedBadges.setVisibility(View.GONE);
        } else {
            binding.tvEmptyEarned.setVisibility(View.GONE);
            binding.rvEarnedBadges.setVisibility(View.VISIBLE);
            earnedAdapter.notifyDataSetChanged();
        }
        
        // Update in-progress section
        if (inProgressBadges.isEmpty()) {
            binding.tvEmptyInProgress.setVisibility(View.VISIBLE);
            binding.rvInProgressBadges.setVisibility(View.GONE);
        } else {
            binding.tvEmptyInProgress.setVisibility(View.GONE);
            binding.rvInProgressBadges.setVisibility(View.VISIBLE);
            inProgressAdapter.notifyDataSetChanged();
        }
        
        // Update locked section
        if (lockedBadges.isEmpty()) {
            binding.tvEmptyLocked.setVisibility(View.VISIBLE);
            binding.rvLockedBadges.setVisibility(View.GONE);
        } else {
            binding.tvEmptyLocked.setVisibility(View.GONE);
            binding.rvLockedBadges.setVisibility(View.VISIBLE);
            lockedAdapter.notifyDataSetChanged();
        }
    }
    
    private void showEmptyState() {
        binding.txtAchievedCount.setText("0");
        binding.txtInProgressCount.setText("0");
        binding.txtLockedCount.setText("0");
        
        binding.tvEmptyEarned.setVisibility(View.VISIBLE);
        binding.rvEarnedBadges.setVisibility(View.GONE);
        
        binding.tvEmptyInProgress.setVisibility(View.VISIBLE);
        binding.rvInProgressBadges.setVisibility(View.GONE);
        
        binding.tvEmptyLocked.setVisibility(View.VISIBLE);
        binding.rvLockedBadges.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
