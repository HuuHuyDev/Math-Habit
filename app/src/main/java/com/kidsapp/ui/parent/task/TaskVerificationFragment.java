package com.kidsapp.ui.parent.task;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.Task;
import com.kidsapp.databinding.FragmentTaskVerificationBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment để phụ huynh xác nhận hoàn thành nhiệm vụ
 */
public class TaskVerificationFragment extends Fragment {

    private static final String TAG = "TaskVerificationFragment";
    private static final String ARG_TASK = "task";

    private FragmentTaskVerificationBinding binding;
    private ApiService apiService;
    private SharedPref sharedPref;
    private Task task;

    public static TaskVerificationFragment newInstance(Task task) {
        TaskVerificationFragment fragment = new TaskVerificationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TASK, task);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            task = (Task) getArguments().getSerializable(ARG_TASK);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskVerificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPref = new SharedPref(requireContext());
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();

        setupViews();
        setupListeners();
    }

    private void setupViews() {
        if (task == null) return;

        binding.txtTaskTitle.setText(task.getTitle());
        binding.txtTaskDescription.setText(task.getDescription());
        binding.txtCompletedAt.setText("Hoàn thành: " + formatDate(task.getCompletedAt()));

        // Hiển thị ghi chú
        if (task.getProofNote() != null && !task.getProofNote().isEmpty()) {
            binding.txtNote.setText(task.getProofNote());
            binding.layoutNote.setVisibility(View.VISIBLE);
        } else {
            binding.layoutNote.setVisibility(View.GONE);
        }

        // Hiển thị ảnh chứng minh
        if (task.getProofImageUrl() != null && !task.getProofImageUrl().isEmpty()) {
            binding.layoutImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(task.getProofImageUrl())
                    .centerCrop()
                    .into(binding.imgProof);

            // Click để xem full ảnh
            binding.imgProof.setOnClickListener(v -> showFullImage());
        } else {
            binding.layoutImage.setVisibility(View.GONE);
        }

        // Hiển thị video chứng minh
        if (task.getProofVideoUrl() != null && !task.getProofVideoUrl().isEmpty()) {
            binding.layoutVideo.setVisibility(View.VISIBLE);
            binding.txtVideoUrl.setText(task.getProofVideoUrl());

            // Click để xem video
            binding.btnPlayVideo.setOnClickListener(v -> playVideo());
        } else {
            binding.layoutVideo.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        // Back button
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Approve button
        binding.btnApprove.setOnClickListener(v -> approveTask());

        // Reject button
        binding.btnReject.setOnClickListener(v -> showRejectDialog());
    }

    private void approveTask() {
        String feedback = binding.edtFeedback.getText().toString().trim();

        showLoading(true);

        ApiService.TaskVerificationRequest request = new ApiService.TaskVerificationRequest(true, feedback);

        apiService.verifyTaskCompletion(task.getId(), request)
                .enqueue(new Callback<ApiService.ApiResponseWrapper<Task>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<Task>> call,
                                           @NonNull Response<ApiService.ApiResponseWrapper<Task>> response) {
                        showLoading(false);

                        if (response.isSuccessful() && response.body() != null && response.body().success) {
                            Toast.makeText(requireContext(), "Đã xác nhận hoàn thành!", Toast.LENGTH_SHORT).show();
                            requireActivity().onBackPressed();
                        } else {
                            Toast.makeText(requireContext(), "Không thể xác nhận", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<Task>> call,
                                          @NonNull Throwable t) {
                        showLoading(false);
                        Log.e(TAG, "Approve task error", t);
                        Toast.makeText(requireContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showRejectDialog() {
        // TODO: Show dialog để nhập lý do từ chối
        String reason = "Chưa hoàn thành đúng yêu cầu";
        rejectTask(reason);
    }

    private void rejectTask(String reason) {
        showLoading(true);

        ApiService.TaskRejectionRequest request = new ApiService.TaskRejectionRequest(reason);

        apiService.rejectTaskCompletion(task.getId(), request)
                .enqueue(new Callback<ApiService.ApiResponseWrapper<Task>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<Task>> call,
                                           @NonNull Response<ApiService.ApiResponseWrapper<Task>> response) {
                        showLoading(false);

                        if (response.isSuccessful() && response.body() != null && response.body().success) {
                            Toast.makeText(requireContext(), "Đã từ chối xác nhận", Toast.LENGTH_SHORT).show();
                            requireActivity().onBackPressed();
                        } else {
                            Toast.makeText(requireContext(), "Không thể từ chối", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<Task>> call,
                                          @NonNull Throwable t) {
                        showLoading(false);
                        Log.e(TAG, "Reject task error", t);
                        Toast.makeText(requireContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showFullImage() {
        // TODO: Mở ảnh full screen
        Toast.makeText(requireContext(), "Xem ảnh full screen", Toast.LENGTH_SHORT).show();
    }

    private void playVideo() {
        // TODO: Mở video player
        Toast.makeText(requireContext(), "Phát video", Toast.LENGTH_SHORT).show();
    }

    private String formatDate(String isoDate) {
        if (isoDate == null) return "";
        try {
            java.text.SimpleDateFormat isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
            java.util.Date date = isoFormat.parse(isoDate);
            return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(date);
        } catch (Exception e) {
            return isoDate;
        }
    }

    private void showLoading(boolean show) {
        if (binding == null) return;
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnApprove.setEnabled(!show);
        binding.btnReject.setEnabled(!show);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
