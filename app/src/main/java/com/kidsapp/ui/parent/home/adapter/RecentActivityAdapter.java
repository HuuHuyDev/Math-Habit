package com.kidsapp.ui.parent.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.data.model.ActivityLog;

import java.util.List;

public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.ActivityViewHolder> {

    private final List<ActivityLog> items;

    public RecentActivityAdapter(List<ActivityLog> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvActivityText;
        private final TextView tvActivityXp;
        private final TextView tvActivityTime;

        ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvActivityText = itemView.findViewById(R.id.tvActivityText);
            tvActivityXp = itemView.findViewById(R.id.tvActivityXp);
            tvActivityTime = itemView.findViewById(R.id.tvActivityTime);
        }

        void bind(ActivityLog log) {
            itemView.startAnimation(AnimationUtils.loadAnimation(
                    itemView.getContext(), R.anim.item_fade_in));

            tvActivityText.setText(log.getChildName() + " " + log.getAction());
            tvActivityXp.setText(String.format("+%d XP", log.getXpEarned()));
            tvActivityTime.setText(log.getCreatedAt());
        }
    }
}

