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
import com.kidsapp.data.repository.ComprehensiveTestRepository;
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
    private String contentId;
    private String contentTitle;
    private ComprehensiveTestRepository comprehensiveTestRepository;

    public static ExamFragment newInstance(String contentId, String contentTitle) {
        ExamFragment fragment = new ExamFragment();
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
        binding = FragmentExamBinding.inflate(inflater, container, false);
        comprehensiveTestRepository = ComprehensiveTestRepository.getInstance();
        
        loadArguments();
        initRecycler();
        setupButtons();
        setupQuestions();
        bindCurrentQuestion();
        startTimer();
        return binding.getRoot();
    }

    private void loadArguments() {
        if (getArguments() != null) {
            contentId = getArguments().getString("content_id", "");
            contentTitle = getArguments().getString("content_title", "Kiểm tra");
        }
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
        
        // Kiểm tra nếu là comprehensive test
        if (contentId != null && contentId.startsWith("comprehensive_test_")) {
            // Load câu hỏi từ comprehensive test repository
            List<Question> comprehensiveQuestions = comprehensiveTestRepository.getComprehensiveTestQuestions(contentId);
            questions.addAll(comprehensiveQuestions);
            return;
        }
        
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
                questions.add(new Question("q6", "7 + 1 = ?",
                    createOptions("A", "6", "B", "7", "C", "8", "D", "9"), 2, "7 + 1 = 8"));
                questions.add(new Question("q7", "5 + 3 = ?",
                    createOptions("A", "6", "B", "7", "C", "8", "D", "9"), 2, "5 + 3 = 8"));
                questions.add(new Question("q8", "2 + 6 = ?",
                    createOptions("A", "6", "B", "7", "C", "8", "D", "9"), 2, "2 + 6 = 8"));
                questions.add(new Question("q9", "9 + 0 = ?",
                    createOptions("A", "8", "B", "9", "C", "10", "D", "11"), 1, "9 + 0 = 9"));
                questions.add(new Question("q10", "4 + 5 = ?",
                    createOptions("A", "7", "B", "8", "C", "9", "D", "10"), 2, "4 + 5 = 9"));
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
                questions.add(new Question("q6", "18 + 21 = ?",
                    createOptions("A", "37", "B", "38", "C", "39", "D", "40"), 2, "18 + 21 = 39"));
                questions.add(new Question("q7", "34 + 25 = ?",
                    createOptions("A", "57", "B", "58", "C", "59", "D", "60"), 2, "34 + 25 = 59"));
                questions.add(new Question("q8", "42 + 16 = ?",
                    createOptions("A", "56", "B", "57", "C", "58", "D", "59"), 2, "42 + 16 = 58"));
                questions.add(new Question("q9", "27 + 31 = ?",
                    createOptions("A", "56", "B", "57", "C", "58", "D", "59"), 2, "27 + 31 = 58"));
                questions.add(new Question("q10", "19 + 20 = ?",
                    createOptions("A", "37", "B", "38", "C", "39", "D", "40"), 2, "19 + 20 = 39"));
                questions.add(new Question("q11", "35 + 24 = ?",
                    createOptions("A", "57", "B", "58", "C", "59", "D", "60"), 2, "35 + 24 = 59"));
                questions.add(new Question("q12", "41 + 17 = ?",
                    createOptions("A", "56", "B", "57", "C", "58", "D", "59"), 2, "41 + 17 = 58"));
                questions.add(new Question("q13", "28 + 30 = ?",
                    createOptions("A", "56", "B", "57", "C", "58", "D", "59"), 2, "28 + 30 = 58"));
                questions.add(new Question("q14", "16 + 22 = ?",
                    createOptions("A", "36", "B", "37", "C", "38", "D", "39"), 2, "16 + 22 = 38"));
                questions.add(new Question("q15", "33 + 26 = ?",
                    createOptions("A", "57", "B", "58", "C", "59", "D", "60"), 2, "33 + 26 = 59"));
                break;
                
            case "3": // Bài toán minh họa
                questions.add(new Question("q1", "Bạn có 5 quả táo, mẹ cho thêm 3 quả. Hỏi bạn có bao nhiêu quả táo?",
                    createOptions("A", "6", "B", "7", "C", "8", "D", "9"), 2, "5 + 3 = 8 quả táo"));
                questions.add(new Question("q2", "Trong lớp có 12 bạn nam và 15 bạn nữ. Hỏi lớp có bao nhiêu học sinh?",
                    createOptions("A", "25", "B", "26", "C", "27", "D", "28"), 2, "12 + 15 = 27 học sinh"));
                questions.add(new Question("q3", "Bé có 8 viên bi xanh và 6 viên bi đỏ. Hỏi bé có tất cả bao nhiêu viên bi?",
                    createOptions("A", "12", "B", "13", "C", "14", "D", "15"), 2, "8 + 6 = 14 viên bi"));
                questions.add(new Question("q4", "Mẹ mua 10 quả cam và 7 quả chanh. Hỏi mẹ mua tất cả bao nhiêu quả?",
                    createOptions("A", "15", "B", "16", "C", "17", "D", "18"), 2, "10 + 7 = 17 quả"));
                questions.add(new Question("q5", "Trong vườn có 14 con gà và 9 con vịt. Hỏi có tất cả bao nhiêu con?",
                    createOptions("A", "21", "B", "22", "C", "23", "D", "24"), 2, "14 + 9 = 23 con"));
                questions.add(new Question("q6", "Bé đọc được 6 trang sách buổi sáng và 5 trang buổi chiều. Hỏi bé đọc được bao nhiêu trang?",
                    createOptions("A", "9", "B", "10", "C", "11", "D", "12"), 2, "6 + 5 = 11 trang"));
                questions.add(new Question("q7", "Trong hộp có 13 viên kẹo đỏ và 11 viên kẹo xanh. Hỏi có tất cả bao nhiêu viên kẹo?",
                    createOptions("A", "22", "B", "23", "C", "24", "D", "25"), 2, "13 + 11 = 24 viên kẹo"));
                questions.add(new Question("q8", "Bạn An có 9 cây bút chì và 8 cây bút màu. Hỏi bạn An có tất cả bao nhiêu cây bút?",
                    createOptions("A", "15", "B", "16", "C", "17", "D", "18"), 2, "9 + 8 = 17 cây bút"));
                questions.add(new Question("q9", "Trong giỏ có 7 quả chuối và 4 quả dứa. Hỏi có tất cả bao nhiêu quả?",
                    createOptions("A", "9", "B", "10", "C", "11", "D", "12"), 2, "7 + 4 = 11 quả"));
                questions.add(new Question("q10", "Bé vẽ được 11 bức tranh tuần này và 8 bức tranh tuần trước. Hỏi bé vẽ được bao nhiêu bức tranh?",
                    createOptions("A", "17", "B", "18", "C", "19", "D", "20"), 2, "11 + 8 = 19 bức tranh"));
                questions.add(new Question("q11", "Trong lớp có 16 bạn thích bóng đá và 12 bạn thích bóng rổ. Hỏi có bao nhiêu bạn?",
                    createOptions("A", "26", "B", "27", "C", "28", "D", "29"), 2, "16 + 12 = 28 bạn"));
                questions.add(new Question("q12", "Mẹ nấu 15 cái bánh và bà nấu 10 cái bánh. Hỏi có tất cả bao nhiêu cái bánh?",
                    createOptions("A", "23", "B", "24", "C", "25", "D", "26"), 2, "15 + 10 = 25 cái bánh"));
                break;
                
            default: // Default questions
                questions.add(new Question("q1", "Bé hãy tính: 25 + 36 = ?",
                    createOptions("A", "51", "B", "61", "C", "62", "D", "71"), 2, "25 + 36 = 61"));
                questions.add(new Question("q2", "Kết quả của 120 - 45 bằng bao nhiêu?",
                    createOptions("A", "65", "B", "70", "C", "75", "D", "80"), 1, "120 - 45 = 75"));
                questions.add(new Question("q3", "Chọn đáp án đúng: 9 x 8 = ?",
                    createOptions("A", "64", "B", "70", "C", "72", "D", "74"), 2, "9 x 8 = 72"));
                questions.add(new Question("q4", "Một thửa ruộng có 30 hàng cây, mỗi hàng 5 cây. Hỏi có bao nhiêu cây?",
                    createOptions("A", "120", "B", "130", "C", "150", "D", "180"), 2, "30 x 5 = 150"));
                questions.add(new Question("q5", "Điền số thích hợp: 240 ÷ 6 = ?",
                    createOptions("A", "30", "B", "35", "C", "40", "D", "45"), 2, "240 chia 6 bằng 40"));
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
                wrongQuestions,
                contentId,
                contentTitle
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

