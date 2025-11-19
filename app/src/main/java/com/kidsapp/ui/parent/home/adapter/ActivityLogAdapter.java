package com.kidsapp.ui.parent.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kidsapp.R;
import com.kidsapp.data.model.ActivityLog;
import com.kidsapp.databinding.ItemActivityLogBinding;
import com.kidsapp.utils.DateUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for Activity Log RecyclerView
 */
public class ActivityLogAdapter extends RecyclerView.Adapter<ActivityLogAdapter.ViewHolder> {
    private List<ActivityLog> activityLogs;

    public ActivityLogAdapter(List<ActivityLog> activityLogs) {
        this.activityLogs = activityLogs != null ? activityLogs : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemActivityLogBinding binding = ItemActivityLogBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityLog activityLog = activityLogs.get(position);
        holder.bind(activityLog);
    }

    @Override
    public int getItemCount() {
        return activityLogs.size();
    }

    public void updateList(List<ActivityLog> newList) {
        this.activityLogs = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemActivityLogBinding binding;

        ViewHolder(ItemActivityLogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ActivityLog activityLog) {
            binding.tvName.setText(activityLog.getChildName());
            binding.tvAction.setText(activityLog.getAction());
            binding.tvXP.setText("+" + activityLog.getXpEarned() + " XP");
            
            // Format time
            String timeAgo = DateUtils.getTimeAgo(activityLog.getCreatedAt());
            binding.tvTime.setText(timeAgo);
        }
    }
}

