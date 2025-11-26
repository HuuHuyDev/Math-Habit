package com.kidsapp.ui.parent.child_manage;

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

import androidx.navigation.Navigation;

import com.kidsapp.R;
import com.kidsapp.databinding.FragmentChildManageBinding;
import com.kidsapp.ui.parent.child_manage.adapter.ChildManageAdapter;
import com.kidsapp.ui.parent.child_manage.bottomsheet.AddChildBottomSheet;
import com.kidsapp.ui.parent.child_manage.bottomsheet.DeleteChildBottomSheet;
import com.kidsapp.ui.parent.child_manage.bottomsheet.EditChildBottomSheet;
import com.kidsapp.ui.parent.child_manage.model.ChildModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment Quản Lý Bé
 */
public class ChildManageFragment extends Fragment {
    private FragmentChildManageBinding binding;
    private ChildManageAdapter adapter;
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
        setupRecyclerView();
        setupListeners();
        loadDemoData();
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
    }

    private void loadDemoData() {
        allChildren.clear();
        
        ChildModel child1 = new ChildModel("1", "Nguyễn Minh Linh", "3A", 12,
                450, 600, 120, "😊");
        child1.setUsername("minhlinhkid");
        child1.setPassword("123456");
        allChildren.add(child1);
        
        ChildModel child2 = new ChildModel("2", "Trần Bảo An", "5B", 18,
                820, 1000, 340, "😄");
        child2.setUsername("baoankid");
        child2.setPassword("123456");
        allChildren.add(child2);
        
        ChildModel child3 = new ChildModel("3", "Lê Minh Châu", "2C", 8,
                280, 400, 85, "😁");
        child3.setUsername("minhchaukid");
        child3.setPassword("123456");
        allChildren.add(child3);

        filteredChildren.addAll(allChildren);
        adapter.setChildren(filteredChildren);
        updateEmptyState();
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
            allChildren.add(child);
            filterChildren(binding.edtSearch.getText().toString());
            Toast.makeText(requireContext(), "Đã thêm bé mới", Toast.LENGTH_SHORT).show();
        });
        bottomSheet.show(getChildFragmentManager(), "AddChildBottomSheet");
    }

    private void showEditBottomSheet(ChildModel child, int position) {
        EditChildBottomSheet bottomSheet = EditChildBottomSheet.newInstance(child);
        bottomSheet.setOnChildUpdatedListener(updatedChild -> {
            int index = allChildren.indexOf(child);
            if (index != -1) {
                allChildren.set(index, updatedChild);
                filterChildren(binding.edtSearch.getText().toString());
                Toast.makeText(requireContext(), "Đã cập nhật thông tin", Toast.LENGTH_SHORT).show();
            }
        });
        bottomSheet.show(getChildFragmentManager(), "EditChildBottomSheet");
    }

    private void showDeleteBottomSheet(ChildModel child, int position) {
        DeleteChildBottomSheet bottomSheet = DeleteChildBottomSheet.newInstance(child.getName());
        bottomSheet.setOnDeleteConfirmedListener(() -> {
            allChildren.remove(child);
            filterChildren(binding.edtSearch.getText().toString());
            Toast.makeText(requireContext(), "Đã xóa " + child.getName(), Toast.LENGTH_SHORT).show();
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
}
