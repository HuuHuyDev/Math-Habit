package com.kidsapp.data.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Response cho chi tiết lịch sử làm bài
 * Dùng trong màn hình History Detail của Child
 */
public class TaskHistoryDetailResponse {
    
    @SerializedName("taskId")
    private String taskId;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("taskType")
    private String taskType;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("completedAt")
    private String completedAt;
    
    @SerializedName("durationSeconds")
    private long durationSeconds;
    
    @SerializedName("totalQuestions")
    private int totalQuestions;
    
    @SerializedName("correctAnswers")
    private int correctAnswers;
    
    @SerializedName("wrongAnswers")
    private int wrongAnswers;
    
    @SerializedName("score")
    private int score;
    
    @SerializedName("coinsEarned")
    private int coinsEarned;
    
    @SerializedName("xpEarned")
    private int xpEarned;
    
    @SerializedName("answers")
    private List<AnswerDetail> answers;
    
    // Getters and Setters
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
    
    public long getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(long durationSeconds) { this.durationSeconds = durationSeconds; }
    
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    
    public int getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }
    
    public int getWrongAnswers() { return wrongAnswers; }
    public void setWrongAnswers(int wrongAnswers) { this.wrongAnswers = wrongAnswers; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    
    public int getCoinsEarned() { return coinsEarned; }
    public void setCoinsEarned(int coinsEarned) { this.coinsEarned = coinsEarned; }
    
    public int getXpEarned() { return xpEarned; }
    public void setXpEarned(int xpEarned) { this.xpEarned = xpEarned; }
    
    public List<AnswerDetail> getAnswers() { return answers; }
    public void setAnswers(List<AnswerDetail> answers) { this.answers = answers; }
    
    public static class AnswerDetail {
        @SerializedName("questionId")
        private String questionId;
        
        @SerializedName("questionText")
        private String questionText;
        
        @SerializedName("selectedAnswer")
        private String selectedAnswer;
        
        @SerializedName("correctAnswer")
        private String correctAnswer;
        
        @SerializedName("isCorrect")
        private boolean isCorrect;
        
        @SerializedName("timeSpentSeconds")
        private int timeSpentSeconds;
        
        // Getters and Setters
        public String getQuestionId() { return questionId; }
        public void setQuestionId(String questionId) { this.questionId = questionId; }
        
        public String getQuestionText() { return questionText; }
        public void setQuestionText(String questionText) { this.questionText = questionText; }
        
        public String getSelectedAnswer() { return selectedAnswer; }
        public void setSelectedAnswer(String selectedAnswer) { this.selectedAnswer = selectedAnswer; }
        
        public String getCorrectAnswer() { return correctAnswer; }
        public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
        
        public boolean isCorrect() { return isCorrect; }
        public void setCorrect(boolean correct) { isCorrect = correct; }
        
        public int getTimeSpentSeconds() { return timeSpentSeconds; }
        public void setTimeSpentSeconds(int timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }
    }
}
