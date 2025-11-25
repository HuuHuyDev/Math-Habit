package com.kidsapp.ui.parent.child.detail.tabs.exercise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;

import java.util.List;

/**
 * Adapter cho RecyclerView hiển thị danh sách bài tập
 */
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private List<ExerciseTask> taskList;

    public ExerciseAdapter(List<ExerciseTask> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        ExerciseTask task = taskList.get(position);
        
        holder.txtTitle.setText(task.getTitle());
        holder.txtScore.setText(String.format("%d/%d đúng", task.getCorrectAnswers(), task.getTotalQuestions()));
        holder.txtXP.setText(String.format("+%d XP", task.getXp()));
        holder.imgIcon.setImageResource(task.getIconRes());
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtScore;
        TextView txtXP;

        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgExerciseIcon);
            txtTitle = itemView.findViewById(R.id.txtExerciseTitle);
            txtScore = itemView.findViewById(R.id.txtExerciseScore);
            txtXP = itemView.findViewById(R.id.txtExerciseXP);
        }
    }
}
