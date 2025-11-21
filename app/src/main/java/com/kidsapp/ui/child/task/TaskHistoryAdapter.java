package com.kidsapp.ui.child.task;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kidsapp.R;
import java.util.List;

public class TaskHistoryAdapter extends RecyclerView.Adapter<TaskHistoryAdapter.HistoryViewHolder> {
    private List<TaskHistory> historyList;

    public TaskHistoryAdapter(List<TaskHistory> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        TaskHistory history = historyList.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView txtHistoryTitle;
        private TextView txtHistoryDate;
        private TextView txtHistoryResult;
        private TextView txtHistoryRating;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtHistoryTitle = itemView.findViewById(R.id.txtHistoryTitle);
            txtHistoryDate = itemView.findViewById(R.id.txtHistoryDate);
            txtHistoryResult = itemView.findViewById(R.id.txtHistoryResult);
            txtHistoryRating = itemView.findViewById(R.id.txtHistoryRating);
        }

        public void bind(TaskHistory history) {
            txtHistoryTitle.setText(history.getTitle());
            txtHistoryDate.setText(history.getDate());
            txtHistoryResult.setText(history.getResult());
            txtHistoryRating.setText(String.format("%.1f", history.getRating()));
        }
    }
}

