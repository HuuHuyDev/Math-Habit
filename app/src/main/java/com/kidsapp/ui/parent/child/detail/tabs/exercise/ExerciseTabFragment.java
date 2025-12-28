package com.kidsapp.ui.parent.child.detail.tabs.exercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.Task;
import com.kidsapp.databinding.FragmentExerciseTabBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment hiển thị tab Học tập (EXERCISE tasks)
 */
public class ExerciseTabFragment extends Fragment {

    private FragmentExerciseTabBinding binding;
    private ExerciseAdapter adapter;
    private String childId;

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
        
        // Lấy childId từ arguments của fragment này
        if (getArguments() != null) {
            childId = getArguments().getString("childId");
        }
        
        setupRecyclerView();
        loadExerciseTasks();
    }

    private void setupRecyclerView() {
        adapter = new ExerciseAdapter(new ArrayList<>());
        binding.recyclerExercise.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerExercise.setAdapter(adapter);
        
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        binding.recyclerExercise.setPadding(padding, padding, padding, padding);
        binding.recyclerExercise.setClipToPadding(false);
    }

    private void loadExerciseTasks() {
        if (childId == null || childId.isEmpty()) {
            showEmptyState();
            return;
        }

        SharedPref sharedPref = new SharedPref(requireContext());
        ApiService apiService = RetrofitClient.getInstance(sharedPref).getApiService();
        
        // Lấy ngày hiện tại
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        
        apiService.getTasksByChild(childId, null, "EXERCISE", today)
                .enqueue(new Callback<ApiService.ApiResponseWrapper<List<Task>>>() {
                    @Override
                    public void onResponse(Call<ApiService.ApiResponseWrapper<List<Task>>> call,
                                           Response<ApiService.ApiResponseWrapper<List<Task>>> response) {
                        if (!isAdded()) return;
                        
                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                            List<Task> tasks = response.body().data;
                            updateUI(tasks);
                        } else {
                            showEmptyState();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiService.ApiResponseWrapper<List<Task>>> call, Throwable t) {
                        if (!isAdded()) return;
                        showEmptyState();
                    }
                });
    }

    private void updateUI(List<Task> tasks) {
        if (tasks.isEmpty()) {
            showEmptyState();
            return;
        }

        List<ExerciseTask> taskList = new ArrayList<>();
        for (Task task : tasks) {
            int xpReward = task.getPointsReward();
            taskList.add(new ExerciseTask(
                    task.getId(),
                    task.getTitle(),
                    0, // correctCount - không có trong Task model
                    10, // totalQuestions - mặc định
                    xpReward,
                    R.drawable.ic_task
            ));
        }
        
        adapter.updateData(taskList);
        binding.recyclerExercise.setVisibility(View.VISIBLE);
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
