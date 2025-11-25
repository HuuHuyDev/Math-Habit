package com.kidsapp.ui.parent.child.detail.components;

import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;

import java.util.List;

/**
 * Adapter cho RecyclerView hiển thị biểu đồ tiến độ 7 ngày
 * Có animation thanh bar từ dưới lên với hiệu ứng "sóng"
 */
public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ProgressViewHolder> {

    private List<DayProgress> progressList;
    private static final int MAX_BAR_HEIGHT = 100; // Chiều cao tối đa của thanh bar (dp, sẽ convert sang px)
    private static final int ANIMATION_DURATION = 900; // Thời gian animation (ms)
    private static final int ANIMATION_DELAY_STEP = 80; // Delay giữa các item (ms)

    public ProgressAdapter(List<DayProgress> progressList) {
        this.progressList = progressList;
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_progress_day, parent, false);
        return new ProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
        DayProgress dayProgress = progressList.get(position);
        
        // Set text phần trăm và ngày
        holder.txtPercent.setText(dayProgress.percent + "%");
        holder.txtDay.setText(dayProgress.day);

        // Tính chiều cao target của thanh bar (dựa trên phần trăm)
        int maxHeightPx = (int) (MAX_BAR_HEIGHT * holder.itemView.getContext()
                .getResources().getDisplayMetrics().density);
        int targetHeight = (int) (maxHeightPx * dayProgress.percent / 100f);
        
        // Reset chiều cao thanh xanh về 0 trước khi animate
        ViewGroup.LayoutParams params = holder.viewProgressBar.getLayoutParams();
        params.height = 0;
        holder.viewProgressBar.setLayoutParams(params);

        // Tạo animation cho thanh bar
        ValueAnimator animator = ValueAnimator.ofInt(0, targetHeight);
        animator.setDuration(ANIMATION_DURATION);
        animator.setStartDelay(position * ANIMATION_DELAY_STEP); // Delay tăng dần theo position
        animator.setInterpolator(new DecelerateInterpolator());
        
        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            
            // Cập nhật chiều cao thanh bar xanh (phần đã hoàn thành)
            ViewGroup.LayoutParams layoutParams = holder.viewProgressBar.getLayoutParams();
            layoutParams.height = animatedValue;
            holder.viewProgressBar.setLayoutParams(layoutParams);
        });

        animator.start();
    }

    @Override
    public int getItemCount() {
        return progressList != null ? progressList.size() : 0;
    }

    /**
     * ViewHolder cho mỗi item trong RecyclerView
     */
    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        TextView txtPercent;
        TextView txtDay;
        View viewProgressBar;
        ViewGroup containerBar;

        ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPercent = itemView.findViewById(R.id.txtPercent);
            txtDay = itemView.findViewById(R.id.txtDay);
            viewProgressBar = itemView.findViewById(R.id.viewProgressBar);
            containerBar = itemView.findViewById(R.id.containerBar);
        }
    }
}
