package com.kidsapp.data.response;

import com.google.gson.annotations.SerializedName;
import com.kidsapp.data.model.Task;

/**
 * Response wrapper cho Task từ API
 * Đồng bộ với BE TaskResponse
 */
public class TaskResponse {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("childId")
    private String childId;
    
    @SerializedName("assignedBy")
    private String assignedBy;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("taskType")
    private String taskType;
    
    @SerializedName("exerciseId")
    private String exerciseId;
    
    @SerializedName("habitTemplateId")
    private String habitTemplateId;
    
    @SerializedName("dueDate")
    private String dueDate;
    
    @SerializedName("dueTime")
    private String dueTime;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("pointsReward")
    private Integer pointsReward;
    
    @SerializedName("coinsReward")
    private Integer coinsReward;
    
    @SerializedName("isRecurring")
    private Boolean isRecurring;
    
    @SerializedName("recurrencePattern")
    private String recurrencePattern;
    
    @SerializedName("priority")
    private Integer priority;
    
    @SerializedName("isMandatory")
    private Boolean isMandatory;
    
    @SerializedName("parentNote")
    private String parentNote;
    
    @SerializedName("submittedAt")
    private String submittedAt;
    
    @SerializedName("completedAt")
    private String completedAt;
    
    @SerializedName("xpEarned")
    private Integer xpEarned;
    
    @SerializedName("coinsEarned")
    private Integer coinsEarned;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("updatedAt")
    private String updatedAt;
    
    @SerializedName("activeProof")
    private Task.TaskProof activeProof;

    // Getters
    public String getId() { return id; }
    public String getChildId() { return childId; }
    public String getAssignedBy() { return assignedBy; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getTaskType() { return taskType; }
    public String getExerciseId() { return exerciseId; }
    public String getHabitTemplateId() { return habitTemplateId; }
    public String getDueDate() { return dueDate; }
    public String getDueTime() { return dueTime; }
    public String getStatus() { return status; }
    public Integer getPointsReward() { return pointsReward; }
    public Integer getCoinsReward() { return coinsReward; }
    public Boolean getIsRecurring() { return isRecurring; }
    public String getRecurrencePattern() { return recurrencePattern; }
    public Integer getPriority() { return priority; }
    public Boolean getIsMandatory() { return isMandatory; }
    public String getParentNote() { return parentNote; }
    public String getSubmittedAt() { return submittedAt; }
    public String getCompletedAt() { return completedAt; }
    public Integer getXpEarned() { return xpEarned; }
    public Integer getCoinsEarned() { return coinsEarned; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public Task.TaskProof getActiveProof() { return activeProof; }
}
