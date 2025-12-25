package com.kidsapp.ui.parent.child.detail.tabs.housework;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kidsapp.R;

import java.util.List;

/**
 * Adapter cho RecyclerView hiển thị danh sách việc nhà
 */
public class HouseworkAdapter extends RecyclerView.Adapter<HouseworkAdapter.HouseworkViewHolder> {

    private List<HouseworkTask> taskList;

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
        int colorGreenLight = ContextCompat.getColor(holder.itemView.getContext(), R.color.surface_variant);
        int colorGray = ContextCompat.getColor(holder.itemView.getContext(), R.color.text_tertiary);
        int colorWhite = ContextCompat.getColor(holder.itemView.getContext(), R.color.white);
        int colorSurface = ContextCompat.getColor(holder.itemView.getContext(), R.color.surface);
        
        // Cập nhật trạng thái dựa trên isCompleted
        if (task.isCompleted()) {
            // Đã hoàn thành
            holder.cardView.setCardBackgroundColor(colorGreenLight);
            holder.cardView.setCardElevation(1);
            
            // Checkbox: nền xanh, hiện checkmark trắng
            holder.containerCheckIcon.setBackgroundResource(R.drawable.bg_checkmark_square);
            holder.imgCheckIcon.setVisibility(View.VISIBLE);
            
            // Task icon: nền xanh, icon trắng
            holder.containerTaskIcon.setBackgroundResource(R.drawable.bg_task_icon_checked);
            holder.imgIcon.setColorFilter(colorWhite);
            
            // Text trạng thái: màu xanh
            holder.txtStatus.setText("Đã hoàn thành");
            holder.txtStatus.setTextColor(colorGreen);
        } else {
            // Chưa làm
            holder.cardView.setCardBackgroundColor(colorSurface);
            holder.cardView.setCardElevation(2);
            
            // Checkbox: viền xám, ẩn checkmark
            holder.containerCheckIcon.setBackgroundResource(R.drawable.bg_checkbox_unchecked);
            holder.imgCheckIcon.setVisibility(View.GONE);
            
            // Task icon: nền xám, icon xám
            holder.containerTaskIcon.setBackgroundResource(R.drawable.bg_task_icon_unchecked);
            holder.imgIcon.setColorFilter(colorGray);
            
            // Text trạng thái: màu xám
            holder.txtStatus.setText("Chưa làm");
            holder.txtStatus.setTextColor(colorGray);
        }
        
        // Parent chỉ xem, không thể click để đánh dấu hoàn thành
        // Việc đánh dấu hoàn thành do Child thực hiện
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    static class HouseworkViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView imgIcon;
        ImageView imgCheckIcon;
        TextView txtTitle;
        TextView txtStatus;
        View containerCheckIcon;
        View containerTaskIcon;

        HouseworkViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardHousework);
            imgIcon = itemView.findViewById(R.id.imgHouseworkIcon);
            imgCheckIcon = itemView.findViewById(R.id.imgCheckIcon);
            txtTitle = itemView.findViewById(R.id.txtHouseworkTitle);
            txtStatus = itemView.findViewById(R.id.txtHouseworkStatus);
            containerCheckIcon = itemView.findViewById(R.id.containerCheckIcon);
            containerTaskIcon = itemView.findViewById(R.id.containerTaskIcon);
        }
    }
}
