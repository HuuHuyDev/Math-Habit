package com.kidsapp.ui.child.challenge;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.R;
import com.kidsapp.data.model.AnswerOption;
import com.kidsapp.data.model.Question;
import com.kidsapp.databinding.FragmentQuizBattleBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Fragment màn hình battle chính
 * Hiển thị câu hỏi, đếm ngược thời gian, cập nhật điểm 2 người chơi
 */
public class QuizBattleFragment extends Fragment implements BattleAnswerAdapter.OnAnswerSelectedListener {

    private static final long QUESTION_TIME_MS = 30000; // 30 seconds per question
    private static final int TOTAL_QUESTIONS = 10;
    private static final int BASE_SCORE = 100;

    private FragmentQuizBattleBinding binding;
    private BattleAnswerAdapter answerAdapter;
    private final List<Question> questions = new ArrayList<>();
    private int currentIndex = 0;
    private int player1Score = 0;
    private int player2Score = 0;
    private int player1Correct = 0;
    private int player2Correct = 0;
    private CountDownTimer questionTimer;
    private Handler handler = new Handler();
    private Random random = new Random();
    private long startTime;
    private long totalTime = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentQuizBattleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupRecyclerView();
        setupQuestions();
        updatePlayerInfo();
        showQuestion();
    }

    private void setupRecyclerView() {
        answerAdapter = new BattleAnswerAdapter(this);
        binding.recyclerAnswers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerAnswers.setAdapter(answerAdapter);
    }

    private void setupQuestions() {
        // TODO: Load questions from repository based on challenge settings
        // For now, create sample questions
        questions.add(new Question("q1", "2 + 3 = ?",
            createOptions("A", "4", "B", "5", "C", "6", "D", "7"), 1, "2 + 3 = 5"));
        questions.add(new Question("q2", "5 - 2 = ?",
            createOptions("A", "2", "B", "3", "C", "4", "D", "5"), 1, "5 - 2 = 3"));
        questions.add(new Question("q3", "4 + 4 = ?",
            createOptions("A", "6", "B", "7", "C", "8", "D", "9"), 2, "4 + 4 = 8"));
        questions.add(new Question("q4", "10 - 3 = ?",
            createOptions("A", "5", "B", "6", "C", "7", "D", "8"), 2, "10 - 3 = 7"));
        questions.add(new Question("q5", "6 + 2 = ?",
            createOptions("A", "6", "B", "7", "C", "8", "D", "9"), 2, "6 + 2 = 8"));
        questions.add(new Question("q6", "9 - 4 = ?",
            createOptions("A", "4", "B", "5", "C", "6", "D", "7"), 1, "9 - 4 = 5"));
        questions.add(new Question("q7", "3 + 5 = ?",
            createOptions("A", "6", "B", "7", "C", "8", "D", "9"), 2, "3 + 5 = 8"));
        questions.add(new Question("q8", "8 - 3 = ?",
            createOptions("A", "4", "B", "5", "C", "6", "D", "7"), 1, "8 - 3 = 5"));
        questions.add(new Question("q9", "7 + 1 = ?",
            createOptions("A", "6", "B", "7", "C", "8", "D", "9"), 2, "7 + 1 = 8"));
        questions.add(new Question("q10", "6 - 2 = ?",
            createOptions("A", "3", "B", "4", "C", "5", "D", "6"), 1, "6 - 2 = 4"));
    }

    private List<AnswerOption> createOptions(String... data) {
        List<AnswerOption> options = new ArrayList<>();
        for (int i = 0; i < data.length; i += 2) {
            options.add(new AnswerOption(data[i], data[i + 1]));
        }
        return options;
    }

    private void updatePlayerInfo() {
        binding.txtPlayer1Name.setText("Bạn");
        binding.txtPlayer2Name.setText("Đối thủ");
        binding.txtPlayer1Score.setText(player1Score + " điểm");
        binding.txtPlayer2Score.setText(player2Score + " điểm");
    }

    private void showQuestion() {
        if (currentIndex >= questions.size()) {
            finishBattle();
            return;
        }

        Question question = questions.get(currentIndex);
        binding.txtQuestionNumber.setText(String.format(Locale.getDefault(), "Câu %02d", currentIndex + 1));
        binding.txtQuestionTitle.setText(question.getTitle());
        binding.txtQuestionProgress.setText(
            String.format(Locale.getDefault(), "Câu %d/%d", currentIndex + 1, TOTAL_QUESTIONS));

        answerAdapter.submitOptions(question.getOptions());
        answerAdapter.resetState();

        startTime = System.currentTimeMillis();
        startQuestionTimer();
        simulateOpponentAnswer();
    }

    private void startQuestionTimer() {
        if (questionTimer != null) {
            questionTimer.cancel();
        }

        binding.txtTimer.setText(formatTime(QUESTION_TIME_MS));
        questionTimer = new CountDownTimer(QUESTION_TIME_MS, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.txtTimer.setText(formatTime(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                onTimeUp();
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
    public void onAnswerSelected(int position) {
        if (questionTimer != null) {
            questionTimer.cancel();
        }

        Question question = questions.get(currentIndex);
        long answerTime = System.currentTimeMillis() - startTime;
        totalTime += answerTime;

        boolean isCorrect = position == question.getCorrectIndex();
        
        if (isCorrect) {
            answerAdapter.markCorrect(position);
            player1Correct++;
            // Calculate score based on speed (faster = more points)
            int timeBonus = (int) ((QUESTION_TIME_MS - answerTime) / 100);
            player1Score += BASE_SCORE + timeBonus;
        } else {
            answerAdapter.markWrong(position);
        }

        updatePlayerInfo();

        // Move to next question after delay
        handler.postDelayed(() -> {
            currentIndex++;
            showQuestion();
        }, 2000);
    }

    private void onTimeUp() {
        // Player didn't answer in time
        handler.postDelayed(() -> {
            currentIndex++;
            showQuestion();
        }, 1000);
    }

    /**
     * Simulate opponent answering (AI)
     * Opponent has 70% chance to answer correctly
     */
    private void simulateOpponentAnswer() {
        int delay = 3000 + random.nextInt(5000); // 3-8 seconds
        handler.postDelayed(() -> {
            if (currentIndex < questions.size()) {
                Question question = questions.get(currentIndex);
                boolean opponentCorrect = random.nextInt(100) < 70; // 70% correct rate
                
                if (opponentCorrect) {
                    player2Correct++;
                    int timeBonus = random.nextInt(200);
                    player2Score += BASE_SCORE + timeBonus;
                    updatePlayerInfo();
                }
            }
        }, delay);
    }

    private void finishBattle() {
        if (questionTimer != null) {
            questionTimer.cancel();
        }
        handler.removeCallbacksAndMessages(null);

        // Navigate to result screen
        ChallengeResultFragment resultFragment = ChallengeResultFragment.newInstance(
            player1Score,
            player2Score,
            player1Correct,
            TOTAL_QUESTIONS - player1Correct,
            totalTime,
            player1Score > player2Score
        );

        requireActivity().getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.childHomeHost, resultFragment)
            .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (questionTimer != null) {
            questionTimer.cancel();
        }
        handler.removeCallbacksAndMessages(null);
        binding = null;
    }
}
