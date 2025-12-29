package com.kidsapp.data.request;

public class SubmitAnswerRequest {
    private String childId;
    private String questionId;
    private int selectedAnswer; // 1-based index
    private long timeSpent; // milliseconds
    
    public SubmitAnswerRequest() {}
    
    public SubmitAnswerRequest(String childId, String questionId, int selectedAnswer, long timeSpent) {
        this.childId = childId;
        this.questionId = questionId;
        this.selectedAnswer = selectedAnswer;
        this.timeSpent = timeSpent;
    }
    
    public String getChildId() {
        return childId;
    }
    
    public void setChildId(String childId) {
        this.childId = childId;
    }
    
    public String getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
    
    public int getSelectedAnswer() {
        return selectedAnswer;
    }
    
    public void setSelectedAnswer(int selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }
    
    public long getTimeSpent() {
        return timeSpent;
    }
    
    public void setTimeSpent(long timeSpent) {
        this.timeSpent = timeSpent;
    }
}