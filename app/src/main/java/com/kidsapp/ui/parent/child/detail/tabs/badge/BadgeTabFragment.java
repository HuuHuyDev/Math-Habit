package com.kidsapp.ui.parent.child.detail.tabs.badge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.databinding.FragmentBadgeTabBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment hiển thị tab Huy hiệu
 */
public class BadgeTabFragment extends Fragment {

    private FragmentBadgeTabBinding binding;
    private BadgeAdapter adapter;

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
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        // Tạo dữ liệu mẫu
        List<BadgeItem> badgeList = new ArrayList<>();
        badgeList.add(new BadgeItem("1", "Học giỏi", true, R.drawable.ic_trophy));
        badgeList.add(new BadgeItem("2", "Chăm chỉ", true, R.drawable.ic_trophy));
        badgeList.add(new BadgeItem("3", "Ngoan ngoãn", false, R.drawable.ic_trophy));
        badgeList.add(new BadgeItem("4", "Thông minh", true, R.drawable.ic_trophy));
        badgeList.add(new BadgeItem("5", "Kiên trì", false, R.drawable.ic_trophy));
        badgeList.add(new BadgeItem("6", "Sáng tạo", true, R.drawable.ic_trophy));
        badgeList.add(new BadgeItem("7", "Tốt bụng", false, R.drawable.ic_trophy));
        badgeList.add(new BadgeItem("8", "Năng động", true, R.drawable.ic_trophy));
        badgeList.add(new BadgeItem("9", "Tự tin", false, R.drawable.ic_trophy));

        adapter = new BadgeAdapter(badgeList);
        binding.recyclerBadge.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.recyclerBadge.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
