package com.kidsapp.ui.child.task.personal;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kidsapp.R;
import com.kidsapp.databinding.FragmentPersonalTaskDetailBinding;

import java.util.Locale;

/**
 * Fragment hiển thị chi tiết nhiệm vụ cá nhân
 * Bao gồm: Timer, Câu hỏi, Phần thưởng
 */
public class PersonalTaskDetailFragment extends Fragment {

    private FragmentPersonalTaskDetailBinding binding;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private boolean isTimerRunning = false;
    private boolean isTaskCompleted = false;

    // Thông tin nhiệm vụ
    private String taskTitle = "Đọc sách 30 phút";
    private int durationMinutes = 30;
    private int questionCount = 1;
    private float rating = 4.8f;

    public static PersonalTaskDetailFragment newInstance(String title, int duration, int questions, float rating) {
        PersonalTaskDetailFragment fragment = new PersonalTaskDetailFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("duration", duration);
        args.putInt("questions", questions);
        args.putFloat("rating", rating);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPersonalTaskDetailBinding.inflate(inflater, container, false);
        
        loadArguments();
        setupHeader();
        setupTaskInfo();
        setupTimer();
        setupQuestion();
        setupReward();
        setupCompleteButton();
        
        return binding.getRoot();
    }

    private void loadArguments() {
        if (getArguments() != null) {
            taskTitle = getArguments().getString("title", "Đọc sách 30 phút");
            durationMinutes = getArguments().getInt("duration", 30);
            questionCount = getArguments().getInt("questions", 1);
            rating = getArguments().getFloat("rating", 4.8f);
        }
        
        // Khởi tạo thời gian
        timeLeftInMillis = durationMinutes * 60 * 1000L;
    }

    private void setupHeader() {
        binding.txtTaskTitle.setText(taskTitle);
        binding.txtTaskSubtitle.setText("Hôm nay con hãy đọc sách nhé 😊");
        binding.imgTaskIcon.setImageResource(R.drawable.ic_book);
        
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupTaskInfo() {
        binding.txtDuration.setText(durationMinutes + " phút");
        binding.txtQuestions.setText(questionCount + " câu hỏi");
        binding.txtRating.setText(String.format(Locale.getDefault(), "%.1f", rating));
    }

    private void setupTimer() {
        updateTimerText();
        
        binding.btnStartPause.setOnClickListener(v -> {
            if (isTimerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                timeLeftInMillis = 0;
                updateTimerText();
                onTimerComplete();
            }
        }.start();

        isTimerRunning = true;
        binding.btnStartPause.setText("Tạm dừng");
        binding.btnStartPause.setBackgroundResource(R.drawable.bg_button_secondary);
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        binding.btnStartPause.setText("Tiếp tục");
        binding.btnStartPause.setBackgroundResource(R.drawable.bg_button_primary);
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        binding.txtTimer.setText(timeFormatted);
    }

    private void onTimerComplete() {
        // Ẩn timer section
        binding.layoutTimerSection.setVisibility(View.GONE);
        
        // Hiện question section
        binding.layoutQuestionSection.setVisibility(View.VISIBLE);
        
        Toast.makeText(requireContext(), "Tuyệt vời! Giờ hãy trả lời câu hỏi nhé! 🎉", Toast.LENGTH_SHORT).show();
    }

    private void setupQuestion() {
        binding.layoutQuestionSection.setVisibility(View.GONE);
        
        // Setup các nút trả lời
        binding.btnAnswer1.setOnClickListener(v -> onAnswerSelected("Truyện"));
        binding.btnAnswer2.setOnClickListener(v -> onAnswerSelected("Sách học"));
        binding.btnAnswer3.setOnClickListener(v -> onAnswerSelected("Sách tranh"));
    }

    private void onAnswerSelected(String answer) {
        // Ẩn question section
        binding.layoutQuestionSection.setVisibility(View.GONE);
        
        // Hiện reward section
        binding.layoutRewardSection.setVisibility(View.VISIBLE);
        
        // Enable nút hoàn thành
        binding.btnComplete.setEnabled(true);
        binding.btnComplete.setAlpha(1.0f);
        
        isTaskCompleted = true;
    }

    private void setupReward() {
        binding.layoutRewardSection.setVisibility(View.GONE);
    }

    private void setupCompleteButton() {
        binding.btnComplete.setEnabled(false);
        binding.btnComplete.setAlpha(0.5f);
        
        binding.btnComplete.setOnClickListener(v -> {
            if (isTaskCompleted) {
                // Chuyển về màn hình trước hoặc home
                Toast.makeText(requireContext(), "Chúc mừng! Bạn đã hoàn thành nhiệm vụ! 🎉", Toast.LENGTH_SHORT).show();
                
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        binding = null;
    }
}
