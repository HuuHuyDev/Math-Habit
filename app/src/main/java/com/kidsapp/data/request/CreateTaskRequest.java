package com.kidsapp.data.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request để parent tạo/giao bài cho con
 */
public class CreateTaskRequest {
    
    @SerializedName("childId")
    private String childId;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("taskType")
    private String taskType; // exercise, housework, habit, custom
    
    @SerializedName("exerciseId")
    private String exerciseId;
    
    @SerializedName("dueDate")
    private String dueDate; // yyyy-MM-dd
    
    @SerializedName("dueTime")
    private String dueTime; // HH:mm:ss
    
    @SerializedName("pointsReward")
    private Integer pointsReward;
    
    @SerializedName("priority")
    private Integer priority;
    
    @SerializedName("isRecurring")
    private Boolean isRecurring;
    
    @SerializedName("recurrencePattern")
    private String recurrencePattern;

    public CreateTaskRequest() {
    }

    public CreateTaskRequest(String childId, String title, String description, String taskType) {
        this.childId = childId;
        this.title = title;
        this.description = description;
        this.taskType = taskType;
    }

    // Getters and Setters
    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public Integer getPointsReward() {
        return pointsReward;
    }

    public void setPointsReward(Integer pointsReward) {
        this.pointsReward = pointsReward;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getIsRecurring() {
        return isRecurring;
    }

    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    public String getRecurrencePattern() {
        return recurrencePattern;
    }

    public void setRecurrencePattern(String recurrencePattern) {
        this.recurrencePattern = recurrencePattern;
    }
}
