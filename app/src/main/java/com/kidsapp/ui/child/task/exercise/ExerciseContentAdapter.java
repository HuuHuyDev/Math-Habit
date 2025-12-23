package com.kidsapp.ui.child.task.exercise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.databinding.ItemExerciseContentBinding;

import java.util.List;

/**
 * Adapter cho danh sách nội dung bài tập
 */
public class ExerciseContentAdapter extends RecyclerView.Adapter<ExerciseContentAdapter.ContentViewHolder> {

    private final List<ExerciseContent> contents;
    private final OnContentClickListener listener;

    public interface OnContentClickListener {
        void onContentClick(ExerciseContent content);
    }

    public ExerciseContentAdapter(List<ExerciseContent> contents, OnContentClickListener listener) {
        this.contents = contents;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExerciseContentBinding binding = ItemExerciseContentBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ContentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        ExerciseContent content = contents.get(position);
        holder.bind(content, listener);
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {
        private final ItemExerciseContentBinding binding;

        public ContentViewHolder(@NonNull ItemExerciseContentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ExerciseContent content, OnContentClickListener listener) {
            binding.txtTitle.setText(content.getTitle());
            binding.txtDescription.setText(content.getDescription());
            binding.txtQuestions.setText(content.getTotalQuestions() + " câu hỏi");
            binding.txtDuration.setText(content.getDuration() + " phút");
            binding.imgIcon.setImageResource(content.getIconRes());

            // Hiển thị tiến độ
            if (content.getCompletedQuestions() > 0) {
                binding.layoutProgress.setVisibility(View.VISIBLE);
                binding.progressBar.setMax(content.getTotalQuestions());
                binding.progressBar.setProgress(content.getCompletedQuestions());
                binding.txtProgress.setText(String.format("%d/%d hoàn thành", 
                    content.getCompletedQuestions(), content.getTotalQuestions()));
            } else {
                binding.layoutProgress.setVisibility(View.GONE);
            }

            // Hiển thị trạng thái khóa/mở
            if (!content.isUnlocked()) {
                binding.cardContent.setAlpha(0.6f);
                binding.imgLock.setVisibility(View.VISIBLE);
                binding.txtStatus.setVisibility(View.VISIBLE);
                binding.txtStatus.setText("🔒 Chưa mở khóa");
                binding.txtStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_tertiary));
            } else if (content.isCompleted()) {
                binding.cardContent.setAlpha(1.0f);
                binding.imgLock.setVisibility(View.GONE);
                binding.txtStatus.setVisibility(View.VISIBLE);
                binding.txtStatus.setText("✅ Đã hoàn thành");
                binding.txtStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_success));
            } else {
                binding.cardContent.setAlpha(1.0f);
                binding.imgLock.setVisibility(View.GONE);
                binding.txtStatus.setVisibility(View.GONE);
            }

            // Click listener
            binding.cardContent.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onContentClick(content);
                }
            });
        }
    }
}
