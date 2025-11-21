package com.kidsapp.ui.child.task.tabs;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.data.model.HouseworkTask;
import com.kidsapp.databinding.ItemHouseworkTaskBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho danh sách công việc nhà.
 * - Hiển thị tiêu đề, mô tả.
 * - Hiển thị trạng thái tick chọn (isSelected).
 * - Đổi style khi task đã hoàn thành (isCompleted).
 * - Callback lên Fragment khi số lượng task được chọn thay đổi.
 */
public class HouseworkAdapter extends RecyclerView.Adapter<HouseworkAdapter.HouseworkViewHolder> {

    public interface OnSelectionChangedListener {
        /**
         * Được gọi mỗi khi số lượng task được chọn thay đổi.
         *
         * @param selectedCount số lượng task hiện đang được chọn.
         */
        void onSelectionChanged(int selectedCount);
    }

    private final List<HouseworkTask> tasks = new ArrayList<>();
    private final OnSelectionChangedListener selectionChangedListener;

    public HouseworkAdapter(@NonNull List<HouseworkTask> houseworkTasks,
                            @NonNull OnSelectionChangedListener listener) {
        tasks.clear();
        tasks.addAll(houseworkTasks);
        this.selectionChangedListener = listener;
    }

    @NonNull
    @Override
    public HouseworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHouseworkTaskBinding binding = ItemHouseworkTaskBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new HouseworkViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HouseworkViewHolder holder, int position) {
        holder.bind(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    /**
     * Lấy danh sách các task đang được chọn.
     */
    @NonNull
    public List<HouseworkTask> getSelectedTasks() {
        List<HouseworkTask> selected = new ArrayList<>();
        for (HouseworkTask task : tasks) {
            if (task.isSelected()) {
                selected.add(task);
            }
        }
        return selected;
    }

    private int getSelectedCount() {
        int count = 0;
        for (HouseworkTask task : tasks) {
            if (task.isSelected()) {
                count++;
            }
        }
        return count;
    }

    class HouseworkViewHolder extends RecyclerView.ViewHolder {

        private final ItemHouseworkTaskBinding binding;

        HouseworkViewHolder(@NonNull ItemHouseworkTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(@NonNull HouseworkTask task) {
            Context context = binding.getRoot().getContext();

            binding.txtTitle.setText(task.getTitle());
            binding.txtSubtitle.setText(task.getSubtitle());

            // Style khi task đã hoàn thành: gạch ngang + màu xám nhạt
            int normalTitleColor = ContextCompat.getColor(context, R.color.text_primary);
            int normalSubtitleColor = ContextCompat.getColor(context, R.color.text_secondary);
            int completedColor = ContextCompat.getColor(context, R.color.text_tertiary);

            if (task.isCompleted()) {
                binding.txtTitle.setTextColor(completedColor);
                binding.txtSubtitle.setTextColor(completedColor);
                binding.txtTitle.setPaintFlags(binding.txtTitle.getPaintFlags()
                        | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.txtSubtitle.setPaintFlags(binding.txtSubtitle.getPaintFlags()
                        | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                binding.txtTitle.setTextColor(normalTitleColor);
                binding.txtSubtitle.setTextColor(normalSubtitleColor);
                binding.txtTitle.setPaintFlags(binding.txtTitle.getPaintFlags()
                        & (~Paint.STRIKE_THRU_TEXT_FLAG));
                binding.txtSubtitle.setPaintFlags(binding.txtSubtitle.getPaintFlags()
                        & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            // Binding trạng thái checkbox
            updateCheckboxUi(task);

            View.OnClickListener toggleListener = v -> toggleSelection(task);

            // Click vào toàn bộ item
            binding.getRoot().setOnClickListener(toggleListener);
            // Hoặc click trực tiếp vào vòng tròn
            binding.imgCheckbox.setOnClickListener(toggleListener);
        }

        private void toggleSelection(@NonNull HouseworkTask task) {
            // Không cho phép chọn lại nếu đã hoàn thành? Ở đây vẫn cho phép chọn,
            // nhưng có thể đổi sang bỏ qua nếu cần.
            task.setSelected(!task.isSelected());
            updateCheckboxUi(task);

            if (selectionChangedListener != null) {
                selectionChangedListener.onSelectionChanged(getSelectedCount());
            }
        }

        private void updateCheckboxUi(@NonNull HouseworkTask task) {
            if (task.isSelected()) {
                binding.imgCheckbox.setBackgroundResource(R.drawable.bg_checkbox_checked);
                binding.imgCheckbox.setImageResource(R.drawable.ic_check_circle);
                binding.imgCheckbox.setColorFilter(
                        ContextCompat.getColor(binding.getRoot().getContext(), R.color.white));
            } else {
                binding.imgCheckbox.setBackgroundResource(R.drawable.bg_checkbox_unchecked);
                binding.imgCheckbox.setImageDrawable(null);
            }
        }
    }
}


