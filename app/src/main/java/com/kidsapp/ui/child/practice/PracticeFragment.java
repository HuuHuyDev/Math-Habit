package com.kidsapp.ui.child.practice;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.widget.Toast;

import com.kidsapp.R;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.AnswerOption;
import com.kidsapp.data.model.Question;
import com.kidsapp.data.model.QuestionResponse;
import com.kidsapp.data.repository.ExerciseRepository;
import com.kidsapp.databinding.FragmentPracticeBinding;
import com.kidsapp.utils.ExerciseConverter;

import java.util.ArrayList;
import java.util.List;

public class PracticeFragment extends Fragment implements AnswerAdapter.OnAnswerSelectedListener {

    private FragmentPracticeBinding binding;
    private AnswerAdapter answerAdapter;
    private List<Question> questions;
    private int currentIndex = 0;
    private int correctCount = 0;
    private boolean isAnswerLocked = false;
    private CountDownTimer countDownTimer;
    private String contentId;  // exerciseId
    private String contentTitle;
    private String taskId;     // taskId để complete sau khi làm xong
    private int pointsReward;
    private ExerciseRepository exerciseRepository;
    private SharedPref sharedPref;
    private long startTimeMillis; // Thời gian bắt đầu làm bài

    public static PracticeFragment newInstance(String contentId, String contentTitle) {
        PracticeFragment fragment = new PracticeFragment();
        Bundle args = new Bundle();
        args.putString("content_id", contentId);
        args.putString("content_title", contentTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPracticeBinding.inflate(inflater, container, false);
        
        exerciseRepository = new ExerciseRepository(requireContext());
        sharedPref = new SharedPref(requireContext());
        startTimeMillis = System.currentTimeMillis();
        
        loadArguments();
        setupHeader();
        setupAnswerList();
        setupNavigation();
        showPetHint();
        startTimer();
        
        // Load questions từ API
        loadQuestionsFromAPI();
        
        return binding.getRoot();
    }

    private void loadArguments() {
        if (getArguments() != null) {
            contentId = getArguments().getString("content_id", "");
            contentTitle = getArguments().getString("content_title", "Luyện tập");
            taskId = getArguments().getString("taskId", "");
            pointsReward = getArguments().getInt("pointsReward", 0);
        }
    }

    private void setupHeader() {
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        // Hiển thị title bài tập
        if (binding.txtHeaderTitle != null && contentTitle != null) {
            binding.txtHeaderTitle.setText(contentTitle);
        }
    }

    /**
     * Load câu hỏi từ API dựa trên exerciseId (contentId)
     */
    private void loadQuestionsFromAPI() {
        if (contentId == null || contentId.isEmpty()) {
            // Fallback to sample data
            setupQuestions();
            updateUI();
            return;
        }

        // Hiển thị loading
        binding.recyclerAnswers.setVisibility(View.GONE);
        if (binding.progressLoading != null) {
            binding.progressLoading.setVisibility(View.VISIBLE);
        }
        
        // Call API để lấy câu hỏi
        exerciseRepository.getExerciseQuestions(contentId, new ExerciseRepository.QuestionListCallback() {
            @Override
            public void onSuccess(List<QuestionResponse> questionResponses) {
                if (getActivity() == null) return;
                
                // Ẩn loading
                if (binding.progressLoading != null) {
                    binding.progressLoading.setVisibility(View.GONE);
                }
                
                if (questionResponses == null || questionResponses.isEmpty()) {
                    // Nếu không có câu hỏi, fallback to sample data
                    Toast.makeText(requireContext(), 
                        "Bài tập chưa có câu hỏi, hiển thị dữ liệu mẫu", 
                        Toast.LENGTH_SHORT).show();
                    setupQuestions();
                } else {
                    // Convert API model sang UI model
                    questions = ExerciseConverter.convertToQuestions(questionResponses);
                }
                
                // Update UI
                binding.recyclerAnswers.setVisibility(View.VISIBLE);
                updateUI();
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;
                
                // Ẩn loading
                if (binding.progressLoading != null) {
                    binding.progressLoading.setVisibility(View.GONE);
                }
                
                // Hiển thị lỗi và fallback to sample data
                Toast.makeText(requireContext(), 
                    "Không thể tải câu hỏi: " + error, 
                    Toast.LENGTH_SHORT).show();
                
                setupQuestions();
                binding.recyclerAnswers.setVisibility(View.VISIBLE);
                updateUI();
            }
        });
    }

    /**
     * Setup câu hỏi mẫu (fallback khi không có API)
     */
    private void setupQuestions() {
        questions = new ArrayList<>();
        
        // Load questions based on contentId
        // TODO: In production, load from database or API
        switch (contentId) {
            case "1": // Phép cộng 1 chữ số
                questions.add(new Question("q1", "2 + 3 = ?",
                    createOptions("A", "4", "B", "5", "C", "6", "D", "7"), 1, "2 + 3 = 5"));
                questions.add(new Question("q2", "1 + 4 = ?",
                    createOptions("A", "3", "B", "4", "C", "5", "D", "6"), 2, "1 + 4 = 5"));
                questions.add(new Question("q3", "6 + 2 = ?",
                    createOptions("A", "7", "B", "8", "C", "9", "D", "10"), 1, "6 + 2 = 8"));
                questions.add(new Question("q4", "3 + 5 = ?",
                    createOptions("A", "6", "B", "7", "C", "8", "D", "9"), 2, "3 + 5 = 8"));
                questions.add(new Question("q5", "4 + 4 = ?",
                    createOptions("A", "6", "B", "7", "C", "8", "D", "9"), 2, "4 + 4 = 8"));
                break;
                
            case "2": // Phép cộng 2 chữ số
                questions.add(new Question("q1", "12 + 15 = ?",
                    createOptions("A", "25", "B", "26", "C", "27", "D", "28"), 2, "12 + 15 = 27"));
                questions.add(new Question("q2", "23 + 14 = ?",
                    createOptions("A", "35", "B", "36", "C", "37", "D", "38"), 2, "23 + 14 = 37"));
                questions.add(new Question("q3", "31 + 22 = ?",
                    createOptions("A", "51", "B", "52", "C", "53", "D", "54"), 2, "31 + 22 = 53"));
                questions.add(new Question("q4", "45 + 13 = ?",
                    createOptions("A", "56", "B", "57", "C", "58", "D", "59"), 2, "45 + 13 = 58"));
                questions.add(new Question("q5", "26 + 32 = ?",
                    createOptions("A", "56", "B", "57", "C", "58", "D", "59"), 2, "26 + 32 = 58"));
                break;
                
            case "3": // Bài toán minh họa
                questions.add(new Question("q1", "Bạn có 5 quả táo, mẹ cho thêm 3 quả. Hỏi bạn có bao nhiêu quả táo?",
                    createOptions("A", "6", "B", "7", "C", "8", "D", "9"), 2, "5 + 3 = 8 quả táo"));
                questions.add(new Question("q2", "Trong lớp có 12 bạn nam và 15 bạn nữ. Hỏi lớp có bao nhiêu học sinh?",
                    createOptions("A", "25", "B", "26", "C", "27", "D", "28"), 2, "12 + 15 = 27 học sinh"));
                questions.add(new Question("q3", "Bé có 8 viên bi xanh và 6 viên bi đỏ. Hỏi bé có tất cả bao nhiêu viên bi?",
                    createOptions("A", "12", "B", "13", "C", "14", "D", "15"), 2, "8 + 6 = 14 viên bi"));
                break;
                
            default: // Default questions
                questions.add(new Question("q1", "Hãy chọn đáp án đúng cho phép tính 2 + 2 = ?",
                    createOptions("A", "2 + 2 = 10", "B", "12 - 4 = 9", "C", "1 + 1 = 3", "D", "2 + 2 = 4"),
                    3, "Sai vì 2 + 2 = 4, không phải 10."));
                questions.add(new Question("q2", "Kết quả của 5 - 3 là?",
                    createOptions("A", "1", "B", "2", "C", "3", "D", "4"), 1, "5 - 3 = 2, bé nhé."));
                questions.add(new Question("q3", "9 - 6 = ?",
                    createOptions("A", "1", "B", "2", "C", "3", "D", "4"), 2, "9 - 6 = 3."));
                questions.add(new Question("q4", "3 + 4 = ?",
                    createOptions("A", "5", "B", "6", "C", "7", "D", "8"), 2, "3 cộng 4 bằng 7."));
                questions.add(new Question("q5", "12 - 8 = ?",
                    createOptions("A", "3", "B", "4", "C", "5", "D", "6"), 1, "12 trừ 8 bằng 4."));
                break;
        }
    }

    private List<AnswerOption> createOptions(String... data) {
        List<AnswerOption> options = new ArrayList<>();
        for (int i = 0; i < data.length; i += 2) {
            options.add(new AnswerOption(data[i], data[i + 1]));
        }
        return options;
    }

    private void setupAnswerList() {
        answerAdapter = new AnswerAdapter(new ArrayList<>(), this);
        binding.recyclerAnswers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerAnswers.setAdapter(answerAdapter);
    }

    private void setupNavigation() {
        binding.btnNext.setOnClickListener(v -> {
            if (currentIndex < questions.size() - 1) {
                currentIndex++;
                updateUI();
            }
        });

        binding.btnPrev.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                updateUI();
            }
        });

        // Nút Hoàn thành
        binding.btnComplete.setOnClickListener(v -> finishPractice());
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(9 * 60 * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secs = millisUntilFinished / 1000;
                binding.txtTimer.setText(String.format("%02d:%02d", secs / 60, secs % 60));
            }

            @Override
            public void onFinish() {
                finishPractice();
            }
        }.start();
    }

    private void updateUI() {
        isAnswerLocked = false;
        Question current = questions.get(currentIndex);

        binding.txtQuestionTitle.setText(current.getTitle());
        binding.layoutExplanation.setVisibility(View.GONE);
        showPetHint();

        answerAdapter = new AnswerAdapter(current.getOptions(), this);
        binding.recyclerAnswers.setAdapter(answerAdapter);

        updateIndicators();
        updateNavigationButtons();
        
        // ✅ Update trạng thái nút Hoàn thành
        checkAllAnswered();
    }

    /**
     * Cập nhật hiển thị nút Next/Hoàn thành dựa trên vị trí câu hỏi
     */
    private void updateNavigationButtons() {
        boolean isLastQuestion = currentIndex == questions.size() - 1;
        
        // Ẩn nút Next, hiện nút Hoàn thành khi ở câu cuối
        binding.btnNext.setVisibility(isLastQuestion ? View.GONE : View.VISIBLE);
        binding.btnComplete.setVisibility(isLastQuestion ? View.VISIBLE : View.GONE);
        
        // ✅ Nếu đang ở câu cuối, check xem đã làm hết chưa
        if (isLastQuestion) {
            checkAllAnswered();
        }
    }

    private void updateIndicators() {
        binding.layoutIndicators.removeAllViews();
        for (int i = 0; i < questions.size(); i++) {
            TextView indicator = new TextView(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.spacing_32),
                    getResources().getDimensionPixelSize(R.dimen.spacing_32));
            params.setMargins(8, 0, 8, 0);
            indicator.setLayoutParams(params);
            indicator.setGravity(Gravity.CENTER);
            indicator.setText(String.valueOf(i + 1));
            indicator.setTextColor(i == currentIndex ? android.graphics.Color.WHITE : getResources().getColor(R.color.text_primary));
            indicator.setBackgroundResource(
                    i == currentIndex ? R.drawable.bg_indicator_active : R.drawable.bg_indicator_inactive);
            binding.layoutIndicators.addView(indicator);
        }
    }

    private void showPetHint() {
        binding.txtPetBubble.setText(getString(R.string.practice_hint_start));
        // Không đổi màu header
    }

    private void showPetCorrect() {
        binding.txtPetBubble.setText("Chính xác! Giỏi lắm! 🎉");
        // Không đổi màu header - chỉ đổi màu đáp án
    }

    private void showPetWrong() {
        binding.txtPetBubble.setText("Chưa đúng rồi! Đọc giải thích bên dưới nhé 💡");
        // Không đổi màu header - chỉ đổi màu đáp án
    }

    @Override
    public void onAnswerSelected(int position) {
        if (isAnswerLocked) return;

        Question current = questions.get(currentIndex);
        
        // Lưu đáp án người dùng chọn vào Question
        current.setSelectedIndex(position);
        
        // Kiểm tra xem có correctIndex không (sample data vs API data)
        if (current.getCorrectIndex() != -1) {
            // ✅ SAMPLE DATA: Có correctIndex → Feedback ngay lập tức
            handleSampleDataAnswer(position, current);
        } else {
            // ✅ API DATA: Không có correctIndex → Chỉ lưu lại
            handleAPIDataAnswer(position, current);
        }
        
        // Update trạng thái nút Hoàn thành
        checkAllAnswered();
    }
    
    /**
     * Xử lý khi dùng sample data (có correctIndex)
     * Hiển thị feedback ngay lập tức: đúng/sai
     */
    private void handleSampleDataAnswer(int position, Question current) {
        isAnswerLocked = true;
        
        if (position == current.getCorrectIndex()) {
            // ✅ Đúng
            correctCount++;
            showPetCorrect();
            answerAdapter.markCorrect(position);
            binding.layoutExplanation.setVisibility(View.GONE);
            
            // Auto next sau 1.5s (nếu không phải câu cuối)
            if (currentIndex < questions.size() - 1) {
                binding.recyclerAnswers.postDelayed(() -> {
                    currentIndex++;
                    updateUI();
                }, 1500);
            }
        } else {
            // ❌ Sai
            showPetWrong();
            binding.layoutExplanation.setVisibility(View.VISIBLE);
            binding.txtExplanationContent.setText(current.getExplanation());
            answerAdapter.markWrong(position);
            isAnswerLocked = false; // Cho phép chọn lại
        }
    }
    
    /**
     * Xử lý khi dùng API data (không có correctIndex)
     * Chỉ highlight đáp án đã chọn, không biết đúng/sai ngay
     */
    private void handleAPIDataAnswer(int position, Question current) {
        // Chỉ highlight đáp án đã chọn (màu xanh nhạt)
        answerAdapter.markSelected(position);
        
        // Show pet hint - không đổi màu header
        binding.txtPetBubble.setText("Đã chọn! Làm tiếp câu khác nhé!");
        
        // Auto next sau 0.5s (không cần đợi lâu)
        if (currentIndex < questions.size() - 1) {
            binding.recyclerAnswers.postDelayed(() -> {
                currentIndex++;
                updateUI();
            }, 500);
        }
    }
    
    /**
     * Kiểm tra xem tất cả câu hỏi đã được trả lời chưa
     * Nếu đã làm hết → enable nút Hoàn Thành + đổi màu xanh
     * Nếu chưa → disable nút + màu xám
     */
    private void checkAllAnswered() {
        boolean allAnswered = true;
        
        // Duyệt qua tất cả câu hỏi
        for (Question question : questions) {
            if (!question.isAnswered()) {
                allAnswered = false;
                break;
            }
        }
        
        // Enable/disable nút Hoàn thành
        if (binding.btnComplete != null) {
            binding.btnComplete.setEnabled(allAnswered);
            
            // Đổi màu nút
            if (allAnswered) {
                // Màu xanh - có thể nộp bài
                binding.btnComplete.setBackgroundResource(R.drawable.bg_button_primary);
                binding.btnComplete.setAlpha(1.0f);
            } else {
                // Màu xám - chưa thể nộp bài
                binding.btnComplete.setBackgroundResource(R.drawable.bg_button_disabled);
                binding.btnComplete.setAlpha(0.5f);
            }
        }
    }

    private void finishPractice() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        
        // Tính điểm dựa trên correctIndex
        int totalCount = questions.size();
        int correctAnswers = 0;
        
        for (Question question : questions) {
            if (question.getSelectedIndex() != -1 && 
                question.getSelectedIndex() == question.getCorrectIndex()) {
                correctAnswers++;
            }
        }
        
        int score = totalCount > 0 ? (correctAnswers * 100 / totalCount) : 0;
        
        // Complete task trực tiếp
        final int finalCorrectAnswers = correctAnswers;
        final int finalScore = score;
        
        if (taskId != null && !taskId.isEmpty()) {
            completeExerciseTask(score, correctAnswers, totalCount);
        }
        
        // Hiển thị dialog kết quả
        showResultDialog(finalCorrectAnswers, totalCount, finalScore);
    }
    
    /**
     * Hiển thị dialog kết quả sau khi hoàn thành bài tập
     */
    private void showResultDialog(int correctAnswers, int totalCount, int score) {
        if (getContext() == null) return;
        
        // Tạo dialog
        androidx.appcompat.app.AlertDialog.Builder builder = 
            new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        
        // Tạo custom view cho dialog
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 32, 48, 16);
        layout.setGravity(Gravity.CENTER);
        
        // Icon kết quả
        TextView iconText = new TextView(requireContext());
        iconText.setText(score >= 80 ? "🎉" : score >= 50 ? "👍" : "💪");
        iconText.setTextSize(48);
        iconText.setGravity(Gravity.CENTER);
        layout.addView(iconText);
        
        // Tiêu đề
        TextView titleText = new TextView(requireContext());
        titleText.setText(score >= 80 ? "Xuất sắc!" : score >= 50 ? "Tốt lắm!" : "Cố gắng hơn nhé!");
        titleText.setTextSize(22);
        titleText.setTextColor(getResources().getColor(R.color.text_primary));
        titleText.setGravity(Gravity.CENTER);
        titleText.setPadding(0, 16, 0, 16);
        layout.addView(titleText);
        
        // Kết quả chi tiết
        TextView resultText = new TextView(requireContext());
        resultText.setText(String.format("Số câu đúng: %d/%d\nĐiểm số: %d%%", 
            correctAnswers, totalCount, score));
        resultText.setTextSize(16);
        resultText.setTextColor(getResources().getColor(R.color.text_secondary));
        resultText.setGravity(Gravity.CENTER);
        resultText.setLineSpacing(8, 1);
        layout.addView(resultText);
        
        // Điểm thưởng
        if (pointsReward > 0) {
            TextView rewardText = new TextView(requireContext());
            rewardText.setText(String.format("+%d ⭐ điểm thưởng", pointsReward));
            rewardText.setTextSize(18);
            rewardText.setTextColor(getResources().getColor(R.color.coin_orange));
            rewardText.setGravity(Gravity.CENTER);
            rewardText.setPadding(0, 24, 0, 0);
            layout.addView(rewardText);
        }
        
        builder.setView(layout);
        builder.setCancelable(false);
        builder.setPositiveButton("Hoàn tất", (dialog, which) -> {
            dialog.dismiss();
            // Quay lại và refresh danh sách
            goBackAndRefresh();
        });
        
        builder.show();
    }
    
    /**
     * Quay lại màn hình trước và thông báo refresh
     */
    private void goBackAndRefresh() {
        if (getActivity() != null) {
            // Set result để fragment trước biết cần refresh
            // Dùng activity's fragment manager để ExerciseTabFragment nhận được
            requireActivity().getSupportFragmentManager().setFragmentResult("exercise_completed", new Bundle());
            getActivity().onBackPressed();
        }
    }

    /**
     * Gọi API complete exercise task
     */
    private void completeExerciseTask(int score, int correctAnswers, int totalAnswers) {
        com.kidsapp.data.api.ApiService apiService = 
            com.kidsapp.data.api.RetrofitClient.getInstance(sharedPref).getApiService();
        
        apiService.completeExercise(taskId, score, correctAnswers, totalAnswers)
            .enqueue(new retrofit2.Callback<com.kidsapp.data.api.ApiService.ApiResponseWrapper<com.kidsapp.data.response.TaskResponse>>() {
                @Override
                public void onResponse(retrofit2.Call<com.kidsapp.data.api.ApiService.ApiResponseWrapper<com.kidsapp.data.response.TaskResponse>> call,
                                      retrofit2.Response<com.kidsapp.data.api.ApiService.ApiResponseWrapper<com.kidsapp.data.response.TaskResponse>> response) {
                    if (response.isSuccessful()) {
                        android.util.Log.d("PracticeFragment", "Task completed successfully");
                    } else {
                        android.util.Log.e("PracticeFragment", "Failed to complete task: " + response.code());
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<com.kidsapp.data.api.ApiService.ApiResponseWrapper<com.kidsapp.data.response.TaskResponse>> call, 
                                     Throwable t) {
                    android.util.Log.e("PracticeFragment", "Error completing task", t);
                }
            });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        // Clear converter cache
        ExerciseConverter.clearCache();
        binding = null;
    }
}

