package com.kidsapp.ui.parent.child.detail.tabs.exercise;

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
import com.kidsapp.databinding.FragmentExerciseTabBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment hiển thị tab Bài tập
 */
public class ExerciseTabFragment extends Fragment {

    private FragmentExerciseTabBinding binding;
    private ExerciseAdapter adapter;

    public ExerciseTabFragment() {
        // Required empty public constructor
    }

    public static ExerciseTabFragment newInstance() {
        return new ExerciseTabFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentExerciseTabBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        // Tạo dữ liệu mẫu
        List<ExerciseTask> taskList = new ArrayList<>();
        taskList.add(new ExerciseTask("1", "Cộng trừ trong phạm vi 10", 8, 10, 50, R.drawable.ic_task));
        taskList.add(new ExerciseTask("2", "Nhân chia cơ bản", 6, 8, 40, R.drawable.ic_task));
        taskList.add(new ExerciseTask("3", "So sánh số lớn nhỏ", 10, 10, 60, R.drawable.ic_task));
        taskList.add(new ExerciseTask("4", "Đếm số từ 1 đến 100", 7, 10, 45, R.drawable.ic_task));

        adapter = new ExerciseAdapter(taskList);
        binding.recyclerExercise.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerExercise.setAdapter(adapter);
        
        // Thêm padding cho RecyclerView
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        binding.recyclerExercise.setPadding(padding, padding, padding, padding);
        binding.recyclerExercise.setClipToPadding(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
