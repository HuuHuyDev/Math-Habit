package com.kidsapp.ui.child.quizz;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsapp.R;
import com.kidsapp.data.model.AnswerOption;
import com.kidsapp.data.model.Question;

import java.util.List;

/**
 * Adapter hiển thị danh sách câu sai
 */
public class ExamReviewAdapter extends RecyclerView.Adapter<ExamReviewAdapter.ReviewViewHolder> {

    private final List<Question> questions;

    public ExamReviewAdapter(List<Question> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.bind(question, position + 1);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView txtQuestionNumber;
        TextView txtQuestionTitle;
        TextView txtExplanation;
        ViewGroup layoutAnswers;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            txtQuestionNumber = itemView.findViewById(R.id.txtQuestionNumber);
            txtQuestionTitle = itemView.findViewById(R.id.txtQuestionTitle);
            txtExplanation = itemView.findViewById(R.id.txtExplanation);
            layoutAnswers = itemView.findViewById(R.id.layoutAnswers);
        }

        void bind(Question question, int questionNumber) {
            txtQuestionNumber.setText("Câu " + questionNumber);
            txtQuestionTitle.setText(question.getTitle());
            
            // Hiển thị giải thích
            String explanation = question.getExplanation();
            if (explanation != null && !explanation.isEmpty()) {
                txtExplanation.setText("💡 " + explanation);
                txtExplanation.setVisibility(View.VISIBLE);
            } else {
                txtExplanation.setVisibility(View.GONE);
            }

            // Hiển thị các đáp án
            layoutAnswers.removeAllViews();
            List<AnswerOption> options = question.getOptions();
            int selectedIndex = question.getSelectedIndex();
            int correctIndex = question.getCorrectIndex();
            
            for (int i = 0; i < options.size(); i++) {
                AnswerOption option = options.get(i);
                boolean isCorrect = (i == correctIndex);
                boolean isSelected = (i == selectedIndex);
                TextView answerView = createAnswerView(option, isCorrect, isSelected);
                layoutAnswers.addView(answerView);
            }
        }

        private TextView createAnswerView(AnswerOption option, boolean isCorrect, boolean isSelected) {
            TextView textView = new TextView(itemView.getContext());
            textView.setTextSize(15);
            textView.setPadding(32, 24, 32, 24);
            
            android.view.ViewGroup.MarginLayoutParams params = 
                new android.view.ViewGroup.MarginLayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                );
            params.bottomMargin = 12;
            textView.setLayoutParams(params);

            if (isCorrect) {
                // Đáp án đúng - màu xanh lá
                textView.setBackgroundColor(0xFFE8F5E9);
                textView.setTextColor(0xFF2E7D32);
                textView.setText("✓ " + option.getLabel() + ". " + option.getContent() + " (Đáp án đúng)");
            } else if (isSelected) {
                // Đáp án sai mà người dùng đã chọn - màu đỏ
                textView.setBackgroundColor(0xFFFFEBEE);
                textView.setTextColor(0xFFC62828);
                textView.setText("✗ " + option.getLabel() + ". " + option.getContent() + " (Bạn đã chọn)");
            } else {
                // Đáp án khác - màu xám nhạt
                textView.setBackgroundColor(0xFFF5F5F5);
                textView.setTextColor(0xFF666666);
                textView.setText(option.getLabel() + ". " + option.getContent());
            }

            return textView;
        }
    }
}
