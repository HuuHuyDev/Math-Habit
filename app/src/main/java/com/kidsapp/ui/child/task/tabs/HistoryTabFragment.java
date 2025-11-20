package com.kidsapp.ui.child.task.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kidsapp.R;
import com.kidsapp.ui.child.task.TaskHistory;
import com.kidsapp.ui.child.task.TaskHistoryAdapter;
import java.util.ArrayList;
import java.util.List;

public class HistoryTabFragment extends Fragment {
    private RecyclerView recyclerViewHistory;
    private TaskHistoryAdapter adapter;

    public HistoryTabFragment() {
        // Required empty public constructor
    }

    public static HistoryTabFragment newInstance(String param1, String param2) {
        return new HistoryTabFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewHistory = view.findViewById(R.id.recyclerViewHistory);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        List<TaskHistory> historyList = createSampleHistory();
        adapter = new TaskHistoryAdapter(historyList);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewHistory.setAdapter(adapter);
    }

    private List<TaskHistory> createSampleHistory() {
        List<TaskHistory> historyList = new ArrayList<>();
        historyList.add(new TaskHistory("Bài 1: luyện phép cộng", "15/11/2024", "10/10", 4.8f));
        historyList.add(new TaskHistory("Bài 2: luyện phép trừ", "14/11/2024", "12/12", 4.5f));
        historyList.add(new TaskHistory("Bài 3: luyện phép nhân", "13/11/2024", "8/8", 4.7f));
        historyList.add(new TaskHistory("Dọn dẹp phòng", "12/11/2024", "Hoàn thành", 5.0f));
        historyList.add(new TaskHistory("Đọc sách 30 phút", "11/11/2024", "Hoàn thành", 4.9f));
        return historyList;
    }
}