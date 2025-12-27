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
        
        repository = new ChallengeRepository(requireContext());
        sharedPref = new SharedPref(requireContext());
        
        // Get data from arguments
        Bundle args = getArguments();
        if (args != null) {
            categoryId = args.getString("category_id");
            categoryName = args.getString("category_name");
            difficultyLevel = args.getInt("difficulty_level", 3);
            
            android.util.Log.d("QuickMatchFragment", "Category: " + categoryName + ", Difficulty: " + difficultyLevel);
        }
        
        showRandomTip();
        
        binding.btnCancel.setOnClickListener(v -> {
            cancelMatchmaking();
        });
        
        // Bắt đầu join queue
        joinQueue();
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
        String childId = sharedPref.getChildId();
        
        android.util.Log.d("QuickMatchFragment", "joinQueue - childId: " + childId + ", categoryId: " + categoryId);
        
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
        
        android.util.Log.d("QuickMatchFragment", "Calling joinQuickMatch API...");
        
        repository.joinQuickMatch(childId, categoryId, difficultyLevel, 
            new ChallengeRepository.ResultCallback<MatchFoundResponse>() {
                @Override
                public void onSuccess(MatchFoundResponse response) {
                    android.util.Log.d("QuickMatchFragment", "joinQuickMatch success - matched: " + response.isMatched());
                    
                    if (response.isMatched()) {
                        // Đã tìm thấy đối thủ ngay
                        onMatchFound(response);
                    } else {
                        // Chưa tìm thấy, bắt đầu polling
                        startPolling();
                    }
                }

                @Override
                public void onError(String error) {
                    android.util.Log.e("QuickMatchFragment", "joinQuickMatch error: " + error);
                    Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                }
            });
    }
    
    /**
     * Bắt đầu polling để kiểm tra trạng thái
     */
    private void startPolling() {
        if (isPolling) return;
        
        isPolling = true;
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isPolling) return;
                
                String childId = sharedPref.getChildId();
                if (childId == null) return;
                
                repository.getQueueStatus(childId, new ChallengeRepository.ResultCallback<QueueResponse>() {
                    @Override
                    public void onSuccess(QueueResponse response) {
                        if (response == null) {
                            // Không còn trong queue
                            stopPolling();
                            return;
                        }
                        
                        if ("MATCHED".equals(response.getStatus())) {
                            // Đã tìm thấy đối thủ
                            stopPolling();
                            // Gọi lại API để lấy thông tin match
                            joinQueue();
                        } else if ("EXPIRED".equals(response.getStatus())) {
                            // Hết thời gian chờ
                            stopPolling();
                            Toast.makeText(requireContext(), 
                                "Không tìm thấy đối thủ. Vui lòng thử lại!", 
                                Toast.LENGTH_SHORT).show();
                            requireActivity().onBackPressed();
                        } else {
                            // Vẫn đang chờ, tiếp tục polling
                            if (isPolling) {
                                handler.postDelayed(pollingRunnable, 2000); // Poll mỗi 2 giây
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        // Lỗi khi poll, thử lại sau
                        if (isPolling) {
                            handler.postDelayed(pollingRunnable, 2000);
                        }
                    }
                });
            }
        };
        
        handler.postDelayed(pollingRunnable, 2000); // Bắt đầu poll sau 2 giây
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
        Toast.makeText(requireContext(), 
            "Đã tìm thấy đối thủ: " + response.getOpponentName() + " 🎉", 
            Toast.LENGTH_SHORT).show();
        
        // Navigate to QuizBattleFragment after short delay
        handler.postDelayed(() -> {
            if (getActivity() != null) {
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
            }
        }, 1500);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopPolling();
        handler.removeCallbacksAndMessages(null);
        binding = null;
    }
}
