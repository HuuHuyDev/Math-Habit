package com.kidsapp.ui.child.challenge;

import android.os.Bundle;
import android.os.Handler;
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
import com.kidsapp.data.response.MatchFoundResponse;
import com.kidsapp.data.response.QueueResponse;
import com.kidsapp.databinding.FragmentQuickMatchBinding;

import java.util.Random;

/**
 * Fragment tìm đối thủ nhanh
 * Hiển thị loading và tips trong khi tìm kiếm
 */
public class QuickMatchFragment extends Fragment {

    private FragmentQuickMatchBinding binding;
    private ChallengeRepository repository;
    private SharedPref sharedPref;
    private Handler handler = new Handler();
    private Runnable pollingRunnable;
    
    private String categoryId;
    private String categoryName;
    private int difficultyLevel;
    private boolean isPolling = false;
    
    private String[] tips = {
        "Mẹo: Đọc kỹ câu hỏi trước khi chọn đáp án nhé!",
        "Mẹo: Trả lời nhanh để ghi nhiều điểm hơn!",
        "Mẹo: Giữ bình tĩnh và suy nghĩ kỹ!",
        "Mẹo: Loại trừ đáp án sai trước khi chọn!",
        "Mẹo: Đừng vội vàng, hãy đọc hết các đáp án!"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        android.util.Log.d("QuickMatchFragment", "onCreateView called");
        try {
            binding = FragmentQuickMatchBinding.inflate(inflater, container, false);
            android.util.Log.d("QuickMatchFragment", "Binding inflated successfully");
            return binding.getRoot();
        } catch (Exception e) {
            android.util.Log.e("QuickMatchFragment", "Error inflating layout", e);
            throw e;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        android.util.Log.d("QuickMatchFragment", "=== onViewCreated START ===");
        
        try {
            repository = new ChallengeRepository(requireContext());
            sharedPref = new SharedPref(requireContext());
            android.util.Log.d("QuickMatchFragment", "Repository and SharedPref initialized");
            
            // Get data from arguments
            Bundle args = getArguments();
            if (args != null) {
                categoryId = args.getString("category_id");
                categoryName = args.getString("category_name");
                difficultyLevel = args.getInt("difficulty_level", 3);
                
                android.util.Log.d("QuickMatchFragment", "Arguments received:");
                android.util.Log.d("QuickMatchFragment", "  - categoryId: " + categoryId);
                android.util.Log.d("QuickMatchFragment", "  - categoryName: " + categoryName);
                android.util.Log.d("QuickMatchFragment", "  - difficultyLevel: " + difficultyLevel);
            } else {
                android.util.Log.e("QuickMatchFragment", "Arguments is NULL!");
            }
            
            showRandomTip();
            
            binding.btnCancel.setOnClickListener(v -> {
                android.util.Log.d("QuickMatchFragment", "Cancel button clicked");
                cancelMatchmaking();
            });
            
            // Bắt đầu join queue
            android.util.Log.d("QuickMatchFragment", "Starting joinQueue...");
            joinQueue();
            
        } catch (Exception e) {
            android.util.Log.e("QuickMatchFragment", "EXCEPTION in onViewCreated", e);
            Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
            requireActivity().onBackPressed();
        }
    }

    /**
     * Hiển thị tip ngẫu nhiên
     */
    private void showRandomTip() {
        Random random = new Random();
        String tip = tips[random.nextInt(tips.length)];
        binding.txtTip.setText(tip);
    }
    
    /**
     * Tham gia hàng đợi
     */
    private void joinQueue() {
        try {
            String childId = sharedPref.getChildId();
            
            android.util.Log.d("QuickMatchFragment", "=== RACE CONDITION FIX: joinQueue START ===");
            android.util.Log.d("QuickMatchFragment", "childId: " + (childId != null ? childId : "NULL"));
            android.util.Log.d("QuickMatchFragment", "categoryId: " + (categoryId != null ? categoryId : "NULL"));
            android.util.Log.d("QuickMatchFragment", "categoryName: " + (categoryName != null ? categoryName : "NULL"));
            android.util.Log.d("QuickMatchFragment", "difficultyLevel: " + difficultyLevel);
            
            if (childId == null || childId.isEmpty()) {
                android.util.Log.e("QuickMatchFragment", "childId is null or empty!");
                Toast.makeText(requireContext(), "Chưa đăng nhập. Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
                return;
            }
            
            if (categoryId == null || categoryId.isEmpty()) {
                android.util.Log.e("QuickMatchFragment", "categoryId is null or empty!");
                Toast.makeText(requireContext(), "Thiếu thông tin chủ đề. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
                return;
            }
            
            android.util.Log.d("QuickMatchFragment", "RACE CONDITION FIX: Calling repository.joinQuickMatch...");
            
            repository.joinQuickMatch(childId, categoryId, difficultyLevel, 
                new ChallengeRepository.ResultCallback<MatchFoundResponse>() {
                    @Override
                    public void onSuccess(MatchFoundResponse response) {
                        android.util.Log.d("QuickMatchFragment", "=== RACE CONDITION FIX: joinQuickMatch SUCCESS ===");
                        android.util.Log.d("QuickMatchFragment", "Response: " + (response != null ? response.toString() : "NULL"));
                        
                        if (response == null) {
                            android.util.Log.e("QuickMatchFragment", "Response is NULL!");
                            Toast.makeText(requireContext(), "Lỗi: Không nhận được phản hồi từ server", Toast.LENGTH_SHORT).show();
                            requireActivity().onBackPressed();
                            return;
                        }
                        
                        android.util.Log.d("QuickMatchFragment", "RACE CONDITION FIX: isMatched: " + response.isMatched());
                        
                        if (response.isMatched()) {
                            // Đã tìm thấy đối thủ ngay
                            android.util.Log.d("QuickMatchFragment", "RACE CONDITION FIX: Match found immediately!");
                            onMatchFound(response);
                        } else {
                            // Chưa tìm thấy, bắt đầu polling với tần suất cao hơn
                            android.util.Log.d("QuickMatchFragment", "RACE CONDITION FIX: Not matched yet, starting fast polling...");
                            startPolling();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        android.util.Log.e("QuickMatchFragment", "=== RACE CONDITION FIX: joinQuickMatch ERROR ===");
                        android.util.Log.e("QuickMatchFragment", "Error: " + error);
                        Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    }
                });
                
        } catch (Exception e) {
            android.util.Log.e("QuickMatchFragment", "EXCEPTION in joinQueue", e);
            Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
            requireActivity().onBackPressed();
        }
    }
    
    /**
     * Bắt đầu polling để kiểm tra trạng thái
     */
    private void startPolling() {
        if (isPolling) return;
        
        isPolling = true;
        android.util.Log.d("QuickMatchFragment", "Starting polling...");
        
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isPolling) return;
                
                String childId = sharedPref.getChildId();
                if (childId == null) return;
                
                android.util.Log.d("QuickMatchFragment", "Polling queue status...");
                
                repository.getQueueStatus(childId, new ChallengeRepository.ResultCallback<QueueResponse>() {
                    @Override
                    public void onSuccess(QueueResponse response) {
                        android.util.Log.d("QuickMatchFragment", "Queue status response: " + 
                                          (response != null ? response.toString() : "NULL"));
                        
                        if (response == null) {
                            // Không còn trong queue - có thể đã được matched, kiểm tra challenge
                            android.util.Log.d("QuickMatchFragment", "No queue found - checking for active challenge...");
                            checkForActiveChallenge();
                            return;
                        }
                        
                        String status = response.getStatus();
                        android.util.Log.d("QuickMatchFragment", "Queue status: " + status);
                        
                        if ("MATCHED".equals(status)) {
                            // Đã tìm thấy đối thủ!
                            android.util.Log.d("QuickMatchFragment", "MATCHED status detected!");
                            stopPolling();
                            
                            if (response.getChallengeId() != null) {
                                // Có thông tin challenge, tạo MatchFoundResponse
                                MatchFoundResponse matchResponse = MatchFoundResponse.builder()
                                        .matched(true)
                                        .challengeId(response.getChallengeId())
                                        .opponentName(response.getOpponentName() != null ? response.getOpponentName() : "Đối thủ")
                                        .categoryName(categoryName)
                                        .difficultyLevel(difficultyLevel)
                                        .totalQuestions(10)
                                        .timeLimitMinutes(10)
                                        .message("Đã tìm thấy đối thủ!")
                                        .build();
                                
                                onMatchFound(matchResponse);
                            } else {
                                // Không có challenge info, thử lại polling nhanh hơn
                                android.util.Log.d("QuickMatchFragment", "No challenge info yet, polling faster...");
                                if (isPolling) {
                                    handler.postDelayed(pollingRunnable, 500); // Poll nhanh hơn khi đã matched
                                }
                            }
                        } else if ("EXPIRED".equals(status)) {
                            // Hết thời gian chờ
                            android.util.Log.d("QuickMatchFragment", "Queue expired");
                            stopPolling();
                            Toast.makeText(requireContext(), 
                                "Không tìm thấy đối thủ. Vui lòng thử lại!", 
                                Toast.LENGTH_SHORT).show();
                            requireActivity().onBackPressed();
                        } else {
                            // Vẫn đang chờ, tiếp tục polling
                            android.util.Log.d("QuickMatchFragment", "Still waiting, continue polling...");
                            if (isPolling) {
                                handler.postDelayed(pollingRunnable, 1000); // Poll mỗi 1 giây để responsive hơn
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        android.util.Log.e("QuickMatchFragment", "Polling error: " + error);
                        // Lỗi khi poll, thử lại sau với interval ngắn hơn
                        if (isPolling) {
                            handler.postDelayed(pollingRunnable, 1500);
                        }
                    }
                });
            }
        };
        
        handler.postDelayed(pollingRunnable, 1000); // Bắt đầu poll sau 1 giây
    }
    
    /**
     * Kiểm tra xem có challenge active không (khi không tìm thấy queue)
     */
    private void checkForActiveChallenge() {
        android.util.Log.d("QuickMatchFragment", "Checking for active challenges...");
        
        repository.getActiveChallenges(new ChallengeRepository.ResultCallback<java.util.List<com.kidsapp.data.response.ChallengeResponse>>() {
            @Override
            public void onSuccess(java.util.List<com.kidsapp.data.response.ChallengeResponse> challenges) {
                android.util.Log.d("QuickMatchFragment", "Active challenges: " + 
                                  (challenges != null ? challenges.size() : 0));
                
                if (challenges != null && !challenges.isEmpty()) {
                    // Có challenge active, vào luôn
                    com.kidsapp.data.response.ChallengeResponse challenge = challenges.get(0);
                    
                    MatchFoundResponse matchResponse = MatchFoundResponse.builder()
                            .matched(true)
                            .challengeId(challenge.getId())
                            .opponentName("Đối thủ")
                            .categoryName(categoryName)
                            .difficultyLevel(difficultyLevel)
                            .totalQuestions(10)
                            .timeLimitMinutes(10)
                            .message("Đã tìm thấy đối thủ!")
                            .build();
                    
                    stopPolling();
                    onMatchFound(matchResponse);
                } else {
                    // Không có challenge, thử gọi lại joinQueue để refresh
                    android.util.Log.d("QuickMatchFragment", "No active challenge, trying joinQueue again...");
                    joinQueue();
                }
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("QuickMatchFragment", "Error checking active challenges: " + error);
                // Lỗi khi check challenge, thử lại joinQueue
                android.util.Log.d("QuickMatchFragment", "Retrying joinQueue due to error...");
                joinQueue();
            }
        });
    }
    
    /**
     * Dừng polling
     */
    private void stopPolling() {
        isPolling = false;
        if (pollingRunnable != null) {
            handler.removeCallbacks(pollingRunnable);
        }
    }
    
    /**
     * Hủy tìm kiếm
     */
    private void cancelMatchmaking() {
        stopPolling();
        
        String childId = sharedPref.getChildId();
        if (childId != null) {
            repository.leaveQueue(childId, new ChallengeRepository.ResultCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    requireActivity().onBackPressed();
                }

                @Override
                public void onError(String error) {
                    // Vẫn quay lại dù có lỗi
                    requireActivity().onBackPressed();
                }
            });
        } else {
            requireActivity().onBackPressed();
        }
    }

    /**
     * Khi tìm thấy đối thủ
     */
    private void onMatchFound(MatchFoundResponse response) {
        try {
            android.util.Log.d("QuickMatchFragment", "=== onMatchFound START ===");
            android.util.Log.d("QuickMatchFragment", "Response: " + response);
            android.util.Log.d("QuickMatchFragment", "ChallengeId: " + response.getChallengeId());
            android.util.Log.d("QuickMatchFragment", "OpponentName: " + response.getOpponentName());
            android.util.Log.d("QuickMatchFragment", "OpponentId: " + response.getOpponentId());
            
            Toast.makeText(requireContext(), 
                "Đã tìm thấy đối thủ: " + response.getOpponentName() + " 🎉", 
                Toast.LENGTH_SHORT).show();
            
            // Navigate to QuizBattleFragment after short delay
            handler.postDelayed(() -> {
                try {
                    if (getActivity() == null) {
                        android.util.Log.e("QuickMatchFragment", "Activity is NULL, cannot navigate!");
                        return;
                    }
                    
                    android.util.Log.d("QuickMatchFragment", "Creating QuizBattleFragment...");
                    
                    // Create QuizBattleFragment
                    QuizBattleFragment battleFragment = new QuizBattleFragment();
                    
                    // Pass data to battle fragment
                    Bundle args = new Bundle();
                    args.putString("challenge_id", response.getChallengeId());
                    args.putString("opponent_name", response.getOpponentName());
                    args.putString("opponent_id", response.getOpponentId());
                    args.putString("category_name", response.getCategoryName());
                    args.putInt("difficulty_level", response.getDifficultyLevel());
                    args.putInt("total_questions", response.getTotalQuestions());
                    args.putInt("time_limit_minutes", response.getTimeLimitMinutes());
                    battleFragment.setArguments(args);
                    
                    android.util.Log.d("QuickMatchFragment", "Navigating to QuizBattleFragment...");
                    
                    // Navigate to QuizBattleFragment - use childHomeHost
                    getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                        )
                        .replace(R.id.childHomeHost, battleFragment)
                        .addToBackStack(null)
                        .commit();
                    
                    android.util.Log.d("QuickMatchFragment", "Navigation completed!");
                    
                } catch (Exception e) {
                    android.util.Log.e("QuickMatchFragment", "EXCEPTION in navigation", e);
                    Toast.makeText(requireContext(), "Lỗi khi chuyển màn hình: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }, 1500);
            
        } catch (Exception e) {
            android.util.Log.e("QuickMatchFragment", "EXCEPTION in onMatchFound", e);
            Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
            requireActivity().onBackPressed();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopPolling();
        handler.removeCallbacksAndMessages(null);
        binding = null;
    }
}
