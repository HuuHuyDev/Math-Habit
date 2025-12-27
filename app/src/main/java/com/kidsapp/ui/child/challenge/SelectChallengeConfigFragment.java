package com.kidsapp.ui.child.challenge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.R;
import com.kidsapp.data.model.Category;
import com.kidsapp.data.repository.ChallengeRepository;
import com.kidsapp.databinding.FragmentSelectChallengeConfigBinding;

import java.util.List;

/**
 * Fragment chọn cấu hình thách đấu (Category + Difficulty)
 * Dùng cho cả Quick Match và Invite Friend
 */
public class SelectChallengeConfigFragment extends Fragment 
        implements CategoryAdapter.OnCategoryClickListener {

    private FragmentSelectChallengeConfigBinding binding;
    private CategoryAdapter categoryAdapter;
    private ChallengeRepository repository;
    
    private Category selectedCategory;
    private int selectedDifficulty = 3; // Mặc định 3 sao
    private String mode; // "quick_match" hoặc "invite_friend"

    public static SelectChallengeConfigFragment newInstance(String mode) {
        SelectChallengeConfigFragment fragment = new SelectChallengeConfigFragment();
        Bundle args = new Bundle();
        args.putString("mode", mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSelectChallengeConfigBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Get mode from arguments
        if (getArguments() != null) {
            mode = getArguments().getString("mode", "quick_match");
        }
        
        repository = new ChallengeRepository(requireContext());
        setupViews();
        loadCategories();
    }

    private void setupViews() {
        // Back button
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        
        // RecyclerView cho categories
        categoryAdapter = new CategoryAdapter(this);
        binding.rvCategories.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvCategories.setAdapter(categoryAdapter);
        
        // Difficulty selector
        setupDifficultySelector();
        
        // Start button
        updateStartButton();
        binding.btnStart.setOnClickListener(v -> onStartClick());
    }

    private void setupDifficultySelector() {
        // Set initial difficulty
        updateDifficultyUI(selectedDifficulty);
        
        // Difficulty buttons
        binding.btnDifficulty1.setOnClickListener(v -> selectDifficulty(1));
        binding.btnDifficulty2.setOnClickListener(v -> selectDifficulty(2));
        binding.btnDifficulty3.setOnClickListener(v -> selectDifficulty(3));
        binding.btnDifficulty4.setOnClickListener(v -> selectDifficulty(4));
        binding.btnDifficulty5.setOnClickListener(v -> selectDifficulty(5));
    }

    private void selectDifficulty(int difficulty) {
        selectedDifficulty = difficulty;
        updateDifficultyUI(difficulty);
        updateStartButton();
    }

    private void updateDifficultyUI(int difficulty) {
        // Reset all
        binding.btnDifficulty1.setSelected(false);
        binding.btnDifficulty2.setSelected(false);
        binding.btnDifficulty3.setSelected(false);
        binding.btnDifficulty4.setSelected(false);
        binding.btnDifficulty5.setSelected(false);
        
        // Set selected
        switch (difficulty) {
            case 1:
                binding.btnDifficulty1.setSelected(true);
                binding.txtDifficultyLabel.setText("Dễ");
                break;
            case 2:
                binding.btnDifficulty2.setSelected(true);
                binding.txtDifficultyLabel.setText("Trung bình");
                break;
            case 3:
                binding.btnDifficulty3.setSelected(true);
                binding.txtDifficultyLabel.setText("Khó");
                break;
            case 4:
                binding.btnDifficulty4.setSelected(true);
                binding.txtDifficultyLabel.setText("Rất khó");
                break;
            case 5:
                binding.btnDifficulty5.setSelected(true);
                binding.txtDifficultyLabel.setText("Cực khó");
                break;
        }
    }

    private void loadCategories() {
        showLoading(true);
        
        repository.getChallengeCategories(new ChallengeRepository.ResultCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories) {
                showLoading(false);
                
                if (categories == null || categories.isEmpty()) {
                    Toast.makeText(requireContext(), 
                        "Không có chủ đề nào. Vui lòng thử lại sau!", 
                        Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                    return;
                }
                
                categoryAdapter.setCategories(categories);
                
                // Auto select first category
                selectedCategory = categories.get(0);
                categoryAdapter.setSelectedCategory(selectedCategory.getId());
                updateStartButton();
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(requireContext(), 
                    "Lỗi: " + error + ". Vui lòng thử lại!", 
                    Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        });
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.layoutContent.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateStartButton() {
        boolean canStart = selectedCategory != null && selectedDifficulty > 0;
        binding.btnStart.setEnabled(canStart);
        binding.btnStart.setAlpha(canStart ? 1.0f : 0.5f);
        
        if (canStart) {
            String text = "quick_match".equals(mode) 
                    ? "Bắt đầu tìm đối thủ" 
                    : "Tiếp tục";
            binding.btnStart.setText(text);
        }
    }

    @Override
    public void onCategoryClick(Category category) {
        selectedCategory = category;
        categoryAdapter.setSelectedCategory(category.getId());
        updateStartButton();
    }

    private void onStartClick() {
        if (selectedCategory == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn chủ đề", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if ("quick_match".equals(mode)) {
            // Navigate to QuickMatchFragment with config
            navigateToQuickMatch();
        } else {
            // Navigate to InviteFriendFragment with config
            navigateToInviteFriend();
        }
    }

    private void navigateToQuickMatch() {
        try {
            android.util.Log.d("SelectChallengeConfig", "Navigating to QuickMatchFragment...");
            android.util.Log.d("SelectChallengeConfig", "Category: " + selectedCategory.getName() + ", Difficulty: " + selectedDifficulty);
            
            Bundle args = new Bundle();
            args.putString("category_id", selectedCategory.getId());
            args.putString("category_name", selectedCategory.getName());
            args.putInt("difficulty_level", selectedDifficulty);
            
            QuickMatchFragment fragment = new QuickMatchFragment();
            fragment.setArguments(args);
            
            android.util.Log.d("SelectChallengeConfig", "Fragment created, starting transaction...");
            
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.childHomeHost, fragment)
                    .addToBackStack(null)
                    .commit();
            
            android.util.Log.d("SelectChallengeConfig", "Transaction committed successfully");
        } catch (Exception e) {
            android.util.Log.e("SelectChallengeConfig", "Error navigating to QuickMatchFragment", e);
            Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToInviteFriend() {
        Bundle args = new Bundle();
        args.putString("category_id", selectedCategory.getId());
        args.putString("category_name", selectedCategory.getName());
        args.putInt("difficulty_level", selectedDifficulty);
        
        InviteFriendFragment fragment = new InviteFriendFragment();
        fragment.setArguments(args);
        
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.childHomeHost, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
