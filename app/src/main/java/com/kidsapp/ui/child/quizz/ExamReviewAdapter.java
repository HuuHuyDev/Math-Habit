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
            txtExplanation.setText("💡 " + question.getExplanation());

            // Hiển thị các đáp án
            layoutAnswers.removeAllViews();
            List<AnswerOption> options = question.getOptions();
            for (int i = 0; i < options.size(); i++) {
                AnswerOption option = options.get(i);
                TextView answerView = createAnswerView(option, i == question.getCorrectIndex());
                layoutAnswers.addView(answerView);
            }
        }

        private TextView createAnswerView(AnswerOption option, boolean isCorrect) {
            TextView textView = new TextView(itemView.getContext());
            textView.setText(option.getLabel() + ". " + option.getContent());
            textView.setTextSize(15);
            textView.setPadding(16, 12, 16, 12);
            
            android.view.ViewGroup.MarginLayoutParams params = 
                new android.view.ViewGroup.MarginLayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                );
            params.bottomMargin = 8;
            textView.setLayoutParams(params);

            if (isCorrect) {
                // Đáp án đúng - màu xanh lá
                textView.setBackgroundColor(0xFF4CAF50);
                textView.setTextColor(Color.WHITE);
                textView.setText("✓ " + option.getLabel() + ". " + option.getContent());
            } else {
                // Đáp án khác - màu xám nhạt
                textView.setBackgroundColor(0xFFF5F5F5);
                textView.setTextColor(0xFF666666);
            }

            return textView;
        }
    }
}
