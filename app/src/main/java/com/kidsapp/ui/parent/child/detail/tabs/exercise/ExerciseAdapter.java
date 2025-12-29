package com.kidsapp.ui.parent.child.detail.tabs.exercise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
        holder.imgIcon.setImageResource(task.getIconRes());
        
        // Hiển thị trạng thái: Đã làm / Chưa làm
        if (task.isCompleted()) {
            holder.txtScore.setText("✅ Đã làm");
            holder.txtScore.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_success));
        } else {
            holder.txtScore.setText("⏳ Chưa làm");
            holder.txtScore.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary));
        }
        
        // Hiển thị coin và XP
        holder.txtCoin.setText("🪙 +" + task.getCoinReward());
        holder.txtXP.setText("+" + task.getXp() + " XP");
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    public void updateData(List<ExerciseTask> newTaskList) {
        this.taskList = newTaskList;
        notifyDataSetChanged();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtScore;
        TextView txtCoin;
        TextView txtXP;

        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgExerciseIcon);
            txtTitle = itemView.findViewById(R.id.txtExerciseTitle);
            txtScore = itemView.findViewById(R.id.txtExerciseScore);
            txtCoin = itemView.findViewById(R.id.txtExerciseCoin);
            txtXP = itemView.findViewById(R.id.txtExerciseXP);
        }
    }
}
