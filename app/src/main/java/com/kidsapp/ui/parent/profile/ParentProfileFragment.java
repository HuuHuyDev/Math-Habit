package com.kidsapp.ui.parent.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.databinding.BottomsheetChoosePhotoBinding;
import com.kidsapp.databinding.FragmentProfileParentBinding;
import com.kidsapp.ui.auth.LoginActivity;
import com.kidsapp.ui.components.LoadingDialog;
import com.kidsapp.viewmodel.AuthViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Parent Profile Fragment - Hồ sơ phụ huynh
 */
public class ParentProfileFragment extends Fragment {
    private static final String TAG = "ParentProfileFragment";
    private FragmentProfileParentBinding binding;
    private BottomSheetDialog photoBottomSheet;
    private AuthViewModel authViewModel;
    private ApiService apiService;
    private ApiService.ParentProfileResponse currentProfile;
    private LoadingDialog loadingDialog;
    
    // Lưu Uri ảnh đã chọn để upload
    private Uri selectedImageUri = null;

    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String> requestStoragePermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivityResultLaunchers();
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        
        SharedPref sharedPref = new SharedPref(requireContext());
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileParentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingDialog = new LoadingDialog(requireContext());
        setupListeners();
        showLoading();
        loadProfile();
    }
    
    private void showLoading() {
        if (loadingDialog != null) {
            loadingDialog.show("Đang tải...");
        }
    }
    
    private void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    private void setupActivityResultLaunchers() {
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            if (imageBitmap != null) {
                                // Lưu bitmap thành file tạm và upload
                                Uri tempUri = saveBitmapToTempFile(imageBitmap);
                                if (tempUri != null) {
                                    selectedImageUri = tempUri;
                                    uploadAvatarAndUpdate();
                                }
                                Glide.with(requireContext())
                                        .load(imageBitmap)
                                        .circleCrop()
                                        .into(binding.imgAvatar);
                            }
                        }
                    }
                    if (photoBottomSheet != null) photoBottomSheet.dismiss();
                });

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            selectedImageUri = imageUri;
                            Glide.with(requireContext())
                                    .load(imageUri)
                                    .circleCrop()
                                    .into(binding.imgAvatar);
                            // Upload ngay khi chọn ảnh
                            uploadAvatarAndUpdate();
                        }
                    }
                    if (photoBottomSheet != null) photoBottomSheet.dismiss();
                });

        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) openCamera();
                    else Toast.makeText(requireContext(), "Cần cấp quyền camera", Toast.LENGTH_SHORT).show();
                });

        requestStoragePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) openGallery();
                    else Toast.makeText(requireContext(), "Cần cấp quyền truy cập ảnh", Toast.LENGTH_SHORT).show();
                });
    }
    
    /**
     * Lưu Bitmap thành file tạm
     */
    private Uri saveBitmapToTempFile(Bitmap bitmap) {
        try {
            File tempFile = File.createTempFile("avatar_", ".jpg", requireContext().getCacheDir());
            FileOutputStream fos = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            return Uri.fromFile(tempFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Upload avatar trực tiếp qua API PUT /users/me/avatar
     */
    private void uploadAvatarAndUpdate() {
        if (selectedImageUri == null) return;
        
        Toast.makeText(requireContext(), "Đang tải ảnh lên...", Toast.LENGTH_SHORT).show();
        
        try {
            File file = getFileFromUri(selectedImageUri);
            if (file == null || !file.exists()) {
                Toast.makeText(requireContext(), "Không thể đọc file ảnh", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Xác định MediaType dựa trên extension
            String mimeType = getMimeType(file.getName());
            Log.d(TAG, "Uploading file: " + file.getName() + ", size: " + file.length() + ", mimeType: " + mimeType);
            
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            
            // Gọi API mới PUT /users/me/avatar - upload và cập nhật avatar trong 1 request
            apiService.updateAvatar(body).enqueue(new Callback<ApiService.ApiResponseWrapper<ApiService.UserResponse>>() {
                @Override
                public void onResponse(Call<ApiService.ApiResponseWrapper<ApiService.UserResponse>> call,
                                       Response<ApiService.ApiResponseWrapper<ApiService.UserResponse>> response) {
                    if (!isAdded()) return;
                    
                    Log.d(TAG, "Upload response code: " + response.code());
                    
                    if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                        // Cập nhật avatar URL từ response
                        String newAvatarUrl = response.body().data.avatarUrl;
                        Log.d(TAG, "New avatar URL: " + newAvatarUrl);
                        if (currentProfile != null && newAvatarUrl != null) {
                            currentProfile.avatarUrl = newAvatarUrl;
                        }
                        Toast.makeText(requireContext(), "Đã cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
                        selectedImageUri = null;
                    } else {
                        // Log chi tiết lỗi từ server
                        String errorMsg = "Không thể tải ảnh lên";
                        if (response.errorBody() != null) {
                            try {
                                errorMsg = response.errorBody().string();
                                Log.e(TAG, "Upload error: " + errorMsg);
                            } catch (Exception ignored) {}
                        }
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiService.ApiResponseWrapper<ApiService.UserResponse>> call, Throwable t) {
                    if (!isAdded()) return;
                    Log.e(TAG, "Upload failed: " + t.getMessage(), t);
                    Toast.makeText(requireContext(), "Lỗi tải ảnh: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Upload exception: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Lấy MIME type từ tên file
     */
    private String getMimeType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "jpg":
            case "jpeg":
            default:
                return "image/jpeg";
        }
    }
    
    /**
     * Lấy File từ Uri
     */
    private File getFileFromUri(Uri uri) {
        try {
            if ("file".equals(uri.getScheme())) {
                return new File(uri.getPath());
            }
            
            // Content URI - copy to temp file
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;
            
            // Lấy extension từ MIME type
            String mimeType = requireContext().getContentResolver().getType(uri);
            String extension = ".jpg"; // default
            if (mimeType != null) {
                if (mimeType.contains("png")) extension = ".png";
                else if (mimeType.contains("gif")) extension = ".gif";
                else if (mimeType.contains("webp")) extension = ".webp";
            }
            
            File tempFile = File.createTempFile("upload_", extension, requireContext().getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            inputStream.close();
            outputStream.close();
            
            Log.d(TAG, "Created temp file: " + tempFile.getAbsolutePath() + ", size: " + tempFile.length());
            
            return tempFile;
        } catch (Exception e) {
            Log.e(TAG, "getFileFromUri error: " + e.getMessage(), e);
            return null;
        }
    }

    private void loadProfile() {
        apiService.getParentProfileApi().enqueue(new Callback<ApiService.ApiResponseWrapper<ApiService.ParentProfileResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<ApiService.ParentProfileResponse>> call,
                                   Response<ApiService.ApiResponseWrapper<ApiService.ParentProfileResponse>> response) {
                if (!isAdded()) return;
                hideLoading();
                
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    currentProfile = response.body().data;
                    updateUI(currentProfile);
                } else {
                    Toast.makeText(requireContext(), "Không thể tải thông tin", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<ApiService.ParentProfileResponse>> call, Throwable t) {
                if (!isAdded()) return;
                hideLoading();
                Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateUI(ApiService.ParentProfileResponse profile) {
        binding.txtParentName.setText(profile.fullName != null ? profile.fullName : "");
        binding.txtParentEmail.setText(profile.email != null ? profile.email : "");
        
        if (profile.avatarUrl != null && !profile.avatarUrl.isEmpty()) {
            Glide.with(requireContext())
                    .load(profile.avatarUrl)
                    .placeholder(R.drawable.ic_avatar_default)
                    .error(R.drawable.ic_avatar_default)
                    .circleCrop()
                    .into(binding.imgAvatar);
        }
        
        binding.edtFullName.setText(profile.fullName != null ? profile.fullName : "");
        binding.edtEmail.setText(profile.email != null ? profile.email : "");
        binding.edtPhone.setText(profile.phone != null ? profile.phone : "");
        binding.edtPassword.setText("••••••••");
        
        if (profile.notificationEnabled != null) {
            binding.switchHabitReminder.setChecked(profile.notificationEnabled);
            binding.switchMathReminder.setChecked(profile.notificationEnabled);
        }
        if (profile.dailyReportEnabled != null) {
            binding.switchWeeklyReport.setChecked(profile.dailyReportEnabled);
        }
    }

    private void setupListeners() {
        binding.btnSettings.setOnClickListener(v -> Toast.makeText(requireContext(), "Cài đặt", Toast.LENGTH_SHORT).show());
        binding.btnCamera.setOnClickListener(v -> showPhotoBottomSheet());
        binding.btnChangeAvatar.setOnClickListener(v -> showPhotoBottomSheet());
        binding.btnSaveChanges.setOnClickListener(v -> saveChanges());
        binding.btnLogout.setOnClickListener(v -> logout());
    }

    private void showPhotoBottomSheet() {
        photoBottomSheet = new BottomSheetDialog(requireContext());
        BottomsheetChoosePhotoBinding bottomSheetBinding = BottomsheetChoosePhotoBinding.inflate(getLayoutInflater());

        bottomSheetBinding.btnTakePhoto.setOnClickListener(v -> {
            if (checkCameraPermission()) openCamera();
            else requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        });

        bottomSheetBinding.btnChooseGallery.setOnClickListener(v -> {
            if (checkStoragePermission()) openGallery();
            else requestStoragePermission();
        });

        bottomSheetBinding.btnCancel.setOnClickListener(v -> photoBottomSheet.dismiss());
        photoBottomSheet.setContentView(bottomSheetBinding.getRoot());
        photoBottomSheet.show();
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        }
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestStoragePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            takePictureLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(requireContext(), "Không tìm thấy ứng dụng camera", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(pickPhotoIntent);
    }

    private void saveChanges() {
        String fullName = binding.edtFullName.getText().toString().trim();
        String phone = binding.edtPhone.getText().toString().trim();
        String password = binding.edtPassword.getText().toString().trim();

        if (fullName.isEmpty()) {
            binding.edtFullName.setError("Vui lòng nhập họ tên");
            return;
        }

        String newPassword = null;
        if (!password.equals("••••••••") && !password.isEmpty()) {
            newPassword = password;
        }
        
        boolean notificationEnabled = binding.switchHabitReminder.isChecked() || binding.switchMathReminder.isChecked();
        boolean dailyReportEnabled = binding.switchWeeklyReport.isChecked();
        
        ApiService.UpdateParentProfileRequest request = new ApiService.UpdateParentProfileRequest(
                fullName, phone, null, null, null, notificationEnabled, dailyReportEnabled, newPassword);
        
        apiService.updateParentProfile(request).enqueue(new Callback<ApiService.ApiResponseWrapper<ApiService.ParentProfileResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<ApiService.ParentProfileResponse>> call,
                                   Response<ApiService.ApiResponseWrapper<ApiService.ParentProfileResponse>> response) {
                if (!isAdded()) return;
                
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    currentProfile = response.body().data;
                    updateUI(currentProfile);
                    Toast.makeText(requireContext(), "Đã lưu thay đổi", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Không thể lưu thay đổi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<ApiService.ParentProfileResponse>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    try { authViewModel.logout(); } catch (Exception ignored) {}
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideLoading();
        loadingDialog = null;
        binding = null;
    }
}
