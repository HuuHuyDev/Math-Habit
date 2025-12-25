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
import androidx.recyclerview.widget.RecyclerView;
import com.kidsapp.R;
import com.kidsapp.ui.child.task.personal.PersonalTaskDetailFragment;
import com.kidsapp.ui.child.task.Task;
import com.kidsapp.ui.child.task.TaskListAdapter;
import java.util.ArrayList;
import java.util.List;

public class PersonalTabFragment extends Fragment {
    private RecyclerView recyclerViewTasks;
    private TaskListAdapter adapter;

    public PersonalTabFragment() {
        // Required empty public constructor
    }

    public static PersonalTabFragment newInstance(String param1, String param2) {
        return new PersonalTabFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewTasks = view.findViewById(R.id.recyclerViewTasks);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        List<Task> tasks = createSampleTasks();
        adapter = new TaskListAdapter(tasks);
        adapter.setOnTaskClickListener(task -> {
            // Chuyển sang màn hình chi tiết nhiệm vụ
            navigateToTaskDetail(task);
        });

        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewTasks.setAdapter(adapter);
    }
    
    /**
     * Chuyển sang màn hình chi tiết nhiệm vụ cá nhân
     */
    private void navigateToTaskDetail(Task task) {
        if (getActivity() != null) {
            PersonalTaskDetailFragment fragment = PersonalTaskDetailFragment.newInstance(
                task.getTitle(),
                task.getDuration(),
                task.getQuestionCount(),
                task.getRating()
            );
            
            getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.childHomeHost, fragment)
                .addToBackStack(null)
                .commit();
        }
    }

    private List<Task> createSampleTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Đọc sách 30 phút", 1, 30, 4.8f, R.drawable.ic_launcher_foreground, Task.TYPE_PERSONAL));
        tasks.add(new Task("Tập thể dục", 1, 20, 4.6f, R.drawable.ic_launcher_foreground, Task.TYPE_PERSONAL));
        tasks.add(new Task("Học nhạc", 1, 25, 4.7f, R.drawable.ic_launcher_foreground, Task.TYPE_PERSONAL));
        tasks.add(new Task("Tập thể dục", 1, 20, 4.6f, R.drawable.ic_launcher_foreground, Task.TYPE_PERSONAL));
        tasks.add(new Task("Học nhạc", 1, 25, 4.7f, R.drawable.ic_launcher_foreground, Task.TYPE_PERSONAL));
        return tasks;
    }
}