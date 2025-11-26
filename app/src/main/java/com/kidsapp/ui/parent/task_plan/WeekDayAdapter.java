package com.kidsapp.ui.parent.task_plan;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.databinding.ItemWeekDayBinding;
import com.kidsapp.ui.parent.task_plan.model.WeekDay;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho danh sách ngày trong tuần
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

            // Thay đổi màu khi được chọn
            if (isSelected) {
                // Set gradient background cho card
                binding.cardWeekDay.setBackground(
                        binding.getRoot().getContext().getDrawable(com.kidsapp.R.drawable.bg_week_day_selected));
                
                // Text màu trắng
                binding.txtDayLabel.setTextColor(
                        binding.getRoot().getContext().getColor(android.R.color.white));
                binding.txtProgress.setTextColor(
                        binding.getRoot().getContext().getColor(android.R.color.white));
                binding.txtTaskCount.setTextColor(
                        binding.getRoot().getContext().getColor(android.R.color.white));
            } else {
                // Set background trắng thuần
                binding.cardWeekDay.setBackgroundResource(com.kidsapp.R.drawable.bg_card_white_pure);
                
                // Text màu mặc định
                binding.txtDayLabel.setTextColor(
                        binding.getRoot().getContext().getColor(com.kidsapp.R.color.text_primary));
                binding.txtProgress.setTextColor(
                        binding.getRoot().getContext().getColor(com.kidsapp.R.color.text_primary));
                binding.txtTaskCount.setTextColor(
                        binding.getRoot().getContext().getColor(com.kidsapp.R.color.text_secondary));
            }
        }
    }
}
