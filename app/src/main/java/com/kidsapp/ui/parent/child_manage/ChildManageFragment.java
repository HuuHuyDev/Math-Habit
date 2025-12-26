package com.kidsapp.ui.parent.child_manage;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.databinding.FragmentChildManageBinding;
import com.kidsapp.ui.auth.LoginActivity;
import com.kidsapp.ui.parent.child_manage.adapter.ChildManageAdapter;
import com.kidsapp.ui.parent.child_manage.bottomsheet.AddChildBottomSheet;
import com.kidsapp.ui.parent.child_manage.bottomsheet.DeleteChildBottomSheet;
import com.kidsapp.ui.parent.child_manage.bottomsheet.EditChildBottomSheet;
import com.kidsapp.ui.parent.child_manage.model.ChildModel;
import com.kidsapp.viewmodel.ChildViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment Quản Lý Bé
 */
public class ChildManageFragment extends Fragment {
    private FragmentChildManageBinding binding;
    private ChildManageAdapter adapter;
    private ChildViewModel viewModel;
    private List<ChildModel> allChildren = new ArrayList<>();
    private List<ChildModel> filteredChildren = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChildManageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(ChildViewModel.class);
        
        setupRecyclerView();
        setupListeners();
        observeViewModel();
        
        // Load data from API
        viewModel.loadChildren();
    }

    private void setupRecyclerView() {
        adapter = new ChildManageAdapter();
        binding.recyclerChildren.setAdapter(adapter);

        adapter.setOnChildActionListener(new ChildManageAdapter.OnChildActionListener() {
            @Override
            public void onViewDetail(ChildModel child, int position) {
                navigateToChildDetail(child);
            }

            @Override
            public void onEdit(ChildModel child, int position) {
                showEditBottomSheet(child, position);
            }

            @Override
            public void onDelete(ChildModel child, int position) {
                showDeleteBottomSheet(child, position);
            }
        });
    }

    private void setupListeners() {
        // Back button
        binding.appbar.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // Add child buttons
        binding.appbar.btnAddChild.setOnClickListener(v -> showAddBottomSheet());
        binding.btnAddChildBottom.setOnClickListener(v -> showAddBottomSheet());

        // Search
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterChildren(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        // Swipe refresh
        binding.swipeRefresh.setOnRefreshListener(() -> {
            viewModel.loadChildren();
        });
    }

    private void observeViewModel() {
        viewModel.getChildren().observe(getViewLifecycleOwner(), children -> {
            binding.swipeRefresh.setRefreshing(false);
            if (children != null) {
                allChildren.clear();
                for (ApiService.ChildResponse child : children) {
                    allChildren.add(mapToChildModel(child));
                }
                filterChildren(binding.edtSearch.getText().toString());
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            binding.swipeRefresh.setRefreshing(false);
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                
                // Nếu token hết hạn, redirect về login
                if (error.contains("Phiên đăng nhập hết hạn") || error.contains("401")) {
                    handleSessionExpired();
                }
                
                viewModel.clearMessages();
            }
        });

        viewModel.getSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && !success.isEmpty()) {
                Toast.makeText(requireContext(), success, Toast.LENGTH_SHORT).show();
                viewModel.clearMessages();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
    }

    private ChildModel mapToChildModel(ApiService.ChildResponse response) {
        // Tính maxXP dựa trên level
        int level = response.level != null ? response.level : 1;
        int totalPoints = response.totalPoints != null ? response.totalPoints : 0;
        int maxXP = level * 100;
        int currentXP = totalPoints % 100; // XP trong level hiện tại
        
        // Tạo className từ grade
        String className = response.grade != null ? "Lớp " + response.grade : "";
        
        ChildModel model = new ChildModel(
                response.id,
                response.name != null ? response.name : response.nickname,
                className,
                level,
                currentXP,
                maxXP,
                response.currentStreak != null ? response.currentStreak : 0,
                response.avatarUrl != null ? response.avatarUrl : "😊"
        );
        
        model.setNickname(response.nickname);
        model.setSchool(response.school);
        model.setBirthDate(response.birthDate);
        model.setTotalPoints(totalPoints);
        model.setGender(response.gender);
        
        return model;
    }

    private void filterChildren(String query) {
        filteredChildren.clear();
        if (query.isEmpty()) {
            filteredChildren.addAll(allChildren);
        } else {
            String lowerQuery = query.toLowerCase();
            for (ChildModel child : allChildren) {
                if (child.getName().toLowerCase().contains(lowerQuery)) {
                    filteredChildren.add(child);
                }
            }
        }
        adapter.setChildren(filteredChildren);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredChildren.isEmpty()) {
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
            binding.recyclerChildren.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyState.setVisibility(View.GONE);
            binding.recyclerChildren.setVisibility(View.VISIBLE);
        }
    }

    private void showAddBottomSheet() {
        AddChildBottomSheet bottomSheet = AddChildBottomSheet.newInstance();
        bottomSheet.setOnChildAddedListener(child -> {
            // Gọi API thêm bé
            viewModel.createChild(
                    child.getName(),
                    child.getNickname(),
                    child.getBirthDate(),
                    child.getGradeNumber(),
                    child.getSchool(),
                    child.getAvatar(),
                    child.getGender(),
                    child.getUsername(),
                    child.getPassword()
            );
        });
        bottomSheet.show(getChildFragmentManager(), "AddChildBottomSheet");
    }

    private void showEditBottomSheet(ChildModel child, int position) {
        EditChildBottomSheet bottomSheet = EditChildBottomSheet.newInstance(child);
        bottomSheet.setOnChildUpdatedListener(updatedChild -> {
            // Gọi API cập nhật
            viewModel.updateChild(
                    child.getId(),
                    updatedChild.getName(),
                    updatedChild.getNickname(),
                    updatedChild.getBirthDate(),
                    updatedChild.getGradeNumber(),
                    updatedChild.getSchool(),
                    updatedChild.getAvatar(),
                    updatedChild.getGender(),
                    updatedChild.getPassword() // newPassword nếu có
            );
        });
        bottomSheet.show(getChildFragmentManager(), "EditChildBottomSheet");
    }

    private void showDeleteBottomSheet(ChildModel child, int position) {
        android.util.Log.d("ChildManageFragment", "showDeleteBottomSheet: childId=" + child.getId() + ", name=" + child.getName());
        
        DeleteChildBottomSheet bottomSheet = DeleteChildBottomSheet.newInstance(child.getName());
        bottomSheet.setOnDeleteConfirmedListener(() -> {
            android.util.Log.d("ChildManageFragment", "onDeleteConfirmed: childId=" + child.getId());
            // Gọi API xóa
            viewModel.deleteChild(child.getId());
        });
        bottomSheet.show(getChildFragmentManager(), "DeleteChildBottomSheet");
    }

    private void navigateToChildDetail(ChildModel child) {
        Bundle bundle = new Bundle();
        bundle.putString("childId", child.getId());
        bundle.putString("childName", child.getName());
        bundle.putInt("childLevel", child.getLevel());
        bundle.putInt("childXP", child.getCurrentXP());
        bundle.putString("username", child.getUsername());
        bundle.putString("password", child.getPassword());

        try {
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_nav_children_to_childDetail, bundle);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Lỗi navigation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    
    private void handleSessionExpired() {
        // Clear session
        SharedPref sharedPref = new SharedPref(requireContext());
        sharedPref.clearAll();
        RetrofitClient.resetInstance();
        
        // Redirect to login
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
