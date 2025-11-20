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
import com.kidsapp.ui.child.task.Task;
import com.kidsapp.ui.child.task.TaskListAdapter;
import java.util.ArrayList;
import java.util.List;

public class ExerciseTabFragment extends Fragment {
    private RecyclerView recyclerViewTasks;
    private TaskListAdapter adapter;

    public ExerciseTabFragment() {
        // Required empty public constructor
    }

    public static ExerciseTabFragment newInstance(String param1, String param2) {
        return new ExerciseTabFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_tab, container, false);
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
        adapter.setOnTaskClickListener(task ->
                Toast.makeText(requireContext(), "Chọn: " + task.getTitle(), Toast.LENGTH_SHORT).show());

        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewTasks.setAdapter(adapter);
    }

    private List<Task> createSampleTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Bài 1: luyện phép cộng", 10, 15, 4.8f, R.drawable.ic_launcher_foreground, Task.TYPE_EXERCISE));
        tasks.add(new Task("Bài 2: luyện phép trừ", 12, 20, 4.5f, R.drawable.ic_launcher_foreground, Task.TYPE_EXERCISE));
        tasks.add(new Task("Bài 3: luyện phép nhân", 8, 12, 4.7f, R.drawable.ic_launcher_foreground, Task.TYPE_EXERCISE));
        tasks.add(new Task("Bài 4: luyện phép chia", 15, 25, 4.6f, R.drawable.ic_launcher_foreground, Task.TYPE_EXERCISE));
        tasks.add(new Task("Bài 5: bài tập tổng hợp", 9, 18, 4.9f, R.drawable.ic_launcher_foreground, Task.TYPE_EXERCISE));
        return tasks;
    }
}