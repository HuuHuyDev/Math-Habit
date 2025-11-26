package com.kidsapp.ui.parent.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kidsapp.R;
import com.kidsapp.databinding.BottomsheetChoosePhotoBinding;
import com.kidsapp.databinding.FragmentProfileParentBinding;

import java.io.IOException;

/**
 * Parent Profile Fragment - Hồ sơ phụ huynh
 */
public class ParentProfileFragment extends Fragment {
    private FragmentProfileParentBinding binding;
    private BottomSheetDialog photoBottomSheet;

    // Activity Result Launchers
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String> requestStoragePermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivityResultLaunchers();
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
        setupViews();
        setupListeners();
        loadUserData();
    }

    /**
     * Setup Activity Result Launchers
     */
    private void setupActivityResultLaunchers() {
        // Launcher cho chụp ảnh
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            if (imageBitmap != null) {
                                // Hiển thị ảnh lên ImageView với bo tròn
                                com.bumptech.glide.Glide.with(requireContext())
                                        .load(imageBitmap)
                                        .circleCrop()
                                        .into(binding.imgAvatar);
                                binding.imgAvatar.setBackground(null); // Xóa gradient background
                                Toast.makeText(requireContext(), "Đã chụp ảnh thành công", Toast.LENGTH_SHORT).show();
                                // TODO: Upload ảnh lên server
                            }
                        }
                    }
                    if (photoBottomSheet != null) {
                        photoBottomSheet.dismiss();
                    }
                });

        // Launcher cho chọn ảnh từ gallery
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            // Hiển thị ảnh lên ImageView với bo tròn
                            com.bumptech.glide.Glide.with(requireContext())
                                    .load(imageUri)
                                    .circleCrop()
                                    .into(binding.imgAvatar);
                            binding.imgAvatar.setBackground(null); // Xóa gradient background
                            Toast.makeText(requireContext(), "Đã chọn ảnh thành công", Toast.LENGTH_SHORT).show();
                            // TODO: Upload ảnh lên server
                        }
                    }
                    if (photoBottomSheet != null) {
                        photoBottomSheet.dismiss();
                    }
                });

        // Launcher cho xin quyền camera
        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        Toast.makeText(requireContext(),
                                "Cần cấp quyền camera để chụp ảnh", Toast.LENGTH_SHORT).show();
                    }
                });

        // Launcher cho xin quyền storage
        requestStoragePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openGallery();
                    } else {
                        Toast.makeText(requireContext(),
                                "Cần cấp quyền truy cập ảnh", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Setup Views
     */
    private void setupViews() {
        // Set default values
        binding.edtFullName.setText("Nguyễn Phương");
        binding.edtEmail.setText("nguyen.phuong@email.com");
        binding.edtPhone.setText("+84 912 345 678");
        binding.edtPassword.setText("••••••••");
    }

    /**
     * Setup Listeners
     */
    private void setupListeners() {
        // Settings button
        binding.btnSettings.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Cài đặt", Toast.LENGTH_SHORT).show();
        });

        // Camera button
        binding.btnCamera.setOnClickListener(v -> showPhotoBottomSheet());

        // Change avatar button
        binding.btnChangeAvatar.setOnClickListener(v -> showPhotoBottomSheet());

        // Save changes button
        binding.btnSaveChanges.setOnClickListener(v -> saveChanges());

        // Logout button
        binding.btnLogout.setOnClickListener(v -> logout());

        // Switch listeners
        binding.switchHabitReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(requireContext(),
                    "Nhắc làm thói quen: " + (isChecked ? "Bật" : "Tắt"), Toast.LENGTH_SHORT).show();
        });

        binding.switchMathReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(requireContext(),
                    "Nhắc làm bài tập: " + (isChecked ? "Bật" : "Tắt"), Toast.LENGTH_SHORT).show();
        });

        binding.switchWeeklyReport.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(requireContext(),
                    "Báo cáo tuần: " + (isChecked ? "Bật" : "Tắt"), Toast.LENGTH_SHORT).show();
        });

        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(requireContext(),
                    "Dark Mode: " + (isChecked ? "Bật" : "Tắt"), Toast.LENGTH_SHORT).show();
            // TODO: Implement dark mode
        });
    }

    /**
     * Load user data
     */
    private void loadUserData() {
        // TODO: Load từ database/API
        binding.txtParentName.setText("Nguyễn Phương");
        binding.txtParentEmail.setText("nguyen.phuong@email.com");
    }

    /**
     * Show photo bottom sheet
     */
    private void showPhotoBottomSheet() {
        photoBottomSheet = new BottomSheetDialog(requireContext());
        BottomsheetChoosePhotoBinding bottomSheetBinding = BottomsheetChoosePhotoBinding.inflate(
                getLayoutInflater());

        // Take photo
        bottomSheetBinding.btnTakePhoto.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                openCamera();
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        // Choose from gallery
        bottomSheetBinding.btnChooseGallery.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                openGallery();
            } else {
                requestStoragePermission();
            }
        });

        // Cancel
        bottomSheetBinding.btnCancel.setOnClickListener(v -> photoBottomSheet.dismiss());

        photoBottomSheet.setContentView(bottomSheetBinding.getRoot());
        photoBottomSheet.show();
    }

    /**
     * Check camera permission
     */
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check storage permission
     */
    private boolean checkStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ sử dụng READ_MEDIA_IMAGES
            return ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            // Android 12 trở xuống sử dụng READ_EXTERNAL_STORAGE
            return ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * Request storage permission
     */
    private void requestStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestStoragePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    /**
     * Open camera
     */
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            takePictureLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(requireContext(), "Không tìm thấy ứng dụng camera", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Open gallery
     */
    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(pickPhotoIntent);
    }

    /**
     * Save changes
     */
    private void saveChanges() {
        String fullName = binding.edtFullName.getText().toString().trim();
        String email = binding.edtEmail.getText().toString().trim();
        String phone = binding.edtPhone.getText().toString().trim();
        String password = binding.edtPassword.getText().toString().trim();

        if (fullName.isEmpty()) {
            binding.edtFullName.setError("Vui lòng nhập họ tên");
            return;
        }

        if (email.isEmpty()) {
            binding.edtEmail.setError("Vui lòng nhập email");
            return;
        }

        if (phone.isEmpty()) {
            binding.edtPhone.setError("Vui lòng nhập số điện thoại");
            return;
        }

        // TODO: Save to database/API
        Toast.makeText(requireContext(), "Đã lưu thay đổi", Toast.LENGTH_SHORT).show();

        // Update display name
        binding.txtParentName.setText(fullName);
        binding.txtParentEmail.setText(email);
    }

    /**
     * Logout
     */
    private void logout() {
        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    // TODO: Clear session and navigate to login
                    Toast.makeText(requireContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
