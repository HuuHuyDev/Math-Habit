package com.kidsapp.ui.parent.child.detail.tabs.housework;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.kidsapp.R;

import java.util.List;

/**
 * Adapter cho RecyclerView hiển thị danh sách HABIT tasks
 * Parent có thể approve/reject các task đã submit
 */
public class HouseworkAdapter extends RecyclerView.Adapter<HouseworkAdapter.HouseworkViewHolder> {

    private List<HouseworkTask> taskList;
    private OnTaskActionListener listener;
    
    public interface OnTaskActionListener {
        void onApproveClick(HouseworkTask task);
        void onRejectClick(HouseworkTask task);
        void onViewProofClick(HouseworkTask task);
    }
    
    public void setOnTaskActionListener(OnTaskActionListener listener) {
        this.listener = listener;
    }

    public HouseworkAdapter(List<HouseworkTask> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public HouseworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_housework, parent, false);
        return new HouseworkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HouseworkViewHolder holder, int position) {
        HouseworkTask task = taskList.get(position);
        
        holder.txtTitle.setText(task.getTitle());
        holder.imgIcon.setImageResource(task.getIconRes());
        
        int colorGreen = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_success);
        int colorOrange = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_warning);
        int colorGreenLight = ContextCompat.getColor(holder.itemView.getContext(), R.color.surface_variant);
        int colorGray = ContextCompat.getColor(holder.itemView.getContext(), R.color.text_tertiary);
        int colorWhite = ContextCompat.getColor(holder.itemView.getContext(), R.color.white);
        int colorSurface = ContextCompat.getColor(holder.itemView.getContext(), R.color.surface);
        
        // Ẩn action buttons mặc định
        if (holder.layoutActions != null) {
            holder.layoutActions.setVisibility(View.GONE);
        }
        
        String status = task.getStatus();
        
        if (task.isCompleted()) {
            // Đã hoàn thành (COMPLETED)
            holder.cardView.setCardBackgroundColor(colorGreenLight);
            holder.cardView.setCardElevation(1);
            
            holder.containerCheckIcon.setBackgroundResource(R.drawable.bg_checkmark_square);
            holder.imgCheckIcon.setVisibility(View.VISIBLE);
            
            holder.containerTaskIcon.setBackgroundResource(R.drawable.bg_task_icon_checked);
            holder.imgIcon.setColorFilter(colorWhite);
            
            holder.txtStatus.setText("Đã hoàn thành");
            holder.txtStatus.setTextColor(colorGreen);
            
        } else if (task.needsApproval()) {
            // Đã submit, chờ duyệt (SUBMITTED)
            holder.cardView.setCardBackgroundColor(colorSurface);
            holder.cardView.setCardElevation(2);
            
            holder.containerCheckIcon.setBackgroundResource(R.drawable.bg_checkbox_unchecked);
            holder.imgCheckIcon.setVisibility(View.GONE);
            
            holder.containerTaskIcon.setBackgroundResource(R.drawable.bg_task_icon_unchecked);
            holder.imgIcon.setColorFilter(colorOrange);
            
            holder.txtStatus.setText("⏳ Chờ duyệt");
            holder.txtStatus.setTextColor(colorOrange);
            
            // Hiển thị action buttons
            if (holder.layoutActions != null) {
                holder.layoutActions.setVisibility(View.VISIBLE);
            }
            
        } else {
            // Chưa làm (PENDING)
            holder.cardView.setCardBackgroundColor(colorSurface);
            holder.cardView.setCardElevation(2);
            
            holder.containerCheckIcon.setBackgroundResource(R.drawable.bg_checkbox_unchecked);
            holder.imgCheckIcon.setVisibility(View.GONE);
            
            holder.containerTaskIcon.setBackgroundResource(R.drawable.bg_task_icon_unchecked);
            holder.imgIcon.setColorFilter(colorGray);
            
            holder.txtStatus.setText("Chưa làm");
            holder.txtStatus.setTextColor(colorGray);
        }
        
        // Setup click listeners
        if (holder.btnApprove != null) {
            holder.btnApprove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onApproveClick(task);
                }
            });
        }
        
        if (holder.btnReject != null) {
            holder.btnReject.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRejectClick(task);
                }
            });
        }
        
        if (holder.btnViewProof != null) {
            holder.btnViewProof.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewProofClick(task);
                }
            });
        }
        
        // Card click to view proof if submitted or completed (has proof)
        holder.cardView.setOnClickListener(v -> {
            if (listener != null && task.getProofUrl() != null && !task.getProofUrl().isEmpty()) {
                listener.onViewProofClick(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    public void updateData(List<HouseworkTask> newTaskList) {
        this.taskList = newTaskList;
        notifyDataSetChanged();
    }

    static class HouseworkViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView imgIcon;
        ImageView imgCheckIcon;
        TextView txtTitle;
        TextView txtStatus;
        View containerCheckIcon;
        View containerTaskIcon;
        
        // Action buttons for SUBMITTED tasks
        LinearLayout layoutActions;
        MaterialButton btnApprove;
        MaterialButton btnReject;
        MaterialButton btnViewProof;

        HouseworkViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardHousework);
            imgIcon = itemView.findViewById(R.id.imgHouseworkIcon);
            imgCheckIcon = itemView.findViewById(R.id.imgCheckIcon);
            txtTitle = itemView.findViewById(R.id.txtHouseworkTitle);
            txtStatus = itemView.findViewById(R.id.txtHouseworkStatus);
            containerCheckIcon = itemView.findViewById(R.id.containerCheckIcon);
            containerTaskIcon = itemView.findViewById(R.id.containerTaskIcon);
            
            // Action buttons (may be null if not in layout)
            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnViewProof = itemView.findViewById(R.id.btnViewProof);
        }
    }
}
