package com.kidsapp.data.response;

import java.util.List;

public class QuestionResponse {
    private String id;
    private String questionText;
    private List<String> options;
    private Integer correctAnswer; // Không được trả về từ backend (security)
    private String explanation; // Không được trả về từ backend (security)
    private Integer timeLimit; // seconds
    private String categoryName;
    private Integer difficultyLevel;
    
    // Constructors
    public QuestionResponse() {}
    
    public QuestionResponse(String id, String questionText, List<String> options, 
                          Integer correctAnswer, String explanation, Integer timeLimit, 
                          String categoryName, Integer difficultyLevel) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.timeLimit = timeLimit;
        this.categoryName = categoryName;
        this.difficultyLevel = difficultyLevel;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public List<String> getOptions() {
        return options;
    }
    
    public void setOptions(List<String> options) {
        this.options = options;
    }
    
    public Integer getCorrectAnswer() {
        return correctAnswer;
    }
    
    public void setCorrectAnswer(Integer correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    
    public String getExplanation() {
        return explanation;
    }
    
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
    
    public Integer getTimeLimit() {
        return timeLimit;
    }
    
    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public Integer getDifficultyLevel() {
        return difficultyLevel;
    }
    
    public void setDifficultyLevel(Integer difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
}