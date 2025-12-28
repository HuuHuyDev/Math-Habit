package com.kidsapp.ui.parent.child.detail.tabs.housework;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.Task;
import com.kidsapp.data.repository.TaskRepository;
import com.kidsapp.databinding.FragmentHouseworkTabBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment hiển thị tab Thói quen (HABIT tasks)
 * Parent có thể xem và duyệt/từ chối các HABIT đã submit
 */
public class HouseworkTabFragment extends Fragment {

    private FragmentHouseworkTabBinding binding;
    private HouseworkAdapter adapter;
    private String childId;
    private TaskRepository taskRepository;

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
        
        // Lấy childId từ arguments của fragment này
        if (getArguments() != null) {
            childId = getArguments().getString("childId");
        }
        
        taskRepository = new TaskRepository(requireContext());
        
        setupRecyclerView();
        loadHabitTasks();
    }

    private void setupRecyclerView() {
        adapter = new HouseworkAdapter(new ArrayList<>());
        adapter.setOnTaskActionListener(new HouseworkAdapter.OnTaskActionListener() {
            @Override
            public void onApproveClick(HouseworkTask task) {
                approveHabit(task);
            }

            @Override
            public void onRejectClick(HouseworkTask task) {
                showRejectDialog(task);
            }

            @Override
            public void onViewProofClick(HouseworkTask task) {
                viewProof(task);
            }
        });
        
        binding.recyclerHousework.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerHousework.setAdapter(adapter);
        
        // Chỉ padding top/bottom, không padding horizontal để item full width
        int paddingVertical = (int) (8 * getResources().getDisplayMetrics().density);
        binding.recyclerHousework.setPadding(0, paddingVertical, 0, paddingVertical);
        binding.recyclerHousework.setClipToPadding(false);
    }

    private void loadHabitTasks() {
        if (childId == null || childId.isEmpty()) {
            android.util.Log.e("HouseworkTabFragment", "childId is null or empty");
            showEmptyState();
            return;
        }

        android.util.Log.d("HouseworkTabFragment", "Loading HABIT tasks for childId: " + childId);

        SharedPref sharedPref = new SharedPref(requireContext());
        ApiService apiService = RetrofitClient.getInstance(sharedPref).getApiService();
        
        // Lấy ngày hôm nay để filter
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(new java.util.Date());
        
        // Load HABIT tasks của ngày hôm nay
        apiService.getTasksByChild(childId, null, "HABIT", today)
                .enqueue(new Callback<ApiService.ApiResponseWrapper<List<Task>>>() {
                    @Override
                    public void onResponse(Call<ApiService.ApiResponseWrapper<List<Task>>> call,
                                           Response<ApiService.ApiResponseWrapper<List<Task>>> response) {
                        if (!isAdded()) return;
                        
                        android.util.Log.d("HouseworkTabFragment", "Response code: " + response.code());
                        
                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                            List<Task> tasks = response.body().data;
                            android.util.Log.d("HouseworkTabFragment", "Loaded " + tasks.size() + " tasks");
                            updateUI(tasks);
                        } else {
                            android.util.Log.e("HouseworkTabFragment", "Response failed or empty");
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

        List<HouseworkTask> taskList = new ArrayList<>();
        for (Task task : tasks) {
            String status = task.getStatus();
            boolean isCompleted = "COMPLETED".equalsIgnoreCase(status);
            boolean isSubmitted = "SUBMITTED".equalsIgnoreCase(status);
            
            HouseworkTask houseworkTask = new HouseworkTask(
                    task.getId(),
                    task.getTitle(),
                    isCompleted,
                    R.drawable.ic_task
            );
            houseworkTask.setStatus(status);
            houseworkTask.setProofUrl(task.getProofUrl());
            houseworkTask.setSubmitted(isSubmitted);
            
            taskList.add(houseworkTask);
        }
        
        adapter.updateData(taskList);
        binding.recyclerHousework.setVisibility(View.VISIBLE);
    }

    private void showEmptyState() {
        // Hiển thị empty state hoặc message
        adapter.updateData(new ArrayList<>());
    }
    
    /**
     * Duyệt HABIT task
     */
    private void approveHabit(HouseworkTask task) {
        Toast.makeText(requireContext(), "Đang duyệt...", Toast.LENGTH_SHORT).show();
        
        taskRepository.approveHabit(task.getId(), new TaskRepository.TaskCallback() {
            @Override
            public void onSuccess(Task result) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Đã duyệt thói quen!", Toast.LENGTH_SHORT).show();
                loadHabitTasks();
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Hiển thị dialog nhập lý do từ chối
     */
    private void showRejectDialog(HouseworkTask task) {
        android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setHint("Nhập lý do từ chối (tùy chọn)");
        
        new AlertDialog.Builder(requireContext())
                .setTitle("Từ chối thói quen")
                .setMessage("Bạn có chắc muốn từ chối? Con sẽ cần làm lại.")
                .setView(input)
                .setPositiveButton("Từ chối", (dialog, which) -> {
                    String reason = input.getText().toString().trim();
                    rejectHabit(task, reason);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    /**
     * Từ chối HABIT task
     */
    private void rejectHabit(HouseworkTask task, String reason) {
        Toast.makeText(requireContext(), "Đang từ chối...", Toast.LENGTH_SHORT).show();
        
        taskRepository.rejectHabit(task.getId(), reason, new TaskRepository.TaskCallback() {
            @Override
            public void onSuccess(Task result) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Đã từ chối. Con sẽ cần làm lại.", Toast.LENGTH_SHORT).show();
                loadHabitTasks();
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Xem minh chứng (ảnh/video) trong dialog
     */
    private void viewProof(HouseworkTask task) {
        if (task.getProofUrl() == null || task.getProofUrl().isEmpty()) {
            Toast.makeText(requireContext(), "Chưa có minh chứng", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ViewProofDialog dialog = ViewProofDialog.newInstance(task);
        dialog.setOnProofActionListener(new ViewProofDialog.OnProofActionListener() {
            @Override
            public void onApprove(HouseworkTask t) {
                approveHabit(t);
            }

            @Override
            public void onReject(HouseworkTask t, String reason) {
                rejectHabit(t, reason);
            }
        });
        dialog.show(getChildFragmentManager(), "ViewProofDialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
