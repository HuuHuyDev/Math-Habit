package com.kidsapp.ui.child.task.tabs;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.R;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.Task;
import com.kidsapp.data.repository.TaskRepository;
import com.kidsapp.databinding.FragmentWorkTabBinding;
import com.kidsapp.ui.child.task.adapter.WorkTaskAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Tab Công việc - Hiển thị tất cả công việc (Việc nhà + Cá nhân)
 * Gộp housework, habit, và custom tasks
 */
public class WorkTabFragment extends Fragment {
    
    private static final String TAG = "WorkTabFragment";
    
    private FragmentWorkTabBinding binding;
    private WorkTaskAdapter adapter;
    private TaskRepository taskRepository;
    private SharedPref sharedPref;
    
    // Current task being completed
    private Task currentTask;
    
    // Camera/Gallery launchers
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<String[]> storagePermissionLauncher;
    private Uri photoUri;
    private boolean isOpeningCamera = false;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkTabBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize
        sharedPref = new SharedPref(requireContext());
        taskRepository = new TaskRepository(requireContext());
        
        setupActivityLaunchers();
        setupRecyclerView();
        loadTasks();
    }
    
    /**
     * Setup Activity Result Launchers cho camera/gallery
     */
    private void setupActivityLaunchers() {
        // Camera launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && photoUri != null) {
                        uploadProof(photoUri, currentTask);
                    }
                }
        );
        
        // Gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            uploadProof(uri, currentTask);
                        }
                    }
                }
        );
        
        // Camera permission launcher
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        if (isOpeningCamera) {
                            // HABIT tasks: mở camera hoặc video tùy user chọn
                            // Mặc định mở camera ảnh
                            openCamera();
                        }
                    } else {
                        Toast.makeText(requireContext(), 
                                "Cần cấp quyền camera để chụp ảnh/quay video", 
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
        
        // Storage permission launcher (for Android 13+)
        storagePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    boolean allGranted = true;
                    for (Boolean granted : permissions.values()) {
                        if (!granted) {
                            allGranted = false;
                            break;
                        }
                    }
                    
                    if (allGranted) {
                        // HABIT: cho phép chọn cả ảnh và video
                        openGallery(true);
                    } else {
                        Toast.makeText(requireContext(), 
                                "Cần cấp quyền truy cập ảnh/video để chọn từ thư viện", 
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
    
    /**
     * Setup RecyclerView để hiển thị danh sách công việc
     */
    private void setupRecyclerView() {
        binding.recyclerTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        adapter = new WorkTaskAdapter();
        adapter.setOnTaskActionListener(new WorkTaskAdapter.OnTaskActionListener() {
            @Override
            public void onTaskClick(Task task) {
                showTaskDetail(task);
            }

            @Override
            public void onCompleteClick(Task task) {
                onTaskComplete(task);
            }
        });
        
        binding.recyclerTasks.setAdapter(adapter);
        
        // Setup SwipeRefreshLayout
        binding.swipeRefresh.setColorSchemeResources(R.color.primary);
        binding.swipeRefresh.setOnRefreshListener(this::loadTasks);
    }
    
    /**
     * Hiển thị chi tiết task
     */
    private void showTaskDetail(Task task) {
        String status = task.getStatus();
        String statusText;
        
        if ("PENDING".equalsIgnoreCase(status)) {
            statusText = "⏳ Chưa hoàn thành";
        } else if ("SUBMITTED".equalsIgnoreCase(status)) {
            statusText = "📤 Đã nộp - Chờ duyệt";
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            statusText = "❌ Bị từ chối - Cần làm lại";
        } else if ("COMPLETED".equalsIgnoreCase(status)) {
            statusText = "✅ Đã hoàn thành";
        } else {
            statusText = status;
        }
        
        String message = "📝 " + task.getDescription() + 
                "\n\n⭐ Điểm thưởng: " + task.getPointsReward() +
                "\n📊 Trạng thái: " + statusText;
        
        if (task.getDueTime() != null && !task.getDueTime().isEmpty()) {
            message += "\n⏰ Hạn: " + task.getDueTime();
        }
        
        new AlertDialog.Builder(requireContext())
                .setTitle(task.getTitle())
                .setMessage(message)
                .setPositiveButton("Đóng", null)
                .show();
    }
    
    /**
     * Load danh sách công việc từ API
     * Chỉ load HABIT tasks (EXERCISE được xử lý ở tab riêng)
     */
    private void loadTasks() {
        showLoading();
        
        // Ưu tiên childId, fallback sang userId nếu chưa có
        String childId = sharedPref.getChildId();
        if (childId == null || childId.isEmpty()) {
            childId = sharedPref.getUserId();
        }
        
        if (childId == null || childId.isEmpty()) {
            Log.e(TAG, "Child ID and User ID not found");
            showError("Không tìm thấy thông tin tài khoản. Vui lòng đăng nhập lại.");
            return;
        }
        
        Log.d(TAG, "Loading HABIT tasks for child: " + childId);
        
        // Lấy ngày hôm nay để filter
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(new java.util.Date());
        
        // Load HABIT tasks của ngày hôm nay
        taskRepository.getTasksByChild(childId, null, "HABIT", today, new TaskRepository.TasksCallback() {
            @Override
            public void onSuccess(List<Task> allTasks) {
                if (!isAdded()) return;
                
                Log.d(TAG, "Loaded " + allTasks.size() + " HABIT tasks");
                
                // Filter tasks cần hiển thị:
                // - PENDING: chưa làm
                // - SUBMITTED: đã nộp, chờ duyệt
                // - REJECTED: bị từ chối, cần làm lại
                // - COMPLETED: đã hoàn thành
                List<Task> workTasks = new ArrayList<>();
                for (Task task : allTasks) {
                    String status = task.getStatus();
                    if (status == null) continue;
                    
                    status = status.toUpperCase();
                    if ("PENDING".equals(status) || 
                        "SUBMITTED".equals(status) || 
                        "REJECTED".equals(status) ||
                        "COMPLETED".equals(status)) {
                        workTasks.add(task);
                    }
                }
                
                Log.d(TAG, "Filtered " + workTasks.size() + " active HABIT tasks");
                
                if (workTasks.isEmpty()) {
                    showEmptyState();
                } else {
                    showTasks(workTasks);
                }
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                Log.e(TAG, "Error loading tasks: " + error);
                showError(error);
            }
        });
    }
    
    private void showLoading() {
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.recyclerTasks.setVisibility(View.GONE);
        binding.progressLoading.setVisibility(View.VISIBLE);
    }
    
    private void showTasks(List<Task> tasks) {
        binding.progressLoading.setVisibility(View.GONE);
        binding.swipeRefresh.setRefreshing(false);
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.recyclerTasks.setVisibility(View.VISIBLE);
        adapter.setTasks(tasks);
    }
    
    private void showEmptyState() {
        binding.progressLoading.setVisibility(View.GONE);
        binding.swipeRefresh.setRefreshing(false);
        binding.layoutEmpty.setVisibility(View.VISIBLE);
        binding.recyclerTasks.setVisibility(View.GONE);
    }
    
    private void showError(String message) {
        binding.progressLoading.setVisibility(View.GONE);
        binding.swipeRefresh.setRefreshing(false);
        showEmptyState();
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Xử lý khi user click hoàn thành công việc
     * HABIT: Cần chụp ảnh/quay video minh chứng
     */
    private void onTaskComplete(Task task) {
        // Kiểm tra status
        String status = task.getStatus();
        if ("SUBMITTED".equalsIgnoreCase(status)) {
            // Đã nộp, đang chờ duyệt
            Toast.makeText(requireContext(), 
                    "Đang chờ phụ huynh duyệt minh chứng", 
                    Toast.LENGTH_SHORT).show();
            return;
        }
        
        currentTask = task;
        
        // Nếu bị từ chối, hiển thị lý do
        if ("REJECTED".equalsIgnoreCase(status)) {
            String reason = "";
            if (task.getActiveProof() != null && task.getActiveProof().getRejectionReason() != null) {
                reason = task.getActiveProof().getRejectionReason();
            }
            
            new AlertDialog.Builder(requireContext())
                    .setTitle("⚠️ Minh chứng bị từ chối")
                    .setMessage("Lý do: " + (reason.isEmpty() ? "Không đạt yêu cầu" : reason) + 
                            "\n\nBạn cần nộp lại minh chứng mới.")
                    .setPositiveButton("Nộp lại", (dialog, which) -> showHabitProofOptions(task))
                    .setNegativeButton("Để sau", null)
                    .show();
        } else {
            // PENDING - cho phép nộp bình thường
            showHabitProofOptions(task);
        }
    }
    
    /**
     * Hiển thị dialog chọn camera hoặc gallery cho HABIT task
     */
    private void showHabitProofOptions(Task task) {
        String[] options = new String[]{
            "📷 Chụp ảnh", 
            "🎥 Quay video", 
            "🖼️ Chọn từ thư viện"
        };
        
        new AlertDialog.Builder(requireContext())
                .setTitle("Nộp minh chứng thói quen")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else if (which == 1) {
                        openVideoCamera();
                    } else {
                        openGallery(true); // Allow both image and video
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    /**
     * Mở camera để chụp ảnh
     */
    private void openCamera() {
        // Check camera permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            isOpeningCamera = true;
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            return;
        }
        
        isOpeningCamera = false;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        
        // Create temp file
        File photoFile = new File(requireContext().getCacheDir(), "photo_" + System.currentTimeMillis() + ".jpg");
        photoUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".fileprovider",
                photoFile
        );
        
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        cameraLauncher.launch(intent);
    }
    
    /**
     * Mở camera để quay video
     */
    private void openVideoCamera() {
        // Check camera permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            isOpeningCamera = true;
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            return;
        }
        
        isOpeningCamera = false;
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        
        // Create temp file
        File videoFile = new File(requireContext().getCacheDir(), "video_" + System.currentTimeMillis() + ".mp4");
        photoUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".fileprovider",
                videoFile
        );
        
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // High quality
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60); // Max 60 seconds
        
        cameraLauncher.launch(intent);
    }
    
    /**
     * Mở gallery để chọn ảnh/video
     */
    private void openGallery(boolean isImage) {
        // Check storage permission for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            String permission = isImage ? Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_MEDIA_VIDEO;
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                storagePermissionLauncher.launch(new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                });
                return;
            }
        } else {
            // For Android 12 and below
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                storagePermissionLauncher.launch(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
                });
                return;
            }
        }
        
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(isImage ? "image/*" : "video/*");
        galleryLauncher.launch(intent);
    }
    
    /**
     * Upload minh chứng HABIT lên server - Sử dụng API mới submitHabitProof
     */
    private void uploadProof(Uri uri, Task task) {
        // Show loading dialog
        com.kidsapp.ui.components.LoadingDialog loadingDialog = new com.kidsapp.ui.components.LoadingDialog(requireContext());
        loadingDialog.show("Đang gửi minh chứng...");
        
        // Submit HABIT proof với API mới
        taskRepository.submitHabitProof(task.getId(), uri, "", 
                new TaskRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                if (!isAdded()) return;
                loadingDialog.dismiss();
                
                // Show success dialog
                new AlertDialog.Builder(requireContext())
                        .setTitle("🎉 Thành công!")
                        .setMessage("Đã gửi minh chứng cho thói quen \"" + task.getTitle() + "\".\n\nChờ phụ huynh duyệt nhé!")
                        .setPositiveButton("OK", null)
                        .show();
                
                // Reload tasks
                loadTasks();
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                loadingDialog.dismiss();
                Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Submit minh chứng qua API (legacy method - không dùng nữa)
     */
    private void submitTaskProof(String taskId, String proofUrl, String proofType, String note) {
        taskRepository.submitTaskProof(taskId, proofUrl, proofType, note, 
                new TaskRepository.TaskCallback() {
            @Override
            public void onSuccess(Task task) {
                Toast.makeText(requireContext(), "Đã gửi minh chứng thành công!", Toast.LENGTH_SHORT).show();
                // Reload tasks
                loadTasks();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    public void refresh() {
        loadTasks();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
