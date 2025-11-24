package com.kidsapp.ui.child.progress;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import com.kidsapp.data.model.Habit;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;

import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private List<Habit> habitList;
    private OnHabitChangedListener listener;

    public interface OnHabitChangedListener {
        void onHabitChanged();
    }

    public HabitAdapter(List<Habit> habitList, OnHabitChangedListener listener) {
        this.habitList = habitList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habitList.get(position);

        holder.txtHabitName.setText(habit.getTitle());
        holder.checkBox.setChecked(habit.isDone());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            habit.setDone(isChecked);

            if (listener != null) {
                listener.onHabitChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView txtHabitName;
        CheckBox checkBox;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            txtHabitName = itemView.findViewById(R.id.txtHabitName103);
            checkBox = itemView.findViewById(R.id.checkboxHabit103);
        }
    }
}
