package com.kidsapp.ui.child.challenge;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.tabs.TabLayout;
import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.repository.LeaderboardRepository;
import com.kidsapp.databinding.FragmentLeaderboardBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment hiển thị bảng xếp hạng
 * Có các tab: Hôm nay, Tuần này, Tháng này, Tổng thể
 */
public class LeaderboardFragment extends Fragment {
    private static final String TAG = "LeaderboardFragment";
    
    private FragmentLeaderboardBinding binding;
    private LeaderboardRepository leaderboardRepository;
    private LeaderboardAdapter adapter;
    private String currentPeriod = LeaderboardRepository.Period.WEEKLY;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initRepository();
        setupUI();
        setupTabs();
        loadLeaderboard();
    }
    
    private void initRepository() {
        leaderboardRepository = new LeaderboardRepository(requireContext());
    }
    
    private void setupUI() {
        // Setup RecyclerView
        adapter = new LeaderboardAdapter(new ArrayList<>());
        binding.recyclerViewLeaderboard.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewLeaderboard.setAdapter(adapter);
        
        // Setup SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener(this::loadLeaderboard);
        binding.swipeRefreshLayout.setColorSchemeResources(
                R.color.primary,
                R.color.secondary,
                R.color.accent
        );
        
        // Back button
        binding.btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                requireActivity().onBackPressed();
            }
        });
        
        // My ranking card click
        binding.cardMyRanking.setOnClickListener(v -> {
            // Scroll to current user in list
            scrollToCurrentUser();
        });
    }
    
    private void setupTabs() {
        // Add tabs
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Hôm nay"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Tuần này"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Tháng này"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Tổng thể"));
        
        // Set default tab (Weekly)
        binding.tabLayout.getTabAt(1).select();
        
        // Tab selection listener
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        currentPeriod = LeaderboardRepository.Period.DAILY;
                        break;
                    case 1:
                        currentPeriod = LeaderboardRepository.Period.WEEKLY;
                        break;
                    case 2:
                        currentPeriod = LeaderboardRepository.Period.MONTHLY;
                        break;
                    case 3:
                        currentPeriod = LeaderboardRepository.Period.ALL_TIME;
                        break;
                }
                loadLeaderboard();
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Refresh when tab is reselected
                loadLeaderboard();
            }
        });
    }
    
    private void loadLeaderboard() {
        showLoading(true);
        
        // Load leaderboard data
        leaderboardRepository.getLeaderboard(currentPeriod, 50, new LeaderboardRepository.OnLeaderboardCallback() {
            @Override
            public void onSuccess(List<ApiService.LeaderboardResponse> leaderboard) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        updateLeaderboard(leaderboard);
                        loadMyRanking();
                    });
                }
            }
            
            @Override
            public void onError(String message) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showError("Không thể tải bảng xếp hạng: " + message);
                        Log.e(TAG, "Error loading leaderboard: " + message);
                    });
                }
            }
        });
    }
    
    private void loadMyRanking() {
        leaderboardRepository.getMyRanking(currentPeriod, new LeaderboardRepository.OnRankingCallback() {
            @Override
            public void onSuccess(ApiService.LeaderboardResponse ranking) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> updateMyRanking(ranking));
                }
            }
            
            @Override
            public void onError(String message) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Log.e(TAG, "Error loading my ranking: " + message);
                        // Hide my ranking card if error
                        binding.cardMyRanking.setVisibility(View.GONE);
                    });
                }
            }
        });
    }
    
    private void updateLeaderboard(List<ApiService.LeaderboardResponse> leaderboard) {
        if (leaderboard.isEmpty()) {
            showEmptyState();
        } else {
            hideEmptyState();
            adapter.updateData(leaderboard);
        }
    }
    
    private void updateMyRanking(ApiService.LeaderboardResponse ranking) {
        binding.cardMyRanking.setVisibility(View.VISIBLE);
        
        // Update my ranking info
        binding.textMyRank.setText(ranking.rankDisplay != null ? ranking.rankDisplay : "#" + ranking.rank);
        binding.textMyName.setText(ranking.childName != null ? ranking.childName : "Bạn");
        binding.textMyXp.setText(ranking.totalXp + " XP");
        binding.textMyStats.setText(ranking.exercisesCompleted + " bài • " + ranking.challengesWon + " thắng");
        
        // Set badge/medal
        if (ranking.rank != null) {
            if (ranking.rank == 1) {
                binding.textMyRank.setBackgroundResource(R.drawable.bg_rank_gold);
                binding.textMyBadge.setText("🥇");
            } else if (ranking.rank == 2) {
                binding.textMyRank.setBackgroundResource(R.drawable.bg_rank_silver);
                binding.textMyBadge.setText("🥈");
            } else if (ranking.rank == 3) {
                binding.textMyRank.setBackgroundResource(R.drawable.bg_rank_bronze);
                binding.textMyBadge.setText("🥉");
            } else if (ranking.rank <= 10) {
                binding.textMyRank.setBackgroundResource(R.drawable.bg_rank_top10);
                binding.textMyBadge.setText("⭐");
            } else {
                binding.textMyRank.setBackgroundResource(R.drawable.bg_rank_default);
                binding.textMyBadge.setText("🎯");
            }
        }
        
        // Set current user flag for highlighting in list
        ranking.isCurrentUser = true;
        adapter.setCurrentUserId(ranking.childId);
    }
    
    private void scrollToCurrentUser() {
        int position = adapter.getCurrentUserPosition();
        if (position >= 0) {
            binding.recyclerViewLeaderboard.smoothScrollToPosition(position);
        }
    }
    
    private void showLoading(boolean show) {
        binding.swipeRefreshLayout.setRefreshing(show);
        if (show) {
            binding.layoutEmpty.setVisibility(View.GONE);
        }
    }
    
    private void showEmptyState() {
        binding.layoutEmpty.setVisibility(View.VISIBLE);
        binding.textEmptyTitle.setText("Chưa có dữ liệu");
        binding.textEmptyMessage.setText("Bảng xếp hạng sẽ xuất hiện khi có người chơi tham gia thách đấu");
        binding.btnEmptyAction.setText("Làm mới");
        binding.btnEmptyAction.setOnClickListener(v -> loadLeaderboard());
    }
    
    private void hideEmptyState() {
        binding.layoutEmpty.setVisibility(View.GONE);
    }
    
    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}