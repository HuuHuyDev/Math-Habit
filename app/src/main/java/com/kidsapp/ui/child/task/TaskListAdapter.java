package com.kidsapp.ui.child.task;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kidsapp.R;
import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public TaskListAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgTask;
        private TextView txtTaskTitle;
        private TextView txtQuestionCount;
        private TextView txtDuration;
        private TextView txtRating;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTask = itemView.findViewById(R.id.imgTask);
            txtTaskTitle = itemView.findViewById(R.id.txtTaskTitle);
            txtQuestionCount = itemView.findViewById(R.id.txtQuestionCount);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            txtRating = itemView.findViewById(R.id.txtRating);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onTaskClick(taskList.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Task task) {
            imgTask.setImageResource(task.getImageRes());
            txtTaskTitle.setText(task.getTitle());
            txtQuestionCount.setText(task.getQuestionCount() + " câu hỏi");
            txtDuration.setText(task.getDuration() + " phút");
            txtRating.setText(String.format("%.1f", task.getRating()));
        }
    }
}

