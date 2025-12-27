package com.kidsapp.ui.child.task.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.data.model.Task;
import com.kidsapp.databinding.FragmentWorkTabBinding;
import com.kidsapp.ui.child.task.adapter.WorkTaskAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Tab Công việc - Hiển thị tất cả công việc (Việc nhà + Cá nhân)
 * Gộp housework, habit, và custom tasks
 * 
 * NOTE: API logic đã bị xóa, chỉ giữ lại UI với mock data
 */
public class WorkTabFragment extends Fragment {
    
    private static final String TAG = "WorkTabFragment";
    
    private FragmentWorkTabBinding binding;
    private WorkTaskAdapter adapter;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkTabBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupRecyclerView();
    }
    
    /**
     * Setup RecyclerView để hiển thị danh sách công việc
     */
    private void setupRecyclerView() {
        binding.recyclerTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        adapter = new WorkTaskAdapter();
        adapter.setOnTaskActionListener(new WorkTaskAdapter.OnTaskActionListener() {
            @Override
            public void onTaskClick(Task task) {
                Toast.makeText(requireContext(), "Chi tiết: " + task.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCompleteClick(Task task) {
                Toast.makeText(requireContext(), "Hoàn thành: " + task.getTitle(), Toast.LENGTH_SHORT).show();
                // Remove task from list
                adapter.removeTask(task);
                if (adapter.getItemCount() == 0) {
                    showEmptyState();
                }
            }
        });
        
        binding.recyclerTasks.setAdapter(adapter);
    }
    
    /**
     * Load mock data để test UI
     */

    private void showTasks(List<Task> tasks) {
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.recyclerTasks.setVisibility(View.VISIBLE);
        adapter.setTasks(tasks);
    }
    
    private void showEmptyState() {
        binding.layoutEmpty.setVisibility(View.VISIBLE);
        binding.recyclerTasks.setVisibility(View.GONE);
    }
    

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
