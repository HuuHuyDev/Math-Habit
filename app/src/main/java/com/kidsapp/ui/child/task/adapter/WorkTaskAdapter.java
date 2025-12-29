package com.kidsapp.ui.child.task.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.kidsapp.R;
import com.kidsapp.data.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho danh sách công việc (HABIT tasks)
 * Hiển thị các task cần hoàn thành với giao diện đẹp
 */
public class WorkTaskAdapter extends RecyclerView.Adapter<WorkTaskAdapter.TaskViewHolder> {
    
    private List<Task> tasks = new ArrayList<>();
    private OnTaskActionListener listener;
    
    public interface OnTaskActionListener {
        void onTaskClick(Task task);
        void onCompleteClick(Task task);
    }
    
    public void setOnTaskActionListener(OnTaskActionListener listener) {
        this.listener = listener;
    }
    
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void removeTask(Task task) {
        int position = tasks.indexOf(task);
        if (position != -1) {
            tasks.remove(position);
            notifyItemRemoved(position);
        }
    }
    
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_work_task, parent, false);
        return new TaskViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }
    
    @Override
    public int getItemCount() {
        return tasks.size();
    }
    
    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardTask;
        private final ImageView ivTaskIcon;
        private final TextView tvStatusBadge;
        private final TextView tvTitle;
        private final TextView tvDescription;
        private final TextView tvType;
        private final TextView tvCoin;
        private final TextView tvPoints;
        private final TextView tvDueTime;
        private final TextView tvPriority;
        private final MaterialButton btnComplete;
        
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTask = itemView.findViewById(R.id.cardTask);
            ivTaskIcon = itemView.findViewById(R.id.ivTaskIcon);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvType = itemView.findViewById(R.id.tvType);
            tvCoin = itemView.findViewById(R.id.tvCoin);
            tvPoints = itemView.findViewById(R.id.tvPoints);
            tvDueTime = itemView.findViewById(R.id.tvDueTime);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            btnComplete = itemView.findViewById(R.id.btnComplete);
        }
        
        public void bind(Task task) {
            // Title và Description
            tvTitle.setText(task.getTitle() != null ? task.getTitle() : "");
            tvDescription.setText(task.getDescription() != null ? task.getDescription() : "");
            
            // Hiển thị coin và XP
            int coins = task.getCoinsReward();
            int xp = task.getPointsReward();
            tvCoin.setText("🪙 +" + coins);
            tvPoints.setText("+" + xp + " XP");
            
            // Task type badge
            setupTaskType(task);
            
            // Due time
            setupDueTime(task);
            
            // Priority indicator
            setupPriority(task);
            
            // Status badge (nếu đã submit chờ duyệt)
            setupStatusBadge(task);
            
            // Icon theo loại task
            setupTaskIcon(task);
            
            // Button state
            setupButton(task);
            
            // Kiểm tra trạng thái COMPLETED - đổi màu xanh lá
            boolean isCompleted = "COMPLETED".equalsIgnoreCase(task.getStatus());
            if (isCompleted) {
                cardTask.setStrokeColor(ContextCompat.getColor(itemView.getContext(), R.color.status_success));
                cardTask.setStrokeWidth(2);
                ivTaskIcon.setBackgroundResource(R.drawable.bg_icon_completed);
            } else {
                cardTask.setStrokeWidth(0);
            }
            
            // Click listeners
            cardTask.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(task);
                }
            });
            
            btnComplete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCompleteClick(task);
                }
            });
        }
        
        private void setupTaskType(Task task) {
            String type = task.getTaskType();
            if ("HABIT".equalsIgnoreCase(type)) {
                tvType.setText("Thói quen");
                tvType.setBackgroundResource(R.drawable.bg_tag_primary);
                tvType.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.primary));
            } else if ("EXERCISE".equalsIgnoreCase(type)) {
                tvType.setText("Bài tập");
                tvType.setBackgroundResource(R.drawable.bg_tag_exercise);
                tvType.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.exercise_color));
            } else {
                tvType.setText("Công việc");
                tvType.setBackgroundResource(R.drawable.bg_tag_primary);
                tvType.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.primary));
            }
        }
        
        private void setupDueTime(Task task) {
            String dueTime = task.getDueTime();
            if (dueTime != null && !dueTime.isEmpty()) {
                tvDueTime.setText("⏰ " + dueTime);
                tvDueTime.setVisibility(View.VISIBLE);
            } else {
                tvDueTime.setVisibility(View.GONE);
            }
        }
        
        private void setupPriority(Task task) {
            int priority = task.getPriority();
            switch (priority) {
                case 3:
                    tvPriority.setText("🔴");
                    break;
                case 2:
                    tvPriority.setText("🟡");
                    break;
                case 1:
                default:
                    tvPriority.setText("🟢");
                    break;
            }
        }
        
        private void setupStatusBadge(Task task) {
            String status = task.getStatus();
            if ("SUBMITTED".equalsIgnoreCase(status)) {
                // Đã nộp, chờ duyệt
                tvStatusBadge.setVisibility(View.VISIBLE);
                tvStatusBadge.setText("⏳");
                tvStatusBadge.setBackgroundResource(R.drawable.bg_status_pending);
            } else if ("REJECTED".equalsIgnoreCase(status)) {
                // Bị từ chối
                tvStatusBadge.setVisibility(View.VISIBLE);
                tvStatusBadge.setText("!");
                tvStatusBadge.setBackgroundResource(R.drawable.bg_status_rejected);
            } else {
                tvStatusBadge.setVisibility(View.GONE);
            }
        }
        
        private void setupTaskIcon(Task task) {
            String type = task.getTaskType();
            if ("HABIT".equalsIgnoreCase(type)) {
                ivTaskIcon.setImageResource(R.drawable.ic_task_habit);
                ivTaskIcon.setBackgroundResource(R.drawable.bg_icon_habit);
            } else if ("EXERCISE".equalsIgnoreCase(type)) {
                ivTaskIcon.setImageResource(R.drawable.ic_task_exercise);
                ivTaskIcon.setBackgroundResource(R.drawable.bg_icon_exercise);
            } else {
                ivTaskIcon.setImageResource(R.drawable.ic_task_habit);
                ivTaskIcon.setBackgroundResource(R.drawable.bg_icon_circle);
            }
        }
        
        private void setupButton(Task task) {
            String status = task.getStatus();
            if ("SUBMITTED".equalsIgnoreCase(status)) {
                // Đã nộp, chờ duyệt - disable button
                btnComplete.setEnabled(false);
                btnComplete.setAlpha(0.5f);
                btnComplete.setIconResource(R.drawable.ic_check);
            } else if ("REJECTED".equalsIgnoreCase(status)) {
                // Bị từ chối - cho phép nộp lại
                btnComplete.setEnabled(true);
                btnComplete.setAlpha(1f);
                btnComplete.setIconResource(R.drawable.ic_refresh);
                btnComplete.setBackgroundTintList(ContextCompat.getColorStateList(
                        itemView.getContext(), R.color.warning_color));
            } else {
                // PENDING - cho phép nộp
                btnComplete.setEnabled(true);
                btnComplete.setAlpha(1f);
                btnComplete.setIconResource(R.drawable.ic_camera);
                btnComplete.setBackgroundTintList(ContextCompat.getColorStateList(
                        itemView.getContext(), R.color.primary));
            }
        }
    }
}
