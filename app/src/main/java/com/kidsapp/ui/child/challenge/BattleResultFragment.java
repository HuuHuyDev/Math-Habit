package com.kidsapp.ui.child.challenge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kidsapp.R;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.repository.ChallengeRepository;
import com.kidsapp.data.response.ChallengeResultResponse;
import com.kidsapp.databinding.FragmentBattleResultBinding;

/**
 * Fragment hiển thị kết quả thách đấu
 * Cho phép xem điểm số và thống kê với dữ liệu thực từ API
 */
public class BattleResultFragment extends Fragment {

    private FragmentBattleResultBinding binding;
    private ChallengeRepository repository;
    private SharedPref sharedPref;
    private String challengeId;
    private int questionsAnswered;
    private int totalQuestions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBattleResultBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        repository = new ChallengeRepository(requireContext());
        sharedPref = new SharedPref(requireContext());
        
        // Get data from arguments
        if (getArguments() != null) {
            challengeId = getArguments().getString("challenge_id");
            questionsAnswered = getArguments().getInt("questions_answered", 0);
            totalQuestions = getArguments().getInt("total_questions", 10);
        }
        
        setupViews();
        loadBattleResults();
    }

    private void setupViews() {
        // Back to home button
        binding.btnBackToHome.setOnClickListener(v -> {
            // Navigate back to challenge home
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        
        // Play again button
        binding.btnPlayAgain.setOnClickListener(v -> {
            // Navigate back to challenge selection
            ChallengeHomeFragment fragment = new ChallengeHomeFragment();
            
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left,
                            R.anim.slide_out_right,
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                    )
                    .replace(R.id.childHomeHost, fragment)
                    .commit();
        });
    }

    private void loadBattleResults() {
        if (challengeId == null) {
            displayFallbackResults();
            return;
        }
        
        binding.txtResultTitle.setText("Đang tải kết quả...");
        
        String childId = sharedPref.getUserId();
        if (childId == null) {
            displayFallbackResults();
            return;
        }
        
        // Gọi API lấy kết quả battle thực
        repository.getChallengeResult(challengeId, childId, new ChallengeRepository.ResultCallback<ChallengeResultResponse>() {
            @Override
            public void onSuccess(ChallengeResultResponse result) {
                if (result != null) {
                    displayRealBattleResults(result);
                } else {
                    displayFallbackResults();
                }
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("BattleResult", "Error loading battle results: " + error);
                displayFallbackResults();
            }
        });
    }
    
    private void displayRealBattleResults(ChallengeResultResponse result) {
        // Hiển thị kết quả thực từ API
        binding.txtResultTitle.setText("Kết quả thách đấu");
        
        // Player results
        ChallengeResultResponse.PlayerResult playerResult = result.getPlayerResult();
        ChallengeResultResponse.PlayerResult opponentResult = result.getOpponentResult();
        
        if (playerResult != null) {
            binding.txtScore.setText(playerResult.getCorrectAnswers() + "/" + playerResult.getTotalQuestions());
            
            double accuracy = playerResult.getAccuracy() != null ? playerResult.getAccuracy() : 0.0;
            binding.txtScorePercentage.setText(String.format("%.0f%%", accuracy));
            
            binding.txtQuestionsAnswered.setText("Câu đã trả lời: " + playerResult.getTotalQuestions());
            binding.txtTotalQuestions.setText("Câu trả lời đúng: " + playerResult.getCorrectAnswers());
            binding.txtAccuracy.setText("Độ chính xác: " + String.format("%.0f%%", accuracy));
        }
        
        // Opponent results
        if (opponentResult != null) {
            binding.txtOpponentScore.setText(opponentResult.getName() + ": " + 
                opponentResult.getCorrectAnswers() + "/" + opponentResult.getTotalQuestions());
        }
        
        // Battle result
        Boolean isPlayerWinner = result.getIsPlayerWinner();
        String winnerName = result.getWinnerName();
        
        if (isPlayerWinner != null) {
            if (isPlayerWinner) {
                binding.txtBattleResult.setText("🏆 Bạn thắng!");
                binding.txtBattleResult.setTextColor(getResources().getColor(R.color.success));
            } else if (result.getWinner().equals("DRAW")) {
                binding.txtBattleResult.setText("🤝 Hòa!");
                binding.txtBattleResult.setTextColor(getResources().getColor(R.color.primary));
            } else {
                binding.txtBattleResult.setText("😔 " + winnerName + " thắng!");
                binding.txtBattleResult.setTextColor(getResources().getColor(R.color.error));
            }
        }
        
        // Result message based on performance
        String resultMessage = result.getResultMessage();
        if (resultMessage != null && !resultMessage.isEmpty()) {
            binding.txtResultMessage.setText(resultMessage);
        } else {
            // Fallback message based on accuracy
            double accuracy = playerResult != null && playerResult.getAccuracy() != null ? 
                playerResult.getAccuracy() : 0.0;
            setResultMessage(accuracy);
        }
        
        // Rewards info
        ChallengeResultResponse.RewardInfo rewards = result.getRewards();
        if (rewards != null) {
            String rewardText = "🎁 Phần thưởng: +" + rewards.getXpEarned() + " XP, +" + 
                rewards.getCoinsEarned() + " coins";
            // Có thể thêm TextView để hiển thị rewards nếu cần
            android.util.Log.d("BattleResult", rewardText);
        }
        
        android.util.Log.d("BattleResult", "Real battle results loaded successfully");
    }
    
    private void displayFallbackResults() {
        // Hiển thị kết quả fallback khi không có API data
        // Calculate score percentage
        int scorePercentage = totalQuestions > 0 ? (questionsAnswered * 100) / totalQuestions : 0;
        
        // Display results
        binding.txtResultTitle.setText("Kết quả thách đấu");
        binding.txtScore.setText(questionsAnswered + "/" + totalQuestions);
        binding.txtScorePercentage.setText(scorePercentage + "%");
        
        setResultMessage(scorePercentage);
        
        // Show statistics
        binding.txtQuestionsAnswered.setText("Câu đã trả lời: " + questionsAnswered);
        binding.txtTotalQuestions.setText("Tổng số câu: " + totalQuestions);
        binding.txtAccuracy.setText("Độ chính xác: " + scorePercentage + "%");
        
        // Show battle result (demo - sẽ được thay bằng data thực từ API)
        int opponentScore = Math.max(0, questionsAnswered - 1); // Demo opponent score
        binding.txtOpponentScore.setText("Đối thủ: " + opponentScore + "/" + totalQuestions);
        
        if (questionsAnswered > opponentScore) {
            binding.txtBattleResult.setText("🏆 Bạn thắng!");
            binding.txtBattleResult.setTextColor(getResources().getColor(R.color.success));
        } else if (questionsAnswered == opponentScore) {
            binding.txtBattleResult.setText("🤝 Hòa!");
            binding.txtBattleResult.setTextColor(getResources().getColor(R.color.primary));
        } else {
            binding.txtBattleResult.setText("😔 Bạn thua!");
            binding.txtBattleResult.setTextColor(getResources().getColor(R.color.error));
        }
        
        android.util.Log.d("BattleResult", "Final score: " + questionsAnswered + "/" + totalQuestions + " (" + scorePercentage + "%)");
    }
    
    private void setResultMessage(double accuracy) {
        // Show result message based on score
        String resultMessage;
        String resultColor;
        if (accuracy >= 80) {
            resultMessage = "🎉 Xuất sắc! Bạn đã làm rất tốt!";
            resultColor = "success";
        } else if (accuracy >= 60) {
            resultMessage = "👍 Tốt lắm! Hãy tiếp tục cố gắng!";
            resultColor = "primary";
        } else if (accuracy >= 40) {
            resultMessage = "😊 Không tệ! Hãy luyện tập thêm nhé!";
            resultColor = "warning";
        } else {
            resultMessage = "💪 Đừng bỏ cuộc! Hãy thử lại nhé!";
            resultColor = "error";
        }
        
        binding.txtResultMessage.setText(resultMessage);
        
        // Set color based on result
        int color;
        switch (resultColor) {
            case "success":
                color = getResources().getColor(R.color.success);
                break;
            case "warning":
                color = getResources().getColor(R.color.warning_color);
                break;
            case "error":
                color = getResources().getColor(R.color.error);
                break;
            default:
                color = getResources().getColor(R.color.primary);
                break;
        }
        binding.txtResultMessage.setTextColor(color);
        
        android.util.Log.d("BattleResult", "Fallback results displayed");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}