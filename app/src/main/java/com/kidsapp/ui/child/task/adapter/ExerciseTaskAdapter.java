package com.kidsapp.ui.child.task.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.kidsapp.R;
import com.kidsapp.data.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho danh sách bài tập (EXERCISE tasks)
 */
public class ExerciseTaskAdapter extends RecyclerView.Adapter<ExerciseTaskAdapter.ViewHolder> {
    
    private List<Task> tasks = new ArrayList<>();
    private OnTaskClickListener listener;
    
    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }
    
    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.listener = listener;
    }
    
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_task, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(tasks.get(position));
    }
    
    @Override
    public int getItemCount() {
        return tasks.size();
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardTask;
        private final ImageView ivIcon;
        private final ImageView ivCompleted;
        private final TextView tvTitle;
        private final TextView tvDescription;
        private final TextView tvPoints;
        private final TextView tvDueTime;
        private final MaterialButton btnStart;
        
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTask = itemView.findViewById(R.id.cardTask);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            ivCompleted = itemView.findViewById(R.id.ivCompleted);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPoints = itemView.findViewById(R.id.tvPoints);
            tvDueTime = itemView.findViewById(R.id.tvDueTime);
            btnStart = itemView.findViewById(R.id.btnStart);
        }
        
        void bind(Task task) {
            tvTitle.setText(task.getTitle() != null ? task.getTitle() : "");
            tvDescription.setText(task.getDescription() != null ? task.getDescription() : "");
            tvPoints.setText("+" + task.getPointsReward() + " ⭐");
            
            // Due time
            if (task.getDueTime() != null && !task.getDueTime().isEmpty()) {
                tvDueTime.setText("⏰ " + task.getDueTime());
                tvDueTime.setVisibility(View.VISIBLE);
            } else {
                tvDueTime.setVisibility(View.GONE);
            }
            
            // Icon
            ivIcon.setImageResource(R.drawable.ic_task_exercise);
            
            // Kiểm tra trạng thái COMPLETED
            boolean isCompleted = "COMPLETED".equalsIgnoreCase(task.getStatus());
            
            if (isCompleted) {
                // Đã hoàn thành - hiện badge và đổi style
                ivCompleted.setVisibility(View.VISIBLE);
                btnStart.setText("Đã xong");
                btnStart.setEnabled(false);
                btnStart.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    itemView.getContext().getResources().getColor(R.color.status_success)));
                cardTask.setAlpha(0.85f);
            } else {
                // Chưa hoàn thành
                ivCompleted.setVisibility(View.GONE);
                btnStart.setText("Làm bài");
                btnStart.setEnabled(true);
                btnStart.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    itemView.getContext().getResources().getColor(R.color.exercise_color)));
                cardTask.setAlpha(1.0f);
            }
            
            // Click listeners
            cardTask.setOnClickListener(v -> {
                if (listener != null) listener.onTaskClick(task);
            });
            
            btnStart.setOnClickListener(v -> {
                if (listener != null && !isCompleted) listener.onTaskClick(task);
            });
        }
    }
}
