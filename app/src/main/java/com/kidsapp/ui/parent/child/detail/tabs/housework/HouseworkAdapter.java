package com.kidsapp.ui.parent.child.detail.tabs.housework;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kidsapp.R;

import java.util.List;

/**
 * Adapter cho RecyclerView hiển thị danh sách việc nhà
 */
public class HouseworkAdapter extends RecyclerView.Adapter<HouseworkAdapter.HouseworkViewHolder> {

    private List<HouseworkTask> taskList;
    
    // Màu sắc
    private static final int COLOR_GREEN = 0xFF4CAF50;
    private static final int COLOR_GREEN_LIGHT = 0xFFE8F5E9;
    private static final int COLOR_GREEN_BORDER = 0xFFA5D6A7;
    private static final int COLOR_GRAY = 0xFF9E9E9E;
    private static final int COLOR_GRAY_BORDER = 0xFFE0E0E0;
    private static final int COLOR_WHITE = 0xFFFFFFFF;

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
        
        // Cập nhật trạng thái dựa trên isCompleted
        if (task.isCompleted()) {
            // Đã hoàn thành
            // Card: nền xanh nhạt, viền xanh
            holder.cardView.setCardBackgroundColor(COLOR_GREEN_LIGHT);
            holder.cardView.setStrokeColor(COLOR_GREEN_BORDER);
            
            // Checkbox: nền xanh, hiện checkmark trắng
            holder.containerCheckIcon.setBackgroundResource(R.drawable.bg_checkmark_square);
            holder.imgCheckIcon.setVisibility(View.VISIBLE);
            
            // Task icon: nền xanh, icon trắng
            holder.containerTaskIcon.setBackgroundResource(R.drawable.bg_task_icon_checked);
            holder.imgIcon.setColorFilter(COLOR_WHITE);
            
            // Text trạng thái: màu xanh
            holder.txtStatus.setText("Đã hoàn thành");
            holder.txtStatus.setTextColor(COLOR_GREEN);
        } else {
            // Chưa làm
            // Card: nền trắng, viền xám
            holder.cardView.setCardBackgroundColor(COLOR_WHITE);
            holder.cardView.setStrokeColor(COLOR_GRAY_BORDER);
            
            // Checkbox: viền xám, ẩn checkmark
            holder.containerCheckIcon.setBackgroundResource(R.drawable.bg_checkbox_unchecked);
            holder.imgCheckIcon.setVisibility(View.GONE);
            
            // Task icon: nền xám, icon xám
            holder.containerTaskIcon.setBackgroundResource(R.drawable.bg_task_icon_unchecked);
            holder.imgIcon.setColorFilter(COLOR_GRAY);
            
            // Text trạng thái: màu xám
            holder.txtStatus.setText("Chưa làm");
            holder.txtStatus.setTextColor(COLOR_GRAY);
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
