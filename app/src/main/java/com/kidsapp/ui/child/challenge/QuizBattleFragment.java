package com.kidsapp.ui.child.challenge;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.kidsapp.R;
import com.kidsapp.data.ChallengeQuestionRepository;
import com.kidsapp.data.model.AnswerOption;
import com.kidsapp.data.model.ChatMessage;
import com.kidsapp.data.model.Question;
import com.kidsapp.databinding.FragmentQuizBattleBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

/**
 * Fragment màn hình battle chính
 * 2 người chơi cùng làm câu hỏi + chat real-time
 */
public class QuizBattleFragment extends Fragment implements BattleAnswerAdapter.OnAnswerSelectedListener {

    private static final long QUESTION_TIME_MS = 30000;
    private static final int TOTAL_QUESTIONS = 10;
    private static final int BASE_SCORE = 100;

    private FragmentQuizBattleBinding binding;
    private BattleAnswerAdapter answerAdapter;
    private ChatMessageAdapter chatAdapter;
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
    private boolean isChatVisible = false;

    // Player card views
    private TextView txtPlayer1Name, txtPlayer1Score, txtPlayer1Correct;
    private TextView txtPlayer2Name, txtPlayer2Score, txtPlayer2Correct;
    private TextView txtPlayer1Bubble, txtPlayer2Bubble;
    private LinearProgressIndicator progressPlayer1, progressPlayer2;

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
        
        initPlayerCards();
        setupRecyclerView();
        setupChatRecyclerView();
        setupClickListeners();
        setupQuestions();
        updatePlayerInfo();
        showQuestion();
    }

    private void initPlayerCards() {
        // Player 1 card
        View player1Card = binding.player1Card.getRoot();
        txtPlayer1Name = player1Card.findViewById(R.id.txtPlayerName);
        txtPlayer1Score = player1Card.findViewById(R.id.txtPlayerScore);
        txtPlayer1Correct = player1Card.findViewById(R.id.txtCorrectCount);
        progressPlayer1 = player1Card.findViewById(R.id.progressCorrect);
        txtPlayer1Bubble = player1Card.findViewById(R.id.txtChatBubble);

        // Player 2 card
        View player2Card = binding.player2Card.getRoot();
        txtPlayer2Name = player2Card.findViewById(R.id.txtPlayerName);
        txtPlayer2Score = player2Card.findViewById(R.id.txtPlayerScore);
        txtPlayer2Correct = player2Card.findViewById(R.id.txtCorrectCount);
        progressPlayer2 = player2Card.findViewById(R.id.progressCorrect);
        txtPlayer2Bubble = player2Card.findViewById(R.id.txtChatBubble);

        txtPlayer1Name.setText("Bạn");
        txtPlayer2Name.setText("Đối thủ");
    }

    private void setupRecyclerView() {
        answerAdapter = new BattleAnswerAdapter(this);
        binding.recyclerAnswers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerAnswers.setAdapter(answerAdapter);
    }

    private void setupChatRecyclerView() {
        chatAdapter = new ChatMessageAdapter();
        binding.recyclerChat.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerChat.setAdapter(chatAdapter);

        // Add welcome messages
        chatAdapter.addMessage(new ChatMessage(
            UUID.randomUUID().toString(), "system", "Hệ thống",
            "Trận đấu bắt đầu! Chúc may mắn! 🎮",
            System.currentTimeMillis(), false
        ));
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnToggleChat.setOnClickListener(v -> toggleChat());

        binding.btnSendChat.setOnClickListener(v -> sendChatMessage());
    }

    private void toggleChat() {
        isChatVisible = !isChatVisible;
        binding.cardChatMessages.setVisibility(isChatVisible ? View.VISIBLE : View.GONE);
        binding.layoutChatInput.setVisibility(isChatVisible ? View.VISIBLE : View.GONE);
        
        // Scroll to bottom of chat
        if (isChatVisible && chatAdapter.getItemCount() > 0) {
            binding.recyclerChat.scrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

    private void sendChatMessage() {
        String message = binding.edtChatMessage.getText().toString().trim();
        if (message.isEmpty()) return;

        // Hiển thị tin nhắn trên avatar của mình
        showChatBubble(txtPlayer1Bubble, message);

        // Add my message to chat list
        chatAdapter.addMessage(new ChatMessage(
            UUID.randomUUID().toString(), "me", "Bạn",
            message, System.currentTimeMillis(), true
        ));
        binding.edtChatMessage.setText("");
        binding.recyclerChat.scrollToPosition(chatAdapter.getItemCount() - 1);

        // Simulate opponent reply (random)
        simulateOpponentChat();
    }

    /**
     * Hiển thị bong bóng chat trên avatar người chơi
     */
    private void showChatBubble(TextView bubbleView, String message) {
        if (bubbleView == null) return;
        
        bubbleView.setText(message);
        bubbleView.setVisibility(View.VISIBLE);
        
        // Animation scale in
        bubbleView.setScaleX(0f);
        bubbleView.setScaleY(0f);
        bubbleView.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(200)
            .start();

        // Tự động ẩn sau 3 giây
        handler.postDelayed(() -> {
            if (bubbleView != null) {
                bubbleView.animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .setDuration(200)
                    .withEndAction(() -> bubbleView.setVisibility(View.GONE))
                    .start();
            }
        }, 3000);
    }

    private void simulateOpponentChat() {
        String[] replies = {
            "Cố lên! 💪", "Hay lắm! 👏", "Mình sẽ thắng! 🏆",
            "Câu này khó quá 😅", "Nhanh lên nào! ⚡", "Good luck! 🍀",
            "Wow! 🤩", "Haha 😄", "Giỏi quá! ⭐", "Chờ tí! 🤔"
        };
        
        if (random.nextInt(100) < 50) { // 50% chance to reply
            handler.postDelayed(() -> {
                String reply = replies[random.nextInt(replies.length)];
                
                // Hiển thị tin nhắn trên avatar đối thủ
                showChatBubble(txtPlayer2Bubble, reply);
                
                // Add to chat list
                chatAdapter.addMessage(new ChatMessage(
                    UUID.randomUUID().toString(), "opponent", "Đối thủ",
                    reply, System.currentTimeMillis(), false
                ));
                if (isChatVisible) {
                    binding.recyclerChat.scrollToPosition(chatAdapter.getItemCount() - 1);
                }
            }, 1000 + random.nextInt(2000));
        }
    }

    private void setupQuestions() {
        // Lấy câu hỏi toán logic, đố mẹo từ repository
        // Phù hợp cho mọi lứa tuổi, anh chị em bạn bè có thể chơi cùng
        questions.addAll(ChallengeQuestionRepository.getDefaultBattleQuestions());
    }

    private void updatePlayerInfo() {
        txtPlayer1Score.setText(player1Score + " điểm");
        txtPlayer2Score.setText(player2Score + " điểm");
        txtPlayer1Correct.setText(player1Correct + "/" + TOTAL_QUESTIONS);
        txtPlayer2Correct.setText(player2Correct + "/" + TOTAL_QUESTIONS);
        progressPlayer1.setProgress(player1Correct);
        progressPlayer2.setProgress(player2Correct);
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
        if (questionTimer != null) questionTimer.cancel();

        questionTimer = new CountDownTimer(QUESTION_TIME_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.txtTimer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                onTimeUp();
            }
        }.start();
    }

    @Override
    public void onAnswerSelected(int position) {
        if (questionTimer != null) questionTimer.cancel();

        Question question = questions.get(currentIndex);
        long answerTime = System.currentTimeMillis() - startTime;
        totalTime += answerTime;

        boolean isCorrect = position == question.getCorrectIndex();
        
        if (isCorrect) {
            answerAdapter.markCorrect(position);
            player1Correct++;
            int timeBonus = (int) ((QUESTION_TIME_MS - answerTime) / 100);
            player1Score += BASE_SCORE + timeBonus;
        } else {
            answerAdapter.markWrong(position);
        }

        updatePlayerInfo();

        handler.postDelayed(() -> {
            currentIndex++;
            showQuestion();
        }, 1500);
    }

    private void onTimeUp() {
        handler.postDelayed(() -> {
            currentIndex++;
            showQuestion();
        }, 1000);
    }

    private void simulateOpponentAnswer() {
        int delay = 2000 + random.nextInt(4000);
        handler.postDelayed(() -> {
            if (currentIndex < questions.size()) {
                boolean opponentCorrect = random.nextInt(100) < 70;
                
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
        if (questionTimer != null) questionTimer.cancel();
        handler.removeCallbacksAndMessages(null);

        ChallengeResultFragment resultFragment = ChallengeResultFragment.newInstance(
            player1Score, player2Score, player1Correct,
            TOTAL_QUESTIONS - player1Correct, totalTime, player1Score > player2Score
        );

        requireActivity().getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.childHomeHost, resultFragment)
            .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (questionTimer != null) questionTimer.cancel();
        handler.removeCallbacksAndMessages(null);
        binding = null;
    }
}
