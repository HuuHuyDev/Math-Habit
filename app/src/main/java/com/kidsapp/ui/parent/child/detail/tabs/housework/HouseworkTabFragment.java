package com.kidsapp.ui.parent.child.detail.tabs.housework;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.databinding.FragmentHouseworkTabBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment hiển thị tab Việc nhà
 */
public class HouseworkTabFragment extends Fragment {

    private FragmentHouseworkTabBinding binding;
    private HouseworkAdapter adapter;

    public HouseworkTabFragment() {
        // Required empty public constructor
    }

    public static HouseworkTabFragment newInstance() {
        return new HouseworkTabFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHouseworkTabBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        // Tạo dữ liệu mẫu giống hình tham chiếu
        List<HouseworkTask> taskList = new ArrayList<>();
        taskList.add(new HouseworkTask("1", "Đánh răng", true, R.drawable.ic_toothbrush));
        taskList.add(new HouseworkTask("2", "Dọn đồ chơi", true, R.drawable.ic_toys));
        taskList.add(new HouseworkTask("3", "Đọc sách", true, R.drawable.ic_book));
        taskList.add(new HouseworkTask("4", "Uống nước", false, R.drawable.ic_water));
        taskList.add(new HouseworkTask("5", "Ăn sáng đầy đủ", false, R.drawable.ic_breakfast));

        adapter = new HouseworkAdapter(taskList);
        binding.recyclerHousework.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerHousework.setAdapter(adapter);
        
        // Thêm padding cho RecyclerView
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        binding.recyclerHousework.setPadding(padding, padding, padding, padding);
        binding.recyclerHousework.setClipToPadding(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
