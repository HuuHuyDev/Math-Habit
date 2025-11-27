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

import com.kidsapp.R;
import com.kidsapp.data.model.AnswerOption;
import com.kidsapp.data.model.Question;
import com.kidsapp.databinding.FragmentPracticeBinding;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPracticeBinding.inflate(inflater, container, false);
        setupHeader();
        setupQuestions();
        setupAnswerList();
        setupNavigation();
        showPetHint();
        startTimer();
        updateUI();
        return binding.getRoot();
    }

    private void setupHeader() {
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void setupQuestions() {
        questions = new ArrayList<>();
        questions.add(new Question(
                "q1",
                "Hãy chọn đáp án đúng cho phép tính 2 + 2 = ?",
                createOptions("A", "2 + 2 = 10", "B", "12 - 4 = 9", "C", "1 + 1 = 3", "D", "2 + 2 = 4"),
                3,
                "Sai vì 2 + 2 = 4, không phải 10."
        ));
        questions.add(new Question(
                "q2",
                "Kết quả của 5 - 3 là?",
                createOptions("A", "1", "B", "2", "C", "3", "D", "4"),
                1,
                "5 - 3 = 2, bé nhé."
        ));
        questions.add(new Question(
                "q3",
                "9 - 6 = ?",
                createOptions("A", "1", "B", "2", "C", "3", "D", "4"),
                2,
                "9 - 6 = 3."
        ));
        questions.add(new Question(
                "q4",
                "3 + 4 = ?",
                createOptions("A", "5", "B", "6", "C", "7", "D", "8"),
                2,
                "3 cộng 4 bằng 7."
        ));
        questions.add(new Question(
                "q5",
                "12 - 8 = ?",
                createOptions("A", "3", "B", "4", "C", "5", "D", "6"),
                1,
                "12 trừ 8 bằng 4."
        ));
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
            } else {
                finishPractice();
            }
        });

        binding.btnPrev.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                updateUI();
            }
        });

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
        binding.layoutExplanation.getRoot().setVisibility(View.GONE);
        showPetHint();

        answerAdapter = new AnswerAdapter(current.getOptions(), this);
        binding.recyclerAnswers.setAdapter(answerAdapter);

        updateIndicators();
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
    }

    private void showPetCorrect() {
        binding.txtPetBubble.setText(getString(R.string.practice_pet_correct));
    }

    private void showPetWrong() {
        binding.txtPetBubble.setText(getString(R.string.practice_pet_wrong));
    }

    @Override
    public void onAnswerSelected(int position) {
        if (isAnswerLocked) return;

        Question current = questions.get(currentIndex);
        
        // Lưu đáp án người dùng chọn vào Question
        current.setSelectedIndex(position);
        
        // Kiểm tra xem đã làm hết câu chưa để enable/disable nút Hoàn Thành
        checkAllAnswered();
        
        isAnswerLocked = true;
        if (position == current.getCorrectIndex()) {
            correctCount++;
            showPetCorrect();
            binding.layoutExplanation.getRoot().setVisibility(View.GONE);
            answerAdapter.markCorrect(position);
            binding.recyclerAnswers.postDelayed(() -> {
                if (currentIndex < questions.size() - 1) {
                    currentIndex++;
                    updateUI();
                } else {
                    finishPractice();
                }
            }, 1500);
        } else {
            showPetWrong();
            binding.layoutExplanation.getRoot().setVisibility(View.VISIBLE);
            binding.layoutExplanation.txtExplanationContent.setText(current.getExplanation());
            answerAdapter.markWrong(position);
            isAnswerLocked = false;
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

    }

    private void finishPractice() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        Bundle args = new Bundle();
        args.putInt("correct_count", correctCount);
        args.putInt("total_count", questions.size());
        // TODO: điều hướng sang màn hình kết quả
//         NavHostFragment.findNavController(this)
//                .navigate(R.id.action_practiceFragment_to_practiceResultFragment, args);
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

