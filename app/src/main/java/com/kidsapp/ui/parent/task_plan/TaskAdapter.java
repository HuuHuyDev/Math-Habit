package com.kidsapp.ui.parent.task_plan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.databinding.ItemWeekTaskBinding;
import com.kidsapp.ui.parent.task_plan.model.WeekTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho danh sách nhiệm vụ
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<WeekTask> tasks = new ArrayList<>();
    private OnTaskActionListener listener;

    public interface OnTaskActionListener {
        void onEditTask(WeekTask task, int position);
        void onDeleteTask(WeekTask task, int position);
    }

    public void setOnTaskActionListener(OnTaskActionListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<WeekTask> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public void addTask(WeekTask task) {
        tasks.add(task);
        notifyItemInserted(tasks.size() - 1);
    }

    public void updateTask(int position, WeekTask task) {
        if (position >= 0 && position < tasks.size()) {
            tasks.set(position, task);
            notifyItemChanged(position);
        }
    }

    public void removeTask(int position) {
        if (position >= 0 && position < tasks.size()) {
            tasks.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWeekTaskBinding binding = ItemWeekTaskBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final ItemWeekTaskBinding binding;

        public TaskViewHolder(ItemWeekTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.btnEditTask.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEditTask(tasks.get(position), position);
                }
            });

            binding.btnDeleteTask.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteTask(tasks.get(position), position);
                }
            });
        }

        public void bind(WeekTask task) {
            binding.txtTaskTitle.setText(task.getTitle());
            binding.txtTaskCoins.setText("+" + task.getCoins());
            binding.txtTaskXp.setText("+" + task.getXp() + " XP");

            // Set màu sắc và icon theo loại task
            if (task.isHabit()) {
                // Habit - màu xanh lá với icon sách
                binding.cardTask.setBackgroundResource(R.drawable.bg_task_habit);
                binding.frameTaskIcon.setBackgroundResource(R.drawable.bg_task_icon_habit);
                binding.imgTaskIcon.setImageResource(R.drawable.ic_book);
                binding.txtTaskLevel.setVisibility(View.GONE);
            } else {
                // Quiz - màu xanh dương với icon target
                binding.cardTask.setBackgroundResource(R.drawable.bg_task_quiz);
                binding.frameTaskIcon.setBackgroundResource(R.drawable.bg_task_icon_quiz);
                binding.imgTaskIcon.setImageResource(R.drawable.ic_target);
                binding.txtTaskLevel.setVisibility(View.VISIBLE);
                binding.txtTaskLevel.setText("Độ khó: " + task.getLevelText());
            }
        }
    }
}
