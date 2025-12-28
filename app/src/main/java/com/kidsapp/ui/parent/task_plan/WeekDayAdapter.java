package com.kidsapp.ui.parent.task_plan;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.databinding.ItemWeekDayBinding;
import com.kidsapp.ui.parent.task_plan.model.WeekDay;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho danh sách ngày trong tuần
 * Hiển thị khác nhau cho: ngày đã qua (mờ), hôm nay (highlight), ngày tương lai
 */
public class WeekDayAdapter extends RecyclerView.Adapter<WeekDayAdapter.WeekDayViewHolder> {

    private List<WeekDay> weekDays = new ArrayList<>();
    private int selectedPosition = 0;
    private OnDayClickListener listener;

    public interface OnDayClickListener {
        void onDayClick(WeekDay weekDay, int position);
    }

    public void setOnDayClickListener(OnDayClickListener listener) {
        this.listener = listener;
    }

    public void setWeekDays(List<WeekDay> weekDays) {
        this.weekDays = weekDays;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        int oldPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(oldPosition);
        notifyItemChanged(selectedPosition);
    }

    @NonNull
    @Override
    public WeekDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWeekDayBinding binding = ItemWeekDayBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new WeekDayViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WeekDayViewHolder holder, int position) {
        holder.bind(weekDays.get(position), position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return weekDays.size();
    }

    class WeekDayViewHolder extends RecyclerView.ViewHolder {
        private final ItemWeekDayBinding binding;

        public WeekDayViewHolder(ItemWeekDayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.cardWeekDay.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    setSelectedPosition(position);
                    listener.onDayClick(weekDays.get(position), position);
                }
            });
        }

        public void bind(WeekDay weekDay, boolean isSelected) {
            binding.txtDayLabel.setText(weekDay.getDayLabel());
            binding.txtProgress.setText(weekDay.getProgress() + "%");
            binding.txtTaskCount.setText(weekDay.getTaskCountText());

            android.content.Context context = binding.getRoot().getContext();

            if (isSelected) {
                // Ngày được chọn - gradient background
                binding.cardWeekDay.setBackground(context.getDrawable(R.drawable.bg_week_day_selected));
                binding.txtDayLabel.setTextColor(context.getColor(android.R.color.white));
                binding.txtProgress.setTextColor(context.getColor(android.R.color.white));
                binding.txtTaskCount.setTextColor(context.getColor(android.R.color.white));
                binding.cardWeekDay.setAlpha(1.0f);
            } else if (weekDay.isToday()) {
                // Ngày hôm nay - viền xanh đặc biệt
                binding.cardWeekDay.setBackgroundResource(R.drawable.bg_week_day_today);
                binding.txtDayLabel.setTextColor(context.getColor(R.color.primary));
                binding.txtProgress.setTextColor(context.getColor(R.color.primary));
                binding.txtTaskCount.setTextColor(context.getColor(R.color.primary));
                binding.cardWeekDay.setAlpha(1.0f);
            } else if (weekDay.isPast()) {
                // Ngày đã qua - mờ
                binding.cardWeekDay.setBackgroundResource(R.drawable.bg_card_white_pure);
                binding.txtDayLabel.setTextColor(context.getColor(R.color.text_secondary));
                binding.txtProgress.setTextColor(context.getColor(R.color.text_secondary));
                binding.txtTaskCount.setTextColor(context.getColor(R.color.text_hint));
                binding.cardWeekDay.setAlpha(0.6f);
            } else {
                // Ngày tương lai - bình thường
                binding.cardWeekDay.setBackgroundResource(R.drawable.bg_card_white_pure);
                binding.txtDayLabel.setTextColor(context.getColor(R.color.text_primary));
                binding.txtProgress.setTextColor(context.getColor(R.color.text_primary));
                binding.txtTaskCount.setTextColor(context.getColor(R.color.text_secondary));
                binding.cardWeekDay.setAlpha(1.0f);
            }
        }
    }
}
