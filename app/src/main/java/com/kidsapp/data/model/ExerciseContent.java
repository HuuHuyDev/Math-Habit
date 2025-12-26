package com.kidsapp.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model cho bài tập (Exercise Content)
 * Map từ ExerciseContentResponse của backend
 */
public class ExerciseContent {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("gradeLevel")
    private Integer gradeLevel;
    
    @SerializedName("subject")
    private String subject;
    
    @SerializedName("topic")
    private String topic;
    
    @SerializedName("difficultyLevel")
    private Integer difficultyLevel;
    
    @SerializedName("estimatedTimeMinutes")
    private Integer estimatedTimeMinutes;
    
    @SerializedName("pointsReward")
    private Integer pointsReward;
    
    @SerializedName("totalQuestions")
    private Integer totalQuestions;
    
    @SerializedName("isActive")
    private Boolean isActive;
    
    // Progress info
    @SerializedName("status")
    private String status; // not_started, in_progress, completed
    
    @SerializedName("completedQuestions")
    private Integer completedQuestions;
    
    @SerializedName("bestScore")
    private Integer bestScore;
    
    @SerializedName("progressPercent")
    private Integer progressPercent;
    
    @SerializedName("isUnlocked")
    private Boolean isUnlocked;

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Integer getGradeLevel() { return gradeLevel; }
    public String getSubject() { return subject; }
    public String getTopic() { return topic; }
    public Integer getDifficultyLevel() { return difficultyLevel; }
    public Integer getEstimatedTimeMinutes() { return estimatedTimeMinutes; }
    public Integer getPointsReward() { return pointsReward; }
    public Integer getTotalQuestions() { return totalQuestions; }
    public Boolean getIsActive() { return isActive; }
    public String getStatus() { return status; }
    public Integer getCompletedQuestions() { return completedQuestions != null ? completedQuestions : 0; }
    public Integer getBestScore() { return bestScore; }
    public Integer getProgressPercent() { return progressPercent != null ? progressPercent : 0; }
    public Boolean getIsUnlocked() { return isUnlocked != null ? isUnlocked : true; }
    
    // Helper methods
    public boolean isCompleted() {
        return "completed".equals(status);
    }
    
    public boolean isInProgress() {
        return "in_progress".equals(status);
    }
}
