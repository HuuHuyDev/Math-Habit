package com.kidsapp.ui.child.task;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.data.model.HistoryTask;
import com.kidsapp.databinding.ItemHistoryTaskBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho danh sách lịch sử nhiệm vụ đã hoàn thành
 */
public class TaskHistoryAdapter extends RecyclerView.Adapter<TaskHistoryAdapter.HistoryViewHolder> {
    
    /**
     * Interface để xử lý sự kiện click vào nút "Xem chi tiết"
     */
    public interface OnHistoryClickListener {
        void onViewDetailsClick(HistoryTask history);
    }
    
    private List<HistoryTask> historyList;
    private OnHistoryClickListener listener;

    public TaskHistoryAdapter(List<HistoryTask> historyList) {
        this.historyList = historyList != null ? historyList : new ArrayList<>();
    }
    
    public void setOnHistoryClickListener(OnHistoryClickListener listener) {
        this.listener = listener;
    }

    public void updateList(List<HistoryTask> newList) {
        this.historyList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryTaskBinding binding = ItemHistoryTaskBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new HistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryTask history = historyList.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemHistoryTaskBinding binding;

        public HistoryViewHolder(@NonNull ItemHistoryTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(HistoryTask history) {
            // Bind dữ liệu cơ bản
            binding.txtTaskTitle.setText(history.getTitle());
            binding.txtCompletionTime.setText("Hoàn thành: " + history.getCompletionTime());
            binding.txtCoins.setText(String.valueOf(history.getCoins()));
            binding.txtXp.setText(history.getXp() + " XP");
            binding.imgTaskIcon.setImageResource(history.getIconRes());

            // Hiển thị rating nếu có
            if (history.hasRating()) {
                binding.layoutRating.setVisibility(View.VISIBLE);
                binding.txtRating.setText(String.format("%.1f", history.getRating()));
            } else {
                binding.layoutRating.setVisibility(View.GONE);
            }

            // Xử lý click nút "Xem chi tiết"
            binding.btnViewDetails.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewDetailsClick(history);
                }
            });
        }
    }
}
