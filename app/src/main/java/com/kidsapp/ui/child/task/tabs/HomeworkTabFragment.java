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

public class HomeworkTabFragment extends Fragment {
    private RecyclerView recyclerViewTasks;
    private TaskListAdapter adapter;

    public HomeworkTabFragment() {
        // Required empty public constructor
    }

    public static HomeworkTabFragment newInstance(String param1, String param2) {
        return new HomeworkTabFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_homework_tab, container, false);
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
        tasks.add(new Task("Dọn dẹp phòng", 5, 10, 4.5f, R.drawable.ic_launcher_foreground, Task.TYPE_HOMEWORK));
        tasks.add(new Task("Rửa bát", 3, 5, 4.2f, R.drawable.ic_launcher_foreground, Task.TYPE_HOMEWORK));
        tasks.add(new Task("Quét nhà", 2, 8, 4.3f, R.drawable.ic_launcher_foreground, Task.TYPE_HOMEWORK));
        return tasks;
    }
}