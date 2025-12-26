package com.kidsapp.ui.child.task;

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
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.Task;
import com.kidsapp.databinding.FragmentTaskCompletionBinding;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment để trẻ hoàn thành nhiệm vụ với ảnh/video chứng minh
 */
public class TaskCompletionFragment extends Fragment {

    private static final String TAG = "TaskCompletionFragment";
    private static final String ARG_TASK_ID = "task_id";
    private static final String ARG_TASK_TITLE = "task_title";

    private FragmentTaskCompletionBinding binding;
    private ApiService apiService;
    private SharedPref sharedPref;

    private String taskId;
    private String taskTitle;
    private Uri imageUri;
    private Uri videoUri;
    private File photoFile;

    // Activity Result Launchers
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> pickVideoLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    public static TaskCompletionFragment newInstance(String taskId, String taskTitle) {
        TaskCompletionFragment fragment = new TaskCompletionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TASK_ID, taskId);
        args.putString(ARG_TASK_TITLE, taskTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskId = getArguments().getString(ARG_TASK_ID);
            taskTitle = getArguments().getString(ARG_TASK_TITLE);
        }

        setupActivityResultLaunchers();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskCompletionBinding.inflate(inflater, container, false);
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
        binding.txtTaskTitle.setText(taskTitle);
    }

    private void setupListeners() {
        // Back button
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Chụp ảnh
        binding.btnTakePhoto.setOnClickListener(v -> checkPermissionAndTakePhoto());

        // Chọn ảnh từ thư viện
        binding.btnPickImage.setOnClickListener(v -> pickImageFromGallery());

        // Chọn video
        binding.btnPickVideo.setOnClickListener(v -> pickVideoFromGallery());

        // Xóa ảnh
        binding.btnRemoveImage.setOnClickListener(v -> removeImage());

        // Xóa video
        binding.btnRemoveVideo.setOnClickListener(v -> removeVideo());

        // Hoàn thành - tự động chọn API phù hợp
        binding.btnComplete.setOnClickListener(v -> {
            if (imageUri != null || videoUri != null) {
                // Có ảnh/video -> gọi API với proof
                completeTask();
            } else {
                // Không có ảnh/video -> gọi API simple
                completeTaskWithoutProof();
            }
        });

        // Button này đã ẩn, không cần nữa
        binding.btnCompleteWithoutProof.setOnClickListener(v -> completeTaskWithoutProof());
    }

    private void setupActivityResultLaunchers() {
        // Chụp ảnh
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imageUri = Uri.fromFile(photoFile);
                        displayImage();
                    }
                });

        // Chọn ảnh
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        displayImage();
                    }
                });

        // Chọn video
        pickVideoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        videoUri = result.getData().getData();
                        displayVideo();
                    }
                });

        // Request permission
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        takePhoto();
                    } else {
                        Toast.makeText(requireContext(), "Cần quyền camera để chụp ảnh", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkPermissionAndTakePhoto() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            takePhoto();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
                Uri photoURI = FileProvider.getUriForFile(requireContext(),
                        requireContext().getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureLauncher.launch(takePictureIntent);
            } catch (IOException ex) {
                Log.e(TAG, "Error creating image file", ex);
                Toast.makeText(requireContext(), "Không thể tạo file ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "TASK_" + taskId + "_" + System.currentTimeMillis();
        File storageDir = requireContext().getExternalFilesDir(null);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void pickVideoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        pickVideoLauncher.launch(intent);
    }

    private void displayImage() {
        binding.layoutImagePreview.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(imageUri)
                .centerCrop()
                .into(binding.imgProof);
    }

    private void displayVideo() {
        binding.layoutVideoPreview.setVisibility(View.VISIBLE);
        binding.txtVideoName.setText("Video đã chọn");
    }

    private void removeImage() {
        imageUri = null;
        binding.layoutImagePreview.setVisibility(View.GONE);
    }

    private void removeVideo() {
        videoUri = null;
        binding.layoutVideoPreview.setVisibility(View.GONE);
    }

    private void completeTask() {
        String note = binding.edtNote.getText().toString().trim();

        // Không bắt buộc phải có ảnh/video
        // if (imageUri == null && videoUri == null) {
        //     Toast.makeText(requireContext(), "Vui lòng thêm ảnh hoặc video chứng minh", Toast.LENGTH_SHORT).show();
        //     return;
        // }

        showLoading(true);

        try {
            // Prepare multipart parts
            MultipartBody.Part imagePart = null;
            MultipartBody.Part videoPart = null;

            if (imageUri != null) {
                File imageFile = getFileFromUri(imageUri);
                if (imageFile != null) {
                    RequestBody imageBody = RequestBody.create(
                            MediaType.parse("image/*"),
                            imageFile
                    );
                    imagePart = MultipartBody.Part.createFormData("proofImage", imageFile.getName(), imageBody);
                }
            }

            if (videoUri != null) {
                File videoFile = getFileFromUri(videoUri);
                if (videoFile != null) {
                    RequestBody videoBody = RequestBody.create(
                            MediaType.parse("video/*"),
                            videoFile
                    );
                    videoPart = MultipartBody.Part.createFormData("proofVideo", videoFile.getName(), videoBody);
                }
            }

            RequestBody noteBody = RequestBody.create(MediaType.parse("text/plain"), note);

            apiService.completeTaskWithProof(taskId, imagePart, videoPart, noteBody)
                    .enqueue(new Callback<ApiService.ApiResponseWrapper<Task>>() {
                        @Override
                        public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<Task>> call,
                                               @NonNull Response<ApiService.ApiResponseWrapper<Task>> response) {
                            showLoading(false);

                            if (response.isSuccessful() && response.body() != null && response.body().success) {
                                String message = (imageUri != null || videoUri != null) 
                                    ? "Hoàn thành nhiệm vụ! Chờ phụ huynh xác nhận" 
                                    : "Hoàn thành nhiệm vụ thành công!";
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                                requireActivity().onBackPressed();
                            } else {
                                Toast.makeText(requireContext(), "Không thể hoàn thành nhiệm vụ", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<Task>> call,
                                              @NonNull Throwable t) {
                            showLoading(false);
                            Log.e(TAG, "Complete task error", t);
                            Toast.makeText(requireContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            showLoading(false);
            Log.e(TAG, "Error preparing multipart", e);
            Toast.makeText(requireContext(), "Lỗi xử lý file", Toast.LENGTH_SHORT).show();
        }
    }

    private File getFileFromUri(Uri uri) {
        if (uri == null) return null;
        
        // Nếu là file:// URI
        if ("file".equals(uri.getScheme())) {
            return new File(uri.getPath());
        }
        
        // Nếu là content:// URI, copy sang cache
        try {
            java.io.InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;
            
            File tempFile = File.createTempFile("upload_", ".tmp", requireContext().getCacheDir());
            java.io.FileOutputStream outputStream = new java.io.FileOutputStream(tempFile);
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            outputStream.close();
            inputStream.close();
            
            return tempFile;
        } catch (Exception e) {
            Log.e(TAG, "Error getting file from URI", e);
            return null;
        }
    }

    private void completeTaskWithoutProof() {
        String note = binding.edtNote.getText().toString().trim();

        showLoading(true);

        ApiService.TaskCompletionRequest request = new ApiService.TaskCompletionRequest(note);

        apiService.completeTaskSimple(taskId, request)
                .enqueue(new Callback<ApiService.ApiResponseWrapper<Task>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiService.ApiResponseWrapper<Task>> call,
                                           @NonNull Response<ApiService.ApiResponseWrapper<Task>> response) {
                        showLoading(false);

                        if (response.isSuccessful() && response.body() != null && response.body().success) {
                            Toast.makeText(requireContext(), "Hoàn thành nhiệm vụ!", Toast.LENGTH_SHORT).show();
                            requireActivity().onBackPressed();
                        } else {
                            Toast.makeText(requireContext(), "Không thể hoàn thành nhiệm vụ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiService.ApiResponseWrapper<Task>> call,
                                          @NonNull Throwable t) {
                        showLoading(false);
                        Log.e(TAG, "Complete task error", t);
                        Toast.makeText(requireContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showLoading(boolean show) {
        if (binding == null) return;
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnComplete.setEnabled(!show);
        binding.btnCompleteWithoutProof.setEnabled(!show);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
