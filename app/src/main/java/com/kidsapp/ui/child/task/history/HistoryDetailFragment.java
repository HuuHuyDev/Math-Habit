package com.kidsapp.ui.child.task.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kidsapp.databinding.FragmentHistoryDetailBinding;

/**
 * Fragment hiển thị chi tiết lịch sử nhiệm vụ đã hoàn thành
 */
public class HistoryDetailFragment extends Fragment {

    private FragmentHistoryDetailBinding binding;
    
    // Keys để nhận dữ liệu từ Bundle
    private static final String ARG_TASK_TITLE = "task_title";
    private static final String ARG_COMPLETION_TIME = "completion_time";
    private static final String ARG_COINS = "coins";
    private static final String ARG_XP = "xp";
    private static final String ARG_RATING = "rating";
    private static final String ARG_ICON_RES = "icon_res";

    /**
     * Tạo instance mới của Fragment với dữ liệu nhiệm vụ
     */
    public static HistoryDetailFragment newInstance(String title, String completionTime, 
                                                     int coins, int xp, float rating, int iconRes) {
        HistoryDetailFragment fragment = new HistoryDetailFragment();
        
        Bundle args = new Bundle();
        args.putString(ARG_TASK_TITLE, title);
        args.putString(ARG_COMPLETION_TIME, completionTime);
        args.putInt(ARG_COINS, coins);
        args.putInt(ARG_XP, xp);
        args.putFloat(ARG_RATING, rating);
        args.putInt(ARG_ICON_RES, iconRes);
        
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryDetailBinding.inflate(inflater, container, false);
        
        // Nhận dữ liệu và hiển thị
        loadDataAndDisplay();
        
        // Xử lý nút Back
        setupBackButton();
        
        return binding.getRoot();
    }

    /**
     * Nhận dữ liệu từ Bundle và hiển thị lên UI
     */
    private void loadDataAndDisplay() {
        Bundle args = getArguments();
        
        if (args != null) {
            String title = args.getString(ARG_TASK_TITLE, "");
            String completionTime = args.getString(ARG_COMPLETION_TIME, "");
            int coins = args.getInt(ARG_COINS, 0);
            int xp = args.getInt(ARG_XP, 0);
            float rating = args.getFloat(ARG_RATING, 0f);
            int iconRes = args.getInt(ARG_ICON_RES, 0);
            
            // Hiển thị dữ liệu cơ bản
            binding.txtTaskTitle.setText(title);
            binding.txtCompletionTime.setText(completionTime);
            binding.txtCoins.setText(String.valueOf(coins));
            binding.txtXP.setText("+" + xp);
            
            // Hiển thị icon nếu có
            if (iconRes != 0) {
                binding.imgTaskIcon.setImageResource(iconRes);
            }
            
            // Hiển thị rating nếu có
            if (rating > 0) {
                binding.layoutRating.setVisibility(View.VISIBLE);
                binding.txtRating.setText(String.format("%.1f ⭐", rating));
            } else {
                binding.layoutRating.setVisibility(View.GONE);
            }
            
            // Hiển thị dữ liệu mẫu cho các phần mới
            setupDetailData();
            setupProgressData();
            setupFeedbackData();
        }
    }
    
    /**
     * Thiết lập dữ liệu chi tiết (số câu, thời gian)
     */
    private void setupDetailData() {
        // Dữ liệu mẫu - trong thực tế sẽ nhận từ Bundle
        binding.txtTotalQuestions.setText("10 câu");
        binding.txtCorrectAnswers.setText("9 câu");
        binding.txtWrongAnswers.setText("1 câu");
        binding.txtDuration.setText("5 phút 30 giây");
    }
    
    /**
     * Thiết lập dữ liệu tiến trình
     */
    private void setupProgressData() {
        // Tính % hoàn thành - dữ liệu mẫu
        int progress = 90; // 9/10 = 90%
        binding.progressBar.setProgress(progress);
        binding.txtProgressPercent.setText(progress + "%");
        
        // Hiển thị mô tả dựa trên %
        String description;
        if (progress >= 90) {
            description = "Xuất sắc! Bạn đã làm rất tốt! 🌟";
        } else if (progress >= 70) {
            description = "Tốt lắm! Hãy cố gắng thêm nhé! 👍";
        } else if (progress >= 50) {
            description = "Khá tốt! Tiếp tục rèn luyện! 💪";
        } else {
            description = "Cố gắng lên! Lần sau sẽ tốt hơn! 🎯";
        }
        binding.txtProgressDescription.setText(description);
    }
    
    /**
     * Thiết lập nhận xét động viên
     */
    private void setupFeedbackData() {
        // Nhận xét mẫu - trong thực tế có thể tùy chỉnh theo kết quả
        String feedback = "Tuyệt vời! Bạn đã hoàn thành bài tập với kết quả xuất sắc. " +
                "Hãy tiếp tục cố gắng và rèn luyện thêm nhé! 🌟";
        binding.txtFeedback.setText(feedback);
    }
    

    /**
     * Chuyển sang màn hình xem lại đáp án
     */
    private void navigateToReviewAnswers() {
        // TODO: Cần truyền danh sách câu hỏi và đáp án đã chọn
        // Hiện tại chỉ hiển thị thông báo
        if (getActivity() != null) {
            android.widget.Toast.makeText(requireContext(), 
                "Chức năng xem đáp án đang được phát triển.\nCần truyền dữ liệu câu hỏi từ ExamFragment.", 
                android.widget.Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Xử lý nút Back - Quay về trang lịch sử
     */
    private void setupBackButton() {
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
