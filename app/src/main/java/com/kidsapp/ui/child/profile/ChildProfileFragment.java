package com.kidsapp.ui.child.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.kidsapp.R;
import com.kidsapp.databinding.FragmentProfileChildBinding;
import com.kidsapp.ui.auth.LoginActivity;
import com.kidsapp.ui.child.equip.equip;

/**
 * Fragment hiển thị thông tin hồ sơ của Bé
 * Bao gồm: Avatar, Tên, Tuổi, Ngày sinh, Giới tính, Lớp học, XP, Coins, Thành tích
 * Có nút Đăng Xuất để thoát tài khoản
 */
public class ChildProfileFragment extends Fragment {
    
    private FragmentProfileChildBinding binding;
    private SharedPreferences sharedPreferences;
    
    // Key để lưu trữ trong SharedPreferences
    private static final String PREF_NAME = "KidsAppPrefs";
    private static final String KEY_CHILD_NAME = "child_name";
    private static final String KEY_CHILD_AGE = "child_age";
    private static final String KEY_CHILD_BIRTHDAY = "child_birthday";
    private static final String KEY_CHILD_GENDER = "child_gender";
    private static final String KEY_CHILD_GRADE = "child_grade";
    private static final String KEY_CHILD_XP = "child_xp";
    private static final String KEY_CHILD_COINS = "child_coins";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileChildBinding.inflate(inflater, container, false);
        
        // Khởi tạo SharedPreferences
        initSharedPreferences();
        
        // Load dữ liệu từ SharedPreferences
        loadChildData();
        
        // Xử lý sự kiện nút Back
        setupBackButton();
        
        // Xử lý sự kiện nút Trang bị
        setupEquipButton();
        
        // Xử lý sự kiện nút Đăng Xuất
        setupLogoutButton();
        
        return binding.getRoot();
    }

    /**
     * Khởi tạo SharedPreferences để lưu/đọc dữ liệu
     */
    private void initSharedPreferences() {
        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, 0);
    }

    /**
     * Load dữ liệu bé từ SharedPreferences và hiển thị lên UI
     */
    private void loadChildData() {
        // Lấy dữ liệu từ SharedPreferences (nếu không có thì dùng giá trị mặc định)
        String name = sharedPreferences.getString(KEY_CHILD_NAME, "Hồ Hữu Huy");
        int age = sharedPreferences.getInt(KEY_CHILD_AGE, 8);
        String birthday = sharedPreferences.getString(KEY_CHILD_BIRTHDAY, "15/03/2016");
        String gender = sharedPreferences.getString(KEY_CHILD_GENDER, "Nam");
        String grade = sharedPreferences.getString(KEY_CHILD_GRADE, "Lớp 3A");
        int xp = sharedPreferences.getInt(KEY_CHILD_XP, 1250);
        int coins = sharedPreferences.getInt(KEY_CHILD_COINS, 850);
        
        // Hiển thị dữ liệu lên UI
        binding.txtChildName.setText(name);
        binding.txtAge.setText(age + " tuổi");
        binding.txtBirthday.setText(birthday);
        binding.txtGender.setText(gender);
        binding.txtGrade.setText(grade);
        binding.txtXP.setText(String.valueOf(xp));
        binding.txtCoins.setText(String.valueOf(coins));
        
        // Đổi icon giới tính
        if (gender.equals("Nữ")) {
            binding.imgGender.setImageResource(R.drawable.ic_user);
            binding.imgGender.setColorFilter(0xFFE91E63); // Màu hồng
        } else {
            binding.imgGender.setImageResource(R.drawable.ic_user);
            binding.imgGender.setColorFilter(0xFF2196F3); // Màu xanh
        }
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
     * 1. Xóa dữ liệu đăng nhập trong SharedPreferences
     * 2. Chuyển về màn đăng nhập
     * 3. Đóng tất cả Activity trước đó (không cho quay lại)
     */
    private void performLogout() {
        // Xóa trạng thái đăng nhập
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
        
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

