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
    private String contentId;
    private String contentTitle;

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
        
        loadArguments();
        setupHeader();
        setupQuestions();
        setupAnswerList();
        setupNavigation();
        showPetHint();
        startTimer();
        updateUI();
        return binding.getRoot();
    }

    private void loadArguments() {
        if (getArguments() != null) {
            contentId = getArguments().getString("content_id", "");
            contentTitle = getArguments().getString("content_title", "Luyện tập");
        }
    }

    private void setupHeader() {
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

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

