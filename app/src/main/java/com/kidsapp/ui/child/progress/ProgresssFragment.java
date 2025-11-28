package com.kidsapp.ui.child.progress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kidsapp.data.model.Habit;
import com.kidsapp.R;

import java.util.ArrayList;
import java.util.List;

public class ProgresssFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    // --- UI ---
    private RecyclerView rvHabits;
    private TextView tvHabitCount;
    private TextView tvPercent;
    private TextView tvHabitFraction;
    private ProgressBar pbHabit;
    private Button btnCompleteRest;

    // --- Data ---
    private List<Habit> habitList;
    private HabitAdapter adapter;

    public ProgresssFragment() {
        // Required empty public constructor
    }

    public static ProgresssFragment newInstance(String param1, String param2) {
        ProgresssFragment fragment = new ProgresssFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    //nút back
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View btnBack = view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate đúng layout fragment_progresss
        View view = inflater.inflate(R.layout.fragment_progresss, container, false);

        // Ánh xạ view
        rvHabits = view.findViewById(R.id.rvHabits103);
        tvHabitCount = view.findViewById(R.id.tvHabitCount103);
        tvPercent = view.findViewById(R.id.tvPercent103);
        tvHabitFraction = view.findViewById(R.id.tvHabitFraction103);
        pbHabit = view.findViewById(R.id.pbHabit103);
        btnCompleteRest = view.findViewById(R.id.btnCompleteRest103);

        // Khởi tạo data mẫu
        setupHabitData();

        // Setup RecyclerView
        setupRecyclerView();

        // Cập nhật UI lần đầu
        updateProgressUI();

        // Xử lý nút "Hoàn thành phần còn lại"
        btnCompleteRest.setOnClickListener(v -> {
            for (Habit habit : habitList) {
                habit.setDone(true);
            }
            adapter.notifyDataSetChanged();
            updateProgressUI();
        });

        return view;
    }

    private void setupHabitData() {
        habitList = new ArrayList<>();
        habitList.add(new Habit("Đánh răng", true));
        habitList.add(new Habit("Dọn đồ chơi", true));
        habitList.add(new Habit("Đọc sách", false));


    }

    private void setupRecyclerView() {
        adapter = new HabitAdapter(habitList, this::updateProgressUI);
        rvHabits.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHabits.setAdapter(adapter);
        // Vì RecyclerView nằm trong ScrollView:
        rvHabits.setNestedScrollingEnabled(false);
    }

    private void updateProgressUI() {
        int total = habitList.size();
        int done = 0;
        for (Habit h : habitList) {
            if (h.isDone()) done++;
        }

        // 2/3
        tvHabitCount.setText(done + "/" + total);

        // Progress bar
        pbHabit.setMax(total);
        pbHabit.setProgress(done);

        // %
        int percent = (int) (done * 100f / total);
        tvPercent.setText(percent + "%");

        // Text mô tả
        tvHabitFraction.setText(done + "/" + total + " thói quen đã hoàn thành");
    }
}
