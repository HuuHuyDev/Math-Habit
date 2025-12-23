package com.kidsapp.ui.child.review;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.data.model.AnswerOption;
import com.kidsapp.data.model.Question;
import com.kidsapp.databinding.ItemReviewQuestionBinding;

import java.util.List;
import java.util.Map;

/**
 * Adapter để hiển thị danh sách câu hỏi và đáp án trong màn hình review
 */
public class ReviewAnswersAdapter extends RecyclerView.Adapter<ReviewAnswersAdapter.ReviewViewHolder> {

    private final List<Question> questions;
    private final Map<String, Integer> selectedAnswers;

    public ReviewAnswersAdapter(List<Question> questions, Map<String, Integer> selectedAnswers) {
        this.questions = questions;
        this.selectedAnswers = selectedAnswers;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReviewQuestionBinding binding = ItemReviewQuestionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ReviewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.bind(question, position + 1, selectedAnswers);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final ItemReviewQuestionBinding binding;

        public ReviewViewHolder(@NonNull ItemReviewQuestionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Question question, int questionNumber, Map<String, Integer> selectedAnswers) {
            // Hiển thị số câu hỏi và tiêu đề
            binding.txtQuestionNumber.setText(String.format("Câu %d", questionNumber));
            binding.txtQuestionTitle.setText(question.getTitle());

            // Lấy đáp án đã chọn
            Integer selectedIndex = selectedAnswers != null ? selectedAnswers.get(question.getId()) : null;
            int correctIndex = question.getCorrectIndex();

            // Kiểm tra đúng/sai
            boolean isCorrect = selectedIndex != null && selectedIndex == correctIndex;

            // Hiển thị trạng thái
            if (selectedIndex == null) {
                // Không trả lời
                binding.layoutStatus.setVisibility(View.VISIBLE);
                binding.imgStatus.setImageResource(R.drawable.ic_info);
                binding.imgStatus.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.text_tertiary));
                binding.txtStatus.setText("Chưa trả lời");
                binding.txtStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_tertiary));
            } else if (isCorrect) {
                // Đúng
                binding.layoutStatus.setVisibility(View.VISIBLE);
                binding.imgStatus.setImageResource(R.drawable.ic_checkmark_completed);
                binding.imgStatus.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.status_success));
                binding.txtStatus.setText("Đúng");
                binding.txtStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_success));
            } else {
                // Sai
                binding.layoutStatus.setVisibility(View.VISIBLE);
                binding.imgStatus.setImageResource(R.drawable.ic_close);
                binding.imgStatus.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.status_error));
                binding.txtStatus.setText("Sai");
                binding.txtStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_error));
            }

            // Hiển thị các đáp án
            binding.layoutAnswers.removeAllViews();
            List<AnswerOption> options = question.getOptions();
            
            for (int i = 0; i < options.size(); i++) {
                View answerView = LayoutInflater.from(itemView.getContext())
                        .inflate(R.layout.item_review_answer, binding.layoutAnswers, false);
                
                bindAnswerOption(answerView, options.get(i), i, selectedIndex, correctIndex);
                binding.layoutAnswers.addView(answerView);
            }

            // Hiển thị giải thích nếu có
            if (question.getExplanation() != null && !question.getExplanation().isEmpty()) {
                binding.layoutExplanation.setVisibility(View.VISIBLE);
                binding.txtExplanation.setText(question.getExplanation());
            } else {
                binding.layoutExplanation.setVisibility(View.GONE);
            }
        }

        private void bindAnswerOption(View answerView, AnswerOption option, int index, 
                                      Integer selectedIndex, int correctIndex) {
            android.widget.TextView txtLabel = answerView.findViewById(R.id.txtAnswerLabel);
            android.widget.TextView txtContent = answerView.findViewById(R.id.txtAnswerContent);
            android.widget.ImageView imgIndicator = answerView.findViewById(R.id.imgAnswerIndicator);

            txtLabel.setText(option.getLabel());
            txtContent.setText(option.getContent());

            // Xác định trạng thái của đáp án
            boolean isSelected = selectedIndex != null && selectedIndex == index;
            boolean isCorrect = index == correctIndex;

            if (isCorrect) {
                // Đáp án đúng - màu xanh
                answerView.setBackgroundResource(R.drawable.bg_answer_correct);
                txtLabel.setTextColor(ContextCompat.getColor(answerView.getContext(), R.color.status_success));
                txtContent.setTextColor(ContextCompat.getColor(answerView.getContext(), R.color.status_success));
                imgIndicator.setVisibility(View.VISIBLE);
                imgIndicator.setImageResource(R.drawable.ic_checkmark_completed);
                imgIndicator.setColorFilter(ContextCompat.getColor(answerView.getContext(), R.color.status_success));
            } else if (isSelected) {
                // Đáp án sai đã chọn - màu đỏ
                answerView.setBackgroundResource(R.drawable.bg_answer_wrong);
                txtLabel.setTextColor(ContextCompat.getColor(answerView.getContext(), R.color.status_error));
                txtContent.setTextColor(ContextCompat.getColor(answerView.getContext(), R.color.status_error));
                imgIndicator.setVisibility(View.VISIBLE);
                imgIndicator.setImageResource(R.drawable.ic_close);
                imgIndicator.setColorFilter(ContextCompat.getColor(answerView.getContext(), R.color.status_error));
            } else {
                // Đáp án không chọn - màu xám
                answerView.setBackgroundResource(R.drawable.bg_answer_default);
                txtLabel.setTextColor(ContextCompat.getColor(answerView.getContext(), R.color.text_secondary));
                txtContent.setTextColor(ContextCompat.getColor(answerView.getContext(), R.color.text_primary));
                imgIndicator.setVisibility(View.GONE);
            }
        }
    }
}
