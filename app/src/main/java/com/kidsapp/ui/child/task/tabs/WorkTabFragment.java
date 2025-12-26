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

import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.Task;
import com.kidsapp.data.repository.FileUploadRepository;
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
    private FileUploadRepository fileUploadRepository;
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
        fileUploadRepository = new FileUploadRepository(requireContext());
        
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
                            String taskType = currentTask.getTaskType();
                            boolean isHousework = "housework".equals(taskType);
                            if (isHousework) {
                                openCamera();
                            } else {
                                openVideoCamera();
                            }
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
                        String taskType = currentTask.getTaskType();
                        boolean isHousework = "housework".equals(taskType);
                        openGallery(isHousework);
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
                // TODO: Navigate to task detail
                Toast.makeText(requireContext(), "Chi tiết: " + task.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCompleteClick(Task task) {
                onTaskComplete(task);
            }
        });
        
        binding.recyclerTasks.setAdapter(adapter);
    }
    
    /**
     * Load danh sách công việc từ API
     */
    private void loadTasks() {
        showLoading();
        
        String childId = sharedPref.getChildId();
        if (childId == null || childId.isEmpty()) {
            Log.e(TAG, "Child ID not found");
            showError("Không tìm thấy thông tin tài khoản");
            return;
        }
        
        Log.d(TAG, "Loading tasks for child: " + childId);
        
        taskRepository.getTasksByChild(childId, new TaskRepository.TasksCallback() {
            @Override
            public void onSuccess(List<Task> allTasks) {
                Log.d(TAG, "Loaded " + allTasks.size() + " tasks");
                
                // Filter housework, habit, custom với status = pending
                List<Task> workTasks = new ArrayList<>();
                for (Task task : allTasks) {
                    String type = task.getTaskType();
                    String status = task.getStatus();
                    
                    if (("housework".equals(type) || "habit".equals(type) || "custom".equals(type))
                            && "pending".equals(status)) {
                        workTasks.add(task);
                    }
                }
                
                Log.d(TAG, "Filtered " + workTasks.size() + " work tasks");
                
                if (workTasks.isEmpty()) {
                    showEmptyState();
                } else {
                    showTasks(workTasks);
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error loading tasks: " + error);
                showError(error);
            }
        });
    }
    
    private void showLoading() {
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.recyclerTasks.setVisibility(View.GONE);
    }
    
    private void showTasks(List<Task> tasks) {
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.recyclerTasks.setVisibility(View.VISIBLE);
        adapter.setTasks(tasks);
    }
    
    private void showEmptyState() {
        binding.layoutEmpty.setVisibility(View.VISIBLE);
        binding.recyclerTasks.setVisibility(View.GONE);
    }
    
    private void showError(String message) {
        showEmptyState();
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Xử lý khi user click hoàn thành công việc
     */
    private void onTaskComplete(Task task) {
        currentTask = task;
        showProofOptions(task);
    }
    
    /**
     * Hiển thị dialog chọn camera hoặc gallery
     */
    private void showProofOptions(Task task) {
        String taskType = task.getTaskType();
        boolean isHousework = "housework".equals(taskType);
        
        String title = isHousework ? "Chụp ảnh minh chứng" : "Quay video minh chứng";
        String[] options = isHousework 
                ? new String[]{"Chụp ảnh", "Chọn từ thư viện"}
                : new String[]{"Quay video", "Chọn từ thư viện"};
        
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Camera
                        if (isHousework) {
                            openCamera();
                        } else {
                            openVideoCamera();
                        }
                    } else {
                        // Gallery
                        openGallery(isHousework);
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
     * Upload minh chứng lên server - Sử dụng API mới (upload trực tiếp)
     */
    private void uploadProof(Uri uri, Task task) {
        // Show loading
        Toast.makeText(requireContext(), "Đang upload...", Toast.LENGTH_SHORT).show();
        
        // Submit proof với file upload trực tiếp
        taskRepository.submitTaskProofWithFile(task.getId(), uri, "", 
                new TaskRepository.TaskProofCallback() {
            @Override
            public void onSuccess(com.kidsapp.data.api.ApiService.TaskProofResponse proof) {
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
