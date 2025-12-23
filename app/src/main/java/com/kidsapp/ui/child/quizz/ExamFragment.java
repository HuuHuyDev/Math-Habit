package com.kidsapp.ui.child.quizz;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.R;
import com.kidsapp.data.model.AnswerOption;
import com.kidsapp.data.model.Question;
import com.kidsapp.databinding.FragmentExamBinding;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExamFragment extends Fragment implements ExamAnswerAdapter.OnAnswerClickListener {

    private static final long EXAM_DURATION_MS = 15 * 60 * 1000L;

    private FragmentExamBinding binding;
    private ExamAnswerAdapter answerAdapter;
    private final List<Question> questions = new ArrayList<>();
    private final Map<String, Integer> selectedAnswers = new LinkedHashMap<>();
    private int currentIndex = 0;
    private CountDownTimer countDownTimer;
    private boolean isExamFinished = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentExamBinding.inflate(inflater, container, false);
        initRecycler();
        setupButtons();
        setupQuestions();
        bindCurrentQuestion();
        startTimer();
        return binding.getRoot();
    }

    private void initRecycler() {
        answerAdapter = new ExamAnswerAdapter(this);
        binding.recyclerAnswers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerAnswers.setAdapter(answerAdapter);
    }

    private void setupButtons() {
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.btnPrevious.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                bindCurrentQuestion();
            }
        });
        binding.btnNext.setOnClickListener(v -> {
            if (currentIndex < questions.size() - 1) {
                currentIndex++;
                bindCurrentQuestion();
            }
        });
        binding.btnSubmit.setOnClickListener(v -> finishExam(false));
    }

    private void setupQuestions() {
        questions.clear();
        questions.add(new Question(
                "q1",
                "Bé hãy tính: 25 + 36 = ?",
                createOptions("A", "51", "B", "61", "C", "62", "D", "71"),
                2,
                "25 + 36 = 61"
        ));
        questions.add(new Question(
                "q2",
                "Kết quả của 120 - 45 bằng bao nhiêu?",
                createOptions("A", "65", "B", "70", "C", "75", "D", "80"),
                1,
                "120 - 45 = 75"
        ));
        questions.add(new Question(
                "q3",
                "Chọn đáp án đúng: 9 x 8 = ?",
                createOptions("A", "64", "B", "70", "C", "72", "D", "74"),
                2,
                "9 x 8 = 72"
        ));
        questions.add(new Question(
                "q4",
                "Một thửa ruộng có 30 hàng cây, mỗi hàng 5 cây. Hỏi có bao nhiêu cây?",
                createOptions("A", "120", "B", "130", "C", "150", "D", "180"),
                2,
                "30 x 5 = 150"
        ));
        questions.add(new Question(
                "q5",
                "Điền số thích hợp: 240 ÷ 6 = ?",
                createOptions("A", "30", "B", "35", "C", "40", "D", "45"),
                2,
                "240 chia 6 bằng 40"
        ));
    }

    private List<AnswerOption> createOptions(String... data) {
        List<AnswerOption> options = new ArrayList<>();
        for (int i = 0; i < data.length; i += 2) {
            options.add(new AnswerOption(data[i], data[i + 1]));
        }
        return options;
    }

    private void bindCurrentQuestion() {
        if (questions.isEmpty()) return;
        Question question = questions.get(currentIndex);

        binding.cardQuestion.startAnimation(
                AnimationUtils.loadAnimation(requireContext(), R.anim.bottom_sheet_slide_up));

        binding.txtQuestionNumber.setText(
                String.format(Locale.getDefault(), "Câu %02d", currentIndex + 1));
        binding.txtQuestionTitle.setText(question.getTitle());
        binding.txtQuestionProgress.setText(
                String.format(Locale.getDefault(), "Câu %d/%d", currentIndex + 1, questions.size()));

        answerAdapter.submitOptions(question.getOptions());
        int savedIndex = selectedAnswers.containsKey(question.getId())
                ? selectedAnswers.get(question.getId()) : -1;
        answerAdapter.setSelectedIndex(savedIndex);

        updateNavigationState();
    }

    private void updateNavigationState() {
        binding.btnPrevious.setEnabled(currentIndex > 0);
        binding.btnNext.setVisibility(currentIndex == questions.size() - 1 ? View.GONE : View.VISIBLE);
        binding.btnSubmit.setVisibility(currentIndex == questions.size() - 1 ? View.VISIBLE : View.GONE);
    }

    private void startTimer() {
        binding.txtTimer.setText(formatTime(EXAM_DURATION_MS));
        countDownTimer = new CountDownTimer(EXAM_DURATION_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.txtTimer.setText(formatTime(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                finishExam(true);
            }
        }.start();
    }

    private String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    public void onAnswerClick(int position) {
        Question question = questions.get(currentIndex);
        selectedAnswers.put(question.getId(), position);
        answerAdapter.setSelectedIndex(position);
    }

    private void finishExam(boolean isTimeout) {
        if (isExamFinished) return;
        isExamFinished = true;

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        if (isTimeout && isAdded()) {
            Toast.makeText(requireContext(), R.string.exam_time_up, Toast.LENGTH_SHORT).show();
        }

        int correctCount = 0;
        List<String> wrongTitles = new ArrayList<>();
        for (Question question : questions) {
            Integer answerIndex = selectedAnswers.get(question.getId());
            if (answerIndex != null && answerIndex == question.getCorrectIndex()) {
                correctCount++;
            } else {
                wrongTitles.add(question.getTitle());
            }
        }

        int total = questions.size();
        int wrongCount = total - correctCount;
        int percent = Math.round((correctCount * 100f) / total);

        // Tạo danh sách câu sai với đầy đủ thông tin
        ArrayList<Question> wrongQuestions = new ArrayList<>();
        for (Question question : questions) {
            Integer answerIndex = selectedAnswers.get(question.getId());
            if (answerIndex == null || answerIndex != question.getCorrectIndex()) {
                wrongQuestions.add(question);
            }
        }
        
        ExamResultFragment fragment = ExamResultFragment.newInstance(
                correctCount,
                wrongCount,
                total,
                percent,
                new ArrayList<>(wrongTitles),
                isTimeout,
                wrongQuestions
        );

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.childHomeHost, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        binding = null;
    }
}

