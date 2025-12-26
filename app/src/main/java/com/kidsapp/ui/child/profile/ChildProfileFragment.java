package com.kidsapp.ui.child.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kidsapp.R;
import com.kidsapp.data.model.Child;
import com.kidsapp.data.repository.ChildRepository;
import com.kidsapp.databinding.FragmentProfileChildBinding;
import com.kidsapp.ui.auth.LoginActivity;
import com.kidsapp.ui.child.equip.equip;
import com.kidsapp.viewmodel.AuthViewModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Fragment hiển thị thông tin hồ sơ của Bé
 * Bao gồm: Avatar, Tên, Tuổi, Ngày sinh, Giới tính, Lớp học, XP, Coins, Thành tích
 * Có nút Đăng Xuất để thoát tài khoản
 */
public class ChildProfileFragment extends Fragment {
    
    private FragmentProfileChildBinding binding;
    private AuthViewModel authViewModel;
    private ChildRepository childRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileChildBinding.inflate(inflater, container, false);
        
        // Khởi tạo ViewModel và Repository
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        childRepository = new ChildRepository(requireContext());
        
        // Load dữ liệu từ API
        loadChildProfile();
        
        // Xử lý sự kiện nút Back
        setupBackButton();
        
        // Xử lý sự kiện nút Trang bị
        setupEquipButton();
        
        // Xử lý sự kiện nút Đăng Xuất
        setupLogoutButton();
        
        return binding.getRoot();
    }

    /**
     * Load dữ liệu profile từ API (dựa trên token trong Redis)
     */
    private void loadChildProfile() {
        // Hiển thị loading (optional)
        // binding.progressBar.setVisibility(View.VISIBLE);
        
        childRepository.getMyProfile(new ChildRepository.ChildCallback() {
            @Override
            public void onSuccess(Child child) {
                if (getActivity() == null) return;
                
                // Ẩn loading
                // binding.progressBar.setVisibility(View.GONE);
                
                // Hiển thị dữ liệu lên UI
                displayChildData(child);
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;
                
                // Ẩn loading
                // binding.progressBar.setVisibility(View.GONE);
                
                // Hiển thị thông báo lỗi
                Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                
                // Load dữ liệu mặc định hoặc từ cache
                loadDefaultData();
            }
        });
    }

    /**
     * Hiển thị dữ liệu child lên UI
     */
    private void displayChildData(Child child) {
        // Tên (ưu tiên nickname)
        String displayName = child.getDisplayName();
        binding.txtChildName.setText(displayName);
        
        // Level
        binding.txtLevel.setText("Level " + child.getLevel());
        
        // XP (totalPoints)
        binding.txtXP.setText(String.valueOf(child.getTotalPoints()));
        
        // Coins - Tạm thời dùng totalPoints / 2 (hoặc có thể thêm field coins vào backend)
        int coins = child.getTotalPoints() / 2;
        binding.txtCoins.setText(String.valueOf(coins));
        
        // Tuổi
        int age = child.getAge();
        binding.txtAge.setText(age > 0 ? age + " tuổi" : "Chưa cập nhật");
        
        // Ngày sinh
        String birthDate = formatBirthDate(child.getBirthDate());
        binding.txtBirthday.setText(birthDate);
        
        // Giới tính - Tạm thời ẩn vì backend chưa có field này
        binding.txtGender.setText("Chưa cập nhật");
        
        // Lớp học
        Integer grade = child.getGrade();
        if (grade != null && grade > 0) {
            binding.txtGrade.setText("Lớp " + grade);
        } else {
            binding.txtGrade.setText("Chưa cập nhật");
        }
        
        // TODO: Load avatar từ URL nếu có
        // if (child.getAvatarUrl() != null && !child.getAvatarUrl().isEmpty()) {
        //     Glide.with(this).load(child.getAvatarUrl()).into(binding.imgAvatar);
        // }
    }

    /**
     * Format ngày sinh từ yyyy-MM-dd sang dd/MM/yyyy
     */
    private String formatBirthDate(String birthDate) {
        if (birthDate == null || birthDate.isEmpty()) {
            return "Chưa cập nhật";
        }
        
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return outputFormat.format(inputFormat.parse(birthDate));
        } catch (Exception e) {
            return birthDate;
        }
    }

    /**
     * Load dữ liệu mặc định khi API lỗi
     */
    private void loadDefaultData() {
        binding.txtChildName.setText("Bé yêu");
        binding.txtLevel.setText("Level 1");
        binding.txtXP.setText("0");
        binding.txtCoins.setText("0");
        binding.txtAge.setText("Chưa cập nhật");
        binding.txtBirthday.setText("Chưa cập nhật");
        binding.txtGender.setText("Chưa cập nhật");
        binding.txtGrade.setText("Chưa cập nhật");
    }

    /**
     * Xử lý sự kiện nút Back - Quay về trang chủ
     */
    private void setupBackButton() {
        binding.btnBack.setOnClickListener(v -> {
            // Quay lại Fragment trước đó (Home)
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    /**
     * Xử lý sự kiện nút Trang bị - Chuyển đến equip fragment
     */
    private void setupEquipButton() {
        binding.btnEquip.setOnClickListener(v -> {
            // Thêm haptic feedback
            try {
                android.os.Vibrator vibrator = (android.os.Vibrator) requireContext().getSystemService(android.content.Context.VIBRATOR_SERVICE);
                if (vibrator != null && vibrator.hasVibrator()) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        vibrator.vibrate(android.os.VibrationEffect.createOneShot(30, android.os.VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(30);
                    }
                }
            } catch (Exception e) {
                // Ignore vibration errors
            }
            
            // Navigate to equip fragment
            if (getActivity() != null) {
                equip equipFragment = new equip();
                
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,  // enter animation
                                R.anim.slide_out_left,  // exit animation
                                R.anim.slide_in_left,   // pop enter animation
                                R.anim.slide_out_right  // pop exit animation
                        )
                        .replace(R.id.childHomeHost, equipFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    /**
     * Xử lý sự kiện nút Đăng Xuất
     */
    private void setupLogoutButton() {
        binding.btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    /**
     * Hiển thị dialog xác nhận đăng xuất
     */
    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Đăng Xuất")
                .setMessage("Bé có chắc muốn đăng xuất không?")
                .setPositiveButton("Đăng Xuất", (dialog, which) -> {
                    // Thực hiện đăng xuất
                    performLogout();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Thực hiện đăng xuất:
     * 1. Gọi API logout và xóa dữ liệu đăng nhập
     * 2. Chuyển về màn đăng nhập
     * 3. Đóng tất cả Activity trước đó (không cho quay lại)
     */
    private void performLogout() {
        // Gọi logout qua ViewModel (sẽ gọi API và clear SharedPref)
        try {
            authViewModel.logout();
        } catch (Exception ignored) {}
        
        // Hiển thị thông báo
        Toast.makeText(requireContext(), "Đã đăng xuất!", Toast.LENGTH_SHORT).show();
        
        // Chuyển về màn đăng nhập
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        
        // Clear tất cả Activity trước đó (không cho quay lại)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        startActivity(intent);
        
        // Đóng Activity hiện tại
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
