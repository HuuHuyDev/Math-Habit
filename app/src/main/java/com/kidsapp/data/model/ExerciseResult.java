package com.kidsapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Model cho kết quả bài làm
 */
public class ExerciseResult {
    
    @SerializedName("exerciseId")
    private String exerciseId;
    
    @SerializedName("totalQuestions")
    private Integer totalQuestions;
    
    @SerializedName("correctAnswers")
    private Integer correctAnswers;
    
    @SerializedName("score")
    private Integer score;
    
    @SerializedName("pointsEarned")
    private Integer pointsEarned;
    
    @SerializedName("timeSpentSeconds")
    private Integer timeSpentSeconds;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("questionResults")
    private List<QuestionResult> questionResults;

    // Getters
    public String getExerciseId() { return exerciseId; }
    public Integer getTotalQuestions() { return totalQuestions; }
    public Integer getCorrectAnswers() { return correctAnswers; }
    public Integer getScore() { return score; }
    public Integer getPointsEarned() { return pointsEarned; }
    public Integer getTimeSpentSeconds() { return timeSpentSeconds; }
    public String getStatus() { return status; }
    public List<QuestionResult> getQuestionResults() { return questionResults; }
    
    public static class QuestionResult {
        @SerializedName("questionId")
        private String questionId;
        
        @SerializedName("questionText")
        private String questionText;
        
        @SerializedName("selectedOptionId")
        private String selectedOptionId;
        
        @SerializedName("correctOptionId")
        private String correctOptionId;
        
        @SerializedName("isCorrect")
        private Boolean isCorrect;
        
        @SerializedName("explanation")
        private String explanation;

        // Getters
        public String getQuestionId() { return questionId; }
        public String getQuestionText() { return questionText; }
        public String getSelectedOptionId() { return selectedOptionId; }
        public String getCorrectOptionId() { return correctOptionId; }
        public Boolean getIsCorrect() { return isCorrect; }
        public String getExplanation() { return explanation; }
    }
}
