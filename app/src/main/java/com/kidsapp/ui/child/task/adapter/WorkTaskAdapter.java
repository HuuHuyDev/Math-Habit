package com.kidsapp.ui.child.task.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Adapter cho danh sách công việc (housework, habit, custom)
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
        private MaterialCardView cardTask;
        private TextView tvTitle;
        private TextView tvDescription;
        private TextView tvType;
        private TextView tvPoints;
        private TextView tvDueTime;
        private TextView tvPriority;
        private MaterialButton btnComplete;
        
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTask = itemView.findViewById(R.id.cardTask);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvType = itemView.findViewById(R.id.tvType);
            tvPoints = itemView.findViewById(R.id.tvPoints);
            tvDueTime = itemView.findViewById(R.id.tvDueTime);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            btnComplete = itemView.findViewById(R.id.btnComplete);
        }
        
        public void bind(Task task) {
            tvTitle.setText(task.getTitle());
            tvDescription.setText(task.getDescription());
            tvPoints.setText("+" + task.getPointsReward() + " điểm");
            
            // Hiển thị loại công việc
            String typeText = getTaskTypeText(task.getTaskType());
            tvType.setText(typeText);
            
            // Hiển thị thời gian
            if (task.getDueTime() != null) {
                tvDueTime.setText("⏰ " + task.getDueTime());
                tvDueTime.setVisibility(View.VISIBLE);
            } else {
                tvDueTime.setVisibility(View.GONE);
            }
            
            // Hiển thị độ ưu tiên
            String priorityText = getPriorityText(task.getPriority());
            tvPriority.setText(priorityText);
            setPriorityColor(task.getPriority());
            
            // Xử lý click
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
        
        private String getTaskTypeText(String type) {
            switch (type) {
                case "housework":
                    return "🏠 Việc nhà";
                case "habit":
                    return "⭐ Thói quen";
                case "custom":
                    return "✨ Tùy chỉnh";
                default:
                    return "📝 Công việc";
            }
        }
        
        private String getPriorityText(int priority) {
            switch (priority) {
                case 3:
                    return "🔴 Cao";
                case 2:
                    return "🟡 Trung bình";
                case 1:
                default:
                    return "🟢 Thấp";
            }
        }
        
        private void setPriorityColor(int priority) {
            int colorRes;
            switch (priority) {
                case 3:
                    colorRes = R.color.priority_high;
                    break;
                case 2:
                    colorRes = R.color.priority_medium;
                    break;
                case 1:
                default:
                    colorRes = R.color.priority_low;
                    break;
            }
            tvPriority.setTextColor(itemView.getContext().getColor(colorRes));
        }
    }
}
