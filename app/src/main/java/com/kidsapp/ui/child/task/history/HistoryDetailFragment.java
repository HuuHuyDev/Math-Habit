package com.kidsapp.ui.child.task.history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kidsapp.data.repository.TaskRepository;
import com.kidsapp.data.response.TaskHistoryDetailResponse;
import com.kidsapp.databinding.FragmentHistoryDetailBinding;

/**
 * Fragment hiển thị chi tiết lịch sử nhiệm vụ đã hoàn thành
 */
public class HistoryDetailFragment extends Fragment {

    private static final String TAG = "HistoryDetailFragment";
    private FragmentHistoryDetailBinding binding;
    private TaskRepository taskRepository;
    
    // Keys để nhận dữ liệu từ Bundle
    private static final String ARG_TASK_ID = "task_id";
    private static final String ARG_TASK_TITLE = "task_title";
    private static final String ARG_COMPLETION_TIME = "completion_time";
    private static final String ARG_COINS = "coins";
    private static final String ARG_XP = "xp";
    private static final String ARG_RATING = "rating";
    private static final String ARG_ICON_RES = "icon_res";
    private static final String ARG_TASK_TYPE = "task_type";
    private static final String ARG_TOTAL_QUESTIONS = "total_questions";
    private static final String ARG_CORRECT_ANSWERS = "correct_answers";
    private static final String ARG_WRONG_ANSWERS = "wrong_answers";
    private static final String ARG_DURATION_SECONDS = "duration_seconds";
    private static final String ARG_SCORE = "score";

    /**
     * Tạo instance mới của Fragment với dữ liệu nhiệm vụ (full params)
     */
    public static HistoryDetailFragment newInstance(String taskId, String title, String completionTime, 
                                                     int coins, int xp, float rating, int iconRes,
                                                     String taskType, int totalQuestions, 
                                                     int correctAnswers, int wrongAnswers,
                                                     long durationSeconds, int score) {
        HistoryDetailFragment fragment = new HistoryDetailFragment();
        
        Bundle args = new Bundle();
        args.putString(ARG_TASK_ID, taskId);
        args.putString(ARG_TASK_TITLE, title);
        args.putString(ARG_COMPLETION_TIME, completionTime);
        args.putInt(ARG_COINS, coins);
        args.putInt(ARG_XP, xp);
        args.putFloat(ARG_RATING, rating);
        args.putInt(ARG_ICON_RES, iconRes);
        args.putString(ARG_TASK_TYPE, taskType);
        args.putInt(ARG_TOTAL_QUESTIONS, totalQuestions);
        args.putInt(ARG_CORRECT_ANSWERS, correctAnswers);
        args.putInt(ARG_WRONG_ANSWERS, wrongAnswers);
        args.putLong(ARG_DURATION_SECONDS, durationSeconds);
        args.putInt(ARG_SCORE, score);
        
        fragment.setArguments(args);
        return fragment;
    }
    
    /**
     * Backward compatible constructor (basic params only)
     */
    public static HistoryDetailFragment newInstance(String title, String completionTime, 
                                                     int coins, int xp, float rating, int iconRes) {
        return newInstance(null, title, completionTime, coins, xp, rating, iconRes, 
                          "HABIT", 0, 0, 0, 0, 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryDetailBinding.inflate(inflater, container, false);
        
        // Init repository
        taskRepository = new TaskRepository(requireContext());
        
        // Nhận dữ liệu và hiển thị
        loadDataAndDisplay();
        
        // Xử lý nút Back
        setupBackButton();
        
        return binding.getRoot();
    }

    /**
     * Nhận dữ liệu từ Bundle và hiển thị lên UI
     * Nếu có taskId, gọi API để lấy dữ liệu thực
     */
    private void loadDataAndDisplay() {
        Bundle args = getArguments();
        
        if (args != null) {
            String taskId = args.getString(ARG_TASK_ID);
            String title = args.getString(ARG_TASK_TITLE, "");
            String completionTime = args.getString(ARG_COMPLETION_TIME, "");
            int coins = args.getInt(ARG_COINS, 0);
            int xp = args.getInt(ARG_XP, 0);
            float rating = args.getFloat(ARG_RATING, 0f);
            int iconRes = args.getInt(ARG_ICON_RES, 0);
            String taskType = args.getString(ARG_TASK_TYPE, "HABIT");
            
            // Hiển thị dữ liệu cơ bản từ Bundle trước
            binding.txtTaskTitle.setText(title);
            binding.txtCompletionTime.setText(completionTime);
            binding.txtCoins.setText(String.valueOf(coins));
            binding.txtXP.setText("+" + xp);
            
            if (iconRes != 0) {
                binding.imgTaskIcon.setImageResource(iconRes);
            }
            
            if (rating > 0) {
                binding.layoutRating.setVisibility(View.VISIBLE);
                binding.txtRating.setText(String.format("%.1f ⭐", rating));
            } else {
                binding.layoutRating.setVisibility(View.GONE);
            }
            
            // Nếu có taskId và là EXERCISE, gọi API để lấy dữ liệu chi tiết thực
            if (taskId != null && !taskId.isEmpty() && "EXERCISE".equalsIgnoreCase(taskType)) {
                loadHistoryDetailFromApi(taskId);
            } else {
                // Fallback: dùng dữ liệu từ Bundle
                int totalQuestions = args.getInt(ARG_TOTAL_QUESTIONS, 0);
                int correctAnswers = args.getInt(ARG_CORRECT_ANSWERS, 0);
                int wrongAnswers = args.getInt(ARG_WRONG_ANSWERS, 0);
                long durationSeconds = args.getLong(ARG_DURATION_SECONDS, 0);
                int score = args.getInt(ARG_SCORE, 0);
                
                boolean isExercise = "EXERCISE".equalsIgnoreCase(taskType);
                setupDetailData(isExercise, totalQuestions, correctAnswers, wrongAnswers, durationSeconds);
                setupProgressData(isExercise, totalQuestions, correctAnswers, score);
                setupFeedbackData(isExercise, score, correctAnswers, totalQuestions);
            }
        }
    }
    
    /**
     * Gọi API để lấy chi tiết lịch sử làm bài thực
     */
    private void loadHistoryDetailFromApi(String taskId) {
        Log.d(TAG, "Loading history detail from API for taskId: " + taskId);
        
        taskRepository.getTaskHistoryDetail(taskId, new TaskRepository.TaskHistoryDetailCallback() {
            @Override
            public void onSuccess(TaskHistoryDetailResponse detail) {
                if (!isAdded() || binding == null) return;
                
                Log.d(TAG, "Loaded history detail: totalQuestions=" + detail.getTotalQuestions() 
                        + ", correctAnswers=" + detail.getCorrectAnswers()
                        + ", durationSeconds=" + detail.getDurationSeconds());
                
                // Cập nhật UI với dữ liệu thực từ API
                binding.txtCoins.setText(String.valueOf(detail.getCoinsEarned()));
                binding.txtXP.setText("+" + detail.getXpEarned());
                
                boolean isExercise = "EXERCISE".equalsIgnoreCase(detail.getTaskType());
                setupDetailData(isExercise, detail.getTotalQuestions(), detail.getCorrectAnswers(), 
                               detail.getWrongAnswers(), detail.getDurationSeconds());
                setupProgressData(isExercise, detail.getTotalQuestions(), detail.getCorrectAnswers(), 
                                 detail.getScore());
                setupFeedbackData(isExercise, detail.getScore(), detail.getCorrectAnswers(), 
                                 detail.getTotalQuestions());
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                Log.e(TAG, "Error loading history detail: " + error);
                // Fallback: dùng dữ liệu từ Bundle
                Bundle args = getArguments();
                if (args != null) {
                    String taskType = args.getString(ARG_TASK_TYPE, "HABIT");
                    int totalQuestions = args.getInt(ARG_TOTAL_QUESTIONS, 0);
                    int correctAnswers = args.getInt(ARG_CORRECT_ANSWERS, 0);
                    int wrongAnswers = args.getInt(ARG_WRONG_ANSWERS, 0);
                    long durationSeconds = args.getLong(ARG_DURATION_SECONDS, 0);
                    int score = args.getInt(ARG_SCORE, 0);
                    
                    boolean isExercise = "EXERCISE".equalsIgnoreCase(taskType);
                    setupDetailData(isExercise, totalQuestions, correctAnswers, wrongAnswers, durationSeconds);
                    setupProgressData(isExercise, totalQuestions, correctAnswers, score);
                    setupFeedbackData(isExercise, score, correctAnswers, totalQuestions);
                }
            }
        });
    }
    
    /**
     * Thiết lập dữ liệu chi tiết (số câu, thời gian)
     */
    private void setupDetailData(boolean isExercise, int totalQuestions, int correctAnswers, 
                                  int wrongAnswers, long durationSeconds) {
        if (isExercise && totalQuestions > 0) {
            // Show exercise details
            binding.cardDetails.setVisibility(View.VISIBLE);
            binding.txtTotalQuestions.setText(totalQuestions + " câu");
            binding.txtCorrectAnswers.setText(correctAnswers + " câu");
            binding.txtWrongAnswers.setText(wrongAnswers + " câu");
            
            // Format duration
            if (durationSeconds > 0) {
                binding.txtDuration.setText(formatDuration(durationSeconds));
            } else {
                binding.txtDuration.setText("N/A");
            }
        } else {
            // Hide details card for HABIT tasks
            binding.cardDetails.setVisibility(View.GONE);
        }
    }
    
    /**
     * Format duration seconds to readable string
     */
    private String formatDuration(long seconds) {
        if (seconds <= 0) return "N/A";
        
        long minutes = seconds / 60;
        long secs = seconds % 60;
        
        if (minutes > 0 && secs > 0) {
            return minutes + " phút " + secs + " giây";
        } else if (minutes > 0) {
            return minutes + " phút";
        } else {
            return secs + " giây";
        }
    }
    
    /**
     * Thiết lập dữ liệu tiến trình
     */
    private void setupProgressData(boolean isExercise, int totalQuestions, int correctAnswers, int score) {
        if (isExercise && totalQuestions > 0) {
            binding.cardProgress.setVisibility(View.VISIBLE);
            
            // Calculate progress percentage
            int progress = (correctAnswers * 100) / totalQuestions;
            binding.progressBar.setProgress(progress);
            binding.txtProgressPercent.setText(progress + "%");
            
            // Description based on progress
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
        } else {
            // For HABIT tasks, show 100% completion
            binding.cardProgress.setVisibility(View.VISIBLE);
            binding.progressBar.setProgress(100);
            binding.txtProgressPercent.setText("100%");
            binding.txtProgressDescription.setText("Hoàn thành xuất sắc! 🌟");
        }
    }
    
    /**
     * Thiết lập nhận xét động viên
     */
    private void setupFeedbackData(boolean isExercise, int score, int correctAnswers, int totalQuestions) {
        String feedback;
        
        if (isExercise && totalQuestions > 0) {
            int progress = (correctAnswers * 100) / totalQuestions;
            
            if (progress == 100) {
                feedback = "Tuyệt vời! Bạn đã trả lời đúng tất cả các câu hỏi! " +
                        "Hãy tiếp tục phát huy nhé! 🏆";
            } else if (progress >= 80) {
                feedback = "Rất tốt! Bạn đã hoàn thành bài tập với kết quả xuất sắc. " +
                        "Chỉ còn một chút nữa là hoàn hảo! 🌟";
            } else if (progress >= 60) {
                feedback = "Khá tốt! Bạn đã nắm được phần lớn kiến thức. " +
                        "Hãy ôn lại những phần còn sai nhé! 📚";
            } else {
                feedback = "Đừng nản lòng! Mỗi lần làm bài là một cơ hội học hỏi. " +
                        "Hãy xem lại bài và thử lại nhé! 💪";
            }
        } else {
            feedback = "Tuyệt vời! Bạn đã hoàn thành nhiệm vụ thành công. " +
                    "Hãy tiếp tục cố gắng và rèn luyện thêm nhé! 🌟";
        }
        
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
