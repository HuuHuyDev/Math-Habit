package com.example.mathhabit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathhabit.R;
import com.example.mathhabit.model.Habit;

import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    public interface OnHabitCheckedChange {
        void onHabitChanged();
    }

    private List<Habit> habits;
    private OnHabitCheckedChange listener;

    public HabitAdapter(List<Habit> habits, OnHabitCheckedChange listener) {
        this.habits = habits;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habits.get(position);
        holder.tvName.setText(habit.getName());
        holder.cbDone.setOnCheckedChangeListener(null);
        holder.cbDone.setChecked(habit.isDone());

        holder.cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            habit.setDone(isChecked);
            if (listener != null) {
                listener.onHabitChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    static class HabitViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbDone;
        TextView tvName;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            cbDone = itemView.findViewById(R.id.cbHabit103);
            tvName = itemView.findViewById(R.id.tvHabitName103);
        }
    }
}

