package com.example.mathhabit;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathhabit.adapter.HabitAdapter;
import com.example.mathhabit.model.Habit;

import java.util.ArrayList;
import java.util.List;

public class ProgressActivity extends AppCompatActivity {

    private RecyclerView rvHabits;
    private TextView tvHabitCount, tvPercent, tvHabitFraction;
    private ProgressBar pbHabit;
    private HabitAdapter adapter;
    private List<Habit> habitList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // Ánh xạ view
        rvHabits = findViewById(R.id.rvHabits103);
        tvHabitCount = findViewById(R.id.tvHabitCount103);
        tvPercent = findViewById(R.id.tvPercent103);
        tvHabitFraction = findViewById(R.id.tvHabitFraction103);
        pbHabit = findViewById(R.id.pbHabit103);
        Button btnCompleteRest = findViewById(R.id.btnCompleteRest103);

        // Data mẫu
        habitList = new ArrayList<>();
        habitList.add(new Habit("Đánh răng", true));
        habitList.add(new Habit("Dọn đồ chơi", true));
        habitList.add(new Habit("Đọc sách", false));
        habitList.add(new Habit("Uống nước", true));

        // Setup RecyclerView
        adapter = new HabitAdapter(habitList, this::updateProgressUI);
        rvHabits.setLayoutManager(new LinearLayoutManager(this));
        rvHabits.setAdapter(adapter);

        // Thiết lập max cho progress: tổng số thói quen
        pbHabit.setMax(habitList.size());

        // Cập nhật UI lần đầu
        updateProgressUI();

        btnCompleteRest.setOnClickListener(v -> {
            // Ví dụ: auto tick tất cả thói quen còn lại
            for (Habit h : habitList) {
                h.setDone(true);
            }
            adapter.notifyDataSetChanged();
            updateProgressUI();
        });
    }

    private void updateProgressUI() {
        int total = habitList.size();
        int done = 0;
        for (Habit h : habitList) {
            if (h.isDone()) done++;
        }

        // 3/4
        tvHabitCount.setText(done + "/" + total);
        pbHabit.setProgress(done);

        // % hoàn thành
        int percent = (int) (done * 100f / total);
        tvPercent.setText(percent + "%");
        tvHabitFraction.setText(done + "/" + total + " thói quen đã hoàn thành");
    }
}

