package com.kidsapp.ui.parent.child.detail.tabs.badge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.databinding.FragmentBadgeTabBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment hiển thị tab Huy hiệu
 */
public class BadgeTabFragment extends Fragment {

    private FragmentBadgeTabBinding binding;
    private BadgeAdapter adapter;
    private String childId;

    public BadgeTabFragment() {
        // Required empty public constructor
    }

    public static BadgeTabFragment newInstance() {
        return new BadgeTabFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBadgeTabBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Lấy childId từ arguments của fragment này
        if (getArguments() != null) {
            childId = getArguments().getString("childId");
        }
        
        setupRecyclerView();
        loadBadges();
    }

    private void setupRecyclerView() {
        adapter = new BadgeAdapter(new ArrayList<>());
        binding.recyclerBadge.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.recyclerBadge.setAdapter(adapter);
    }

    private void loadBadges() {
        if (childId == null || childId.isEmpty()) {
            showEmptyState();
            return;
        }

        SharedPref sharedPref = new SharedPref(requireContext());
        ApiService apiService = RetrofitClient.getInstance(sharedPref).getApiService();
        
        apiService.getChildBadges(childId)
                .enqueue(new Callback<ApiService.ApiResponseWrapper<List<ApiService.BadgeResponse>>>() {
                    @Override
                    public void onResponse(Call<ApiService.ApiResponseWrapper<List<ApiService.BadgeResponse>>> call,
                                           Response<ApiService.ApiResponseWrapper<List<ApiService.BadgeResponse>>> response) {
                        if (!isAdded()) return;
                        
                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                            List<ApiService.BadgeResponse> badges = response.body().data;
                            updateUI(badges);
                        } else {
                            showEmptyState();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiService.ApiResponseWrapper<List<ApiService.BadgeResponse>>> call, Throwable t) {
                        if (!isAdded()) return;
                        showEmptyState();
                    }
                });
    }

    private void updateUI(List<ApiService.BadgeResponse> badges) {
        if (badges.isEmpty()) {
            showEmptyState();
            return;
        }

        List<BadgeItem> badgeList = new ArrayList<>();
        for (ApiService.BadgeResponse badge : badges) {
            badgeList.add(new BadgeItem(
                    badge.id,
                    badge.name,
                    badge.description,
                    badge.earned,
                    badge.iconUrl,
                    badge.progressValue != null ? badge.progressValue : 0,
                    badge.requirementValue != null ? badge.requirementValue : 0,
                    badge.progressPercent != null ? badge.progressPercent : 0,
                    badge.rarity,
                    badge.xpReward != null ? badge.xpReward : 0,
                    badge.coinsReward != null ? badge.coinsReward : 0
            ));
        }
        
        adapter.updateData(badgeList);
    }

    private void showEmptyState() {
        adapter.updateData(new ArrayList<>());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
