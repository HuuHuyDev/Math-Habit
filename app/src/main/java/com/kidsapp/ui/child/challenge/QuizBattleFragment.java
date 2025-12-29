 package com.kidsapp.ui.child.challenge;

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
import com.kidsapp.data.repository.ChallengeRepository;
import com.kidsapp.data.response.QuestionResponse;
import com.kidsapp.databinding.FragmentQuizBattleBinding;
import com.kidsapp.data.local.SharedPref;

import java.util.List;

/**
 * Fragment cho màn hình thách đấu quiz
 * Hiển thị câu hỏi và cho phép trả lời trong thời gian giới hạn
 */
public class QuizBattleFragment extends Fragment {

    private FragmentQuizBattleBinding binding;
    private ChallengeRepository repository;
    private SharedPref sharedPref;
    private String challengeId;
    private List<QuestionResponse> questions;
    private CountDownTimer countDownTimer;
    private CountDownTimer questionTimer;
    private int currentQuestionIndex = 0;
    private int totalQuestions = 10;
    private int timeLimit = 5; // minutes
    private long timeRemaining;
    private long questionStartTime;
    private int selectedAnswer = 0;
    private int correctAnswers = 0;
    private boolean isBattleMode = false;
    private String opponentName = "Đối thủ";
    private final android.os.Handler handler = new android.os.Handler();

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
        
        try {
            android.util.Log.d("QuizBattle", "=== onViewCreated START ===");
            
            repository = new ChallengeRepository(requireContext());
            sharedPref = new SharedPref(requireContext());
            
            // Validate context and dependencies - repository should not be null after initialization
            android.util.Log.d("QuizBattle", "Repository and SharedPref initialized successfully");
            
            // Get challenge data from arguments
            if (getArguments() != null) {
                challengeId = getArguments().getString("challenge_id");
                
                // Optional arguments with defaults
                String challengeTitle = getArguments().getString("challenge_title", "Thách đấu");
                String categoryName = getArguments().getString("category_name", "Câu đố mẹo");
                totalQuestions = getArguments().getInt("total_questions", 10);
                timeLimit = getArguments().getInt("time_limit", 5);
                isBattleMode = getArguments().getBoolean("is_battle_mode", true); // Default to battle mode
                opponentName = getArguments().getString("opponent_name", "Đối thủ");
                
                android.util.Log.d("QuizBattle", "Arguments loaded:");
                android.util.Log.d("QuizBattle", "  - challengeId: " + challengeId);
                android.util.Log.d("QuizBattle", "  - challengeTitle: " + challengeTitle);
                android.util.Log.d("QuizBattle", "  - categoryName: " + categoryName);
                android.util.Log.d("QuizBattle", "  - totalQuestions: " + totalQuestions);
                android.util.Log.d("QuizBattle", "  - timeLimit: " + timeLimit);
                android.util.Log.d("QuizBattle", "  - isBattleMode: " + isBattleMode);
                android.util.Log.d("QuizBattle", "  - opponentName: " + opponentName);
            } else {
                android.util.Log.e("QuizBattle", "No arguments provided");
                showErrorAndExit("Lỗi: Không có thông tin thách đấu");
                return;
            }
            
            if (challengeId == null || challengeId.isEmpty()) {
                android.util.Log.e("QuizBattle", "challengeId is null or empty!");
                showErrorAndExit("Lỗi: Không có ID thách đấu");
                return;
            }
            
            // Validate user authentication
            String childId = sharedPref.getChildId();
            if (childId == null || childId.isEmpty()) {
                android.util.Log.e("QuizBattle", "Child ID is null - user not logged in");
                showErrorAndExit("Lỗi: Chưa đăng nhập");
                return;
            }
            
            android.util.Log.d("QuizBattle", "User validation passed - childId: " + childId);
            
            setupViews();
            loadQuestions();
            
            android.util.Log.d("QuizBattle", "=== onViewCreated SUCCESS ===");
        } catch (Exception e) {
            android.util.Log.e("QuizBattle", "=== onViewCreated ERROR ===", e);
            showErrorAndExit("Lỗi khởi tạo: " + e.getMessage());
        }
    }
    
    private void showErrorAndExit(String message) {
        try {
            if (getContext() != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
            
            // Delay to let user see the error message
            new android.os.Handler().postDelayed(() -> {
                try {
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        getActivity().onBackPressed();
                    }
                } catch (Exception e) {
                    android.util.Log.e("QuizBattle", "Error in showErrorAndExit", e);
                }
            }, 2000);
        } catch (Exception e) {
            android.util.Log.e("QuizBattle", "Error in showErrorAndExit", e);
        }
    }

    private void setupViews() {
        // Back button
        binding.btnBack.setOnClickListener(v -> {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            if (questionTimer != null) {
                questionTimer.cancel();
            }
            requireActivity().onBackPressed();
        });
        
        // Answer buttons
        binding.btnAnswer1.setOnClickListener(v -> selectAnswer(1));
        binding.btnAnswer2.setOnClickListener(v -> selectAnswer(2));
        binding.btnAnswer3.setOnClickListener(v -> selectAnswer(3));
        binding.btnAnswer4.setOnClickListener(v -> selectAnswer(4));
        
        // Next question button
        binding.btnNext.setOnClickListener(v -> nextQuestion());
    }

    private void loadQuestions() {
        // Show loading state
        binding.txtBattleTitle.setText(R.string.loading_questions);
        binding.txtQuestion.setText(R.string.connecting_server);
        
        // Add safety check
        if (challengeId == null || challengeId.isEmpty()) {
            android.util.Log.e("QuizBattle", "Challenge ID is null when loading questions");
            showErrorAndExit("Lỗi: Không có ID thách đấu");
            return;
        }
        
        android.util.Log.d("QuizBattle", "Loading questions for challenge: " + challengeId);
        android.util.Log.d("QuizBattle", "API Base URL: " + com.kidsapp.utils.Constants.BASE_URL);
        
        // Show network status
        binding.txtQuestion.setText("Đang gọi API: " + com.kidsapp.utils.Constants.BASE_URL + "challenges/" + challengeId + "/questions");
        
        // Show current user info for debugging
        String childId = sharedPref.getChildId();
        String userEmail = sharedPref.getUserEmail();
        android.util.Log.d("QuizBattle", "Current user - childId: " + childId + ", email: " + userEmail);
        
        repository.getChallengeQuestions(challengeId, new ChallengeRepository.ResultCallback<List<QuestionResponse>>() {
            @Override
            public void onSuccess(List<QuestionResponse> questionList) {
                android.util.Log.d("QuizBattle", "=== loadQuestions SUCCESS ===");
                android.util.Log.d("QuizBattle", "Questions received: " + (questionList != null ? questionList.size() : 0));
                
                if (questionList == null || questionList.isEmpty()) {
                    android.util.Log.e("QuizBattle", "No questions received for challenge");
                    binding.txtQuestion.setText(R.string.no_questions_error);
                    
                    // Show detailed error message
                    String errorMsg = "Challenge " + challengeId + " không có câu hỏi.\n\n";
                    errorMsg += "Có thể:\n";
                    errorMsg += "1. Challenge chưa được tạo câu hỏi\n";
                    errorMsg += "2. Category không có câu hỏi trong database\n";
                    errorMsg += "3. Lỗi kết nối API\n\n";
                    errorMsg += "Vui lòng thử lại sau hoặc tạo challenge mới.";
                    
                    binding.txtQuestion.setText(errorMsg);
                    Toast.makeText(requireContext(), "Challenge này chưa có câu hỏi. Vui lòng thử lại sau.", Toast.LENGTH_LONG).show();
                    
                    // Don't exit immediately, let user see the error
                    new android.os.Handler().postDelayed(() -> {
                        showErrorAndExit("Không có câu hỏi");
                    }, 5000);
                    return;
                }
                
                questions = questionList;
                totalQuestions = questions.size();
                
                // Lấy thông tin từ câu hỏi đầu tiên để hiển thị
                QuestionResponse firstQuestion = questions.get(0);
                String categoryName = firstQuestion.getCategoryName();
                if (categoryName == null || categoryName.isEmpty()) {
                    categoryName = "Câu đố mẹo";
                }
                
                // Update UI with battle info
                if (isBattleMode) {
                    binding.txtBattleTitle.setText("🎯 VS " + opponentName);
                    binding.txtChallengeInfo.setText("⚔️ " + categoryName + " - " + totalQuestions + " câu hỏi");
                } else {
                    binding.txtBattleTitle.setText("🎯 Thách đấu bắt đầu!");
                    binding.txtChallengeInfo.setText(categoryName + " - " + totalQuestions + " câu hỏi");
                }
                
                android.util.Log.d("QuizBattle", "UI updated - Category: " + categoryName + ", Questions: " + totalQuestions);
                
                // Start countdown timer
                timeRemaining = timeLimit * 60 * 1000; // Convert to milliseconds
                startTimer();
                
                // Load first question
                loadCurrentQuestion();
                
                // Show battle start message
                showBattleStartMessage();
                
                android.util.Log.d("QuizBattle", "=== loadQuestions COMPLETE ===");
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("QuizBattle", "=== loadQuestions ERROR ===");
                android.util.Log.e("QuizBattle", "Error: " + error);
                
                // Show detailed error on UI
                String detailedError = "❌ Lỗi tải câu hỏi:\n\n" + error + "\n\n";
                detailedError += "Debug info:\n";
                detailedError += "- Challenge ID: " + challengeId + "\n";
                detailedError += "- API URL: " + com.kidsapp.utils.Constants.BASE_URL + "\n";
                detailedError += "- Child ID: " + sharedPref.getChildId() + "\n";
                detailedError += "- User Email: " + sharedPref.getUserEmail() + "\n";
                detailedError += "- Token: " + (sharedPref.getAuthToken() != null ? "EXISTS" : "NULL") + "\n";
                
                binding.txtQuestion.setText(detailedError);
                binding.txtBattleTitle.setText("🚫 Lỗi kết nối");
                
                Toast.makeText(requireContext(), "Lỗi tải câu hỏi: " + error, Toast.LENGTH_LONG).show();
                
                // Don't exit immediately, let user see the error
                new android.os.Handler().postDelayed(() -> {
                    showErrorAndExit("Không thể tải câu hỏi");
                }, 8000);
            }
        });
    }
    
    private void showBattleStartMessage() {
        // Show a message that both players are now in battle
        if (isBattleMode) {
            String message = "🎮 Thách đấu với " + opponentName + "! Ai nhanh và đúng hơn sẽ thắng!";
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
            
            // Update UI to show battle mode
            if (binding != null) {
                binding.txtBattleTitle.setText("🎯 VS " + opponentName);
                
                // Show battle instructions
                android.util.Log.d("QuizBattle", "=== BATTLE MODE ACTIVATED ===");
                android.util.Log.d("QuizBattle", "Player: " + sharedPref.getUserEmail());
                android.util.Log.d("QuizBattle", "Opponent: " + opponentName);
                android.util.Log.d("QuizBattle", "Challenge ID: " + challengeId);
                android.util.Log.d("QuizBattle", "Both players are now in the same battle!");
            }
        } else {
            Toast.makeText(requireContext(), "🎮 Thách đấu bắt đầu!", Toast.LENGTH_SHORT).show();
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                updateTimerDisplay();
            }

            @Override
            public void onFinish() {
                // Time's up
                finishBattle();
            }
        }.start();
    }

    private void updateTimerDisplay() {
        int minutes = (int) (timeRemaining / 1000) / 60;
        int seconds = (int) (timeRemaining / 1000) % 60;
        binding.txtTimer.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void loadCurrentQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            finishBattle();
            return;
        }
        
        QuestionResponse question = questions.get(currentQuestionIndex);
        
        binding.txtQuestionNumber.setText("Câu " + (currentQuestionIndex + 1) + "/" + totalQuestions);
        binding.txtQuestion.setText(question.getQuestionText());
        
        List<String> options = question.getOptions();
        if (options != null && options.size() >= 4) {
            binding.btnAnswer1.setText(options.get(0));
            binding.btnAnswer2.setText(options.get(1));
            binding.btnAnswer3.setText(options.get(2));
            binding.btnAnswer4.setText(options.get(3));
        } else {
            // Fallback nếu không đủ options
            binding.btnAnswer1.setText("Đáp án A");
            binding.btnAnswer2.setText("Đáp án B");
            binding.btnAnswer3.setText("Đáp án C");
            binding.btnAnswer4.setText("Đáp án D");
        }
        
        // Reset button states
        resetAnswerButtons();
        binding.btnNext.setVisibility(View.GONE);
        selectedAnswer = 0;
        
        // Start question timer
        questionStartTime = System.currentTimeMillis();
        
        // Start question countdown if there's a time limit
        Integer timeLimit = question.getTimeLimit();
        if (timeLimit != null && timeLimit > 0) {
            startQuestionTimer(timeLimit);
        } else {
            // Default 30 seconds if no time limit specified
            startQuestionTimer(30);
        }
    }

    private void startQuestionTimer(int seconds) {
        if (questionTimer != null) {
            questionTimer.cancel();
        }
        
        questionTimer = new CountDownTimer(seconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Could show question timer here if needed
            }

            @Override
            public void onFinish() {
                // Auto move to next question if no answer selected
                if (selectedAnswer == 0) {
                    nextQuestion();
                }
            }
        }.start();
    }

    private void selectAnswer(int answerIndex) {
        selectedAnswer = answerIndex;
        
        // Highlight selected answer
        resetAnswerButtons();
        
        switch (answerIndex) {
            case 1:
                binding.btnAnswer1.setBackgroundResource(R.drawable.button_primary);
                break;
            case 2:
                binding.btnAnswer2.setBackgroundResource(R.drawable.button_primary);
                break;
            case 3:
                binding.btnAnswer3.setBackgroundResource(R.drawable.button_primary);
                break;
            case 4:
                binding.btnAnswer4.setBackgroundResource(R.drawable.button_primary);
                break;
        }
        
        // Show next button
        binding.btnNext.setVisibility(View.VISIBLE);
        
        // Submit answer to backend
        submitAnswer();
    }

    private void submitAnswer() {
        if (currentQuestionIndex >= questions.size()) return;
        
        QuestionResponse question = questions.get(currentQuestionIndex);
        long timeSpent = System.currentTimeMillis() - questionStartTime;
        
        android.util.Log.d("QuizBattle", "Submitting answer - Question: " + question.getId() + 
                          ", Answer: " + selectedAnswer + ", Time: " + timeSpent + "ms");
        
        repository.submitAnswer(challengeId, question.getId(), selectedAnswer, timeSpent, 
            new ChallengeRepository.ResultCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    android.util.Log.d("QuizBattle", "Answer submitted successfully: " + result);
                    
                    // Check if answer is correct (we can't know from backend response for security)
                    // But we can track locally for UI feedback
                    // Note: Real scoring is done on backend
                    
                    // Show feedback to user
                    showAnswerFeedback();
                }

                @Override
                public void onError(String error) {
                    android.util.Log.e("QuizBattle", "Error submitting answer: " + error);
                    // Continue anyway, don't block user experience
                    Toast.makeText(requireContext(), "Lỗi gửi câu trả lời, nhưng vẫn tiếp tục", Toast.LENGTH_SHORT).show();
                }
            });
    }
    
    private void showAnswerFeedback() {
        // Show brief feedback that answer was submitted
        // Don't reveal if it's correct or not (backend handles scoring)
        binding.btnNext.setText(R.string.answer_submitted);
        binding.btnNext.setEnabled(true);
        
        // Auto proceed after short delay
        new android.os.Handler().postDelayed(() -> {
            if (binding != null) {
                binding.btnNext.setText(R.string.next_question);
            }
        }, 1000);
    }

    private void resetAnswerButtons() {
        binding.btnAnswer1.setBackgroundResource(R.drawable.button_outline);
        binding.btnAnswer2.setBackgroundResource(R.drawable.button_outline);
        binding.btnAnswer3.setBackgroundResource(R.drawable.button_outline);
        binding.btnAnswer4.setBackgroundResource(R.drawable.button_outline);
    }

    private void nextQuestion() {
        if (questionTimer != null) {
            questionTimer.cancel();
        }
        
        currentQuestionIndex++;
        
        if (currentQuestionIndex < totalQuestions) {
            loadCurrentQuestion();
        } else {
            finishBattle();
        }
    }

    private void finishBattle() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (questionTimer != null) {
            questionTimer.cancel();
        }
        
        // Hiển thị thông báo đã hoàn thành
        Toast.makeText(requireContext(), "🎉 Bạn đã hoàn thành! Đang đợi đối thủ...", Toast.LENGTH_LONG).show();
        
        // Bắt đầu polling để kiểm tra trạng thái challenge
        startStatusPolling();
    }
    
    /**
     * Bắt đầu polling trạng thái challenge để đợi đối thủ hoàn thành
     */
    private void startStatusPolling() {
        String childId = sharedPref.getChildId();
        if (childId == null || challengeId == null) {
            android.util.Log.e("QuizBattle", "Missing childId or challengeId for status polling");
            requireActivity().onBackPressed();
            return;
        }
        
        android.util.Log.d("QuizBattle", "=== STARTING STATUS POLLING ===");
        android.util.Log.d("QuizBattle", "Waiting for opponent to finish...");
        
        // Hiển thị UI đợi
        showWaitingForOpponentUI();
        
        // Polling mỗi 2 giây
        Runnable statusPollingRunnable = new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null || !isAdded()) {
                    return; // Fragment đã bị destroy
                }
                
                android.util.Log.d("QuizBattle", "Polling challenge status...");
                
                final Runnable self = this; // Store reference to this Runnable
                
                repository.getChallengeStatus(challengeId, childId, new ChallengeRepository.ResultCallback<com.kidsapp.data.response.ChallengeGameResponse>() {
                    @Override
                    public void onSuccess(com.kidsapp.data.response.ChallengeGameResponse status) {
                        if (getActivity() == null || !isAdded()) {
                            return;
                        }
                        
                        android.util.Log.d("QuizBattle", "Status received: " + status);
                        
                        if (status != null) {
                            String statusCode = status.getStatus();
                            String message = status.getMessage();
                            
                            // Cập nhật UI với thông tin trạng thái
                            updateWaitingUI(message);
                            
                            if ("COMPLETED".equals(statusCode) && status.isCanViewResult()) {
                                // Cả 2 đều xong, có thể xem kết quả
                                android.util.Log.d("QuizBattle", "Both players finished! Getting results...");
                                stopStatusPolling();
                                getChallengeResultAndNavigate();
                                
                            } else if ("WAITING_OPPONENT".equals(statusCode)) {
                                // Mình xong, đối thủ chưa xong
                                android.util.Log.d("QuizBattle", "Waiting for opponent to finish...");
                                // Tiếp tục polling
                                handler.postDelayed(self, 2000);
                                
                            } else if ("OPPONENT_FINISHED".equals(statusCode)) {
                                // Đối thủ xong, mình chưa xong (không nên xảy ra vì mình đã xong)
                                android.util.Log.d("QuizBattle", "Opponent finished, but we should be finished too");
                                handler.postDelayed(self, 2000);
                                
                            } else {
                                // Trạng thái khác, tiếp tục polling
                                handler.postDelayed(self, 2000);
                            }
                        } else {
                            // Không có response, thử lại
                            handler.postDelayed(self, 3000);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        if (getActivity() == null || !isAdded()) {
                            return;
                        }
                        
                        android.util.Log.e("QuizBattle", "Error polling status: " + error);
                        
                        // Lỗi khi poll, thử lại sau 5 giây
                        updateWaitingUI(getString(R.string.connection_error_retry));
                        handler.postDelayed(self, 5000);
                    }
                });
            }
        };
        
        // Bắt đầu polling ngay lập tức
        handler.post(statusPollingRunnable);
    }
    
    /**
     * Dừng status polling
     */
    private void stopStatusPolling() {
        handler.removeCallbacksAndMessages(null);
        android.util.Log.d("QuizBattle", "Status polling stopped");
    }
    
    /**
     * Hiển thị UI đợi đối thủ
     */
    private void showWaitingForOpponentUI() {
        if (binding == null) return;
        
        // Ẩn các nút câu hỏi
        binding.btnAnswer1.setVisibility(View.GONE);
        binding.btnAnswer2.setVisibility(View.GONE);
        binding.btnAnswer3.setVisibility(View.GONE);
        binding.btnAnswer4.setVisibility(View.GONE);
        binding.btnNext.setVisibility(View.GONE);
        
        // Hiển thị thông báo đợi
        binding.txtQuestion.setText(R.string.waiting_for_opponent);
        binding.txtBattleTitle.setText("🏁 Chờ kết quả");
        
        // Có thể thêm progress bar hoặc animation
    }
    
    /**
     * Cập nhật UI đợi với thông báo mới
     */
    private void updateWaitingUI(String message) {
        if (binding == null) return;
        
        binding.txtQuestion.setText("🎉 Bạn đã hoàn thành!\n\n" + message);
    }
    
    /**
     * Lấy kết quả challenge và navigate đến result screen
     */
    private void getChallengeResultAndNavigate() {
        String childId = sharedPref.getChildId();
        if (childId == null || challengeId == null) {
            android.util.Log.e("QuizBattle", "Missing childId or challengeId for result");
            requireActivity().onBackPressed();
            return;
        }
        
        android.util.Log.d("QuizBattle", "Getting final challenge result...");
        
        repository.getChallengeResult(challengeId, childId, new ChallengeRepository.ResultCallback<com.kidsapp.data.response.ChallengeResultResponse>() {
            @Override
            public void onSuccess(com.kidsapp.data.response.ChallengeResultResponse result) {
                android.util.Log.d("QuizBattle", "Final result received successfully");
                
                // Extract data from API response
                int player1Score = 0;
                int player2Score = 0;
                int correctCount = correctAnswers;
                int wrongCount = currentQuestionIndex - correctAnswers;
                long totalTime = System.currentTimeMillis() - questionStartTime;
                boolean isWin = false;
                String opponentName = "Đối thủ";
                String challengeTitle = "Thách đấu";
                
                if (result != null) {
                    // Get challenge info
                    challengeTitle = result.getTitle() != null ? result.getTitle() : challengeTitle;
                    
                    // Get player results
                    if (result.getPlayerResult() != null) {
                        player1Score = result.getPlayerResult().getTotalScore() != null ? 
                                      result.getPlayerResult().getTotalScore() : correctAnswers;
                        correctCount = result.getPlayerResult().getCorrectAnswers() != null ? 
                                      result.getPlayerResult().getCorrectAnswers() : correctAnswers;
                        wrongCount = result.getPlayerResult().getWrongAnswers() != null ? 
                                    result.getPlayerResult().getWrongAnswers() : (currentQuestionIndex - correctAnswers);
                        totalTime = result.getPlayerResult().getTimeSpent() != null ? 
                                   result.getPlayerResult().getTimeSpent() : totalTime;
                    }
                    
                    // Get opponent results
                    if (result.getOpponentResult() != null) {
                        player2Score = result.getOpponentResult().getTotalScore() != null ? 
                                      result.getOpponentResult().getTotalScore() : 0;
                        opponentName = result.getOpponentResult().getName() != null ? 
                                      result.getOpponentResult().getName() : opponentName;
                    }
                    
                    // Determine winner
                    isWin = result.getIsPlayerWinner() != null ? result.getIsPlayerWinner() : (player1Score > player2Score);
                    
                    android.util.Log.d("QuizBattle", "Final battle results - Player1: " + player1Score + ", Player2: " + player2Score + ", Win: " + isWin);
                }
                
                // Navigate to existing ChallengeResultFragment with real data
                ChallengeResultFragment fragment = ChallengeResultFragment.newInstance(
                        player1Score, 
                        player2Score, 
                        correctCount, 
                        wrongCount, 
                        totalTime,
                        isWin,
                        opponentName,
                        challengeTitle
                );
                
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.childHomeHost, fragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("QuizBattle", "Error getting final challenge result: " + error);
                
                // Fallback to local data if API fails
                int player1Score = correctAnswers;
                int player2Score = Math.max(0, correctAnswers - 1); // Fallback opponent score
                boolean isWin = player1Score > player2Score;
                
                ChallengeResultFragment fragment = ChallengeResultFragment.newInstance(
                        player1Score, 
                        player2Score, 
                        correctAnswers, 
                        currentQuestionIndex - correctAnswers, 
                        System.currentTimeMillis() - questionStartTime,
                        isWin,
                        opponentName,
                        "Thách đấu"
                );
                
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.childHomeHost, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (questionTimer != null) {
            questionTimer.cancel();
        }
        // Dừng status polling
        stopStatusPolling();
        binding = null;
    }
}