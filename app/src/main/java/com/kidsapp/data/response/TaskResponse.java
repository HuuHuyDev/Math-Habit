package com.kidsapp.data.response;

import com.google.gson.annotations.SerializedName;
import com.kidsapp.data.model.Task;

/**
 * Response wrapper cho Task từ API
 */
public class TaskResponse {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("childId")
    private String childId;
    
    @SerializedName("assignedBy")
    private String assignedBy;
    
    @SerializedName("taskType")
    private String taskType;
    
    @SerializedName("exerciseId")
    private Integer exerciseId;
    
    @SerializedName("dueDate")
    private String dueDate;
    
    @SerializedName("dueTime")
    private String dueTime;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("pointsReward")
    private Integer pointsReward;
    
    @SerializedName("priority")
    private Integer priority;
    
    @SerializedName("isRecurring")
    private Boolean isRecurring;
    
    @SerializedName("recurrencePattern")
    private String recurrencePattern;
    
    @SerializedName("activeProof")
    private Task.TaskProof activeProof;
    
    @SerializedName("completedAt")
    private String completedAt;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("updatedAt")
    private String updatedAt;

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getChildId() { return childId; }
    public String getAssignedBy() { return assignedBy; }
    public String getTaskType() { return taskType; }
    public Integer getExerciseId() { return exerciseId; }
    public String getDueDate() { return dueDate; }
    public String getDueTime() { return dueTime; }
    public String getStatus() { return status; }
    public Integer getPointsReward() { return pointsReward; }
    public Integer getPriority() { return priority; }
    public Boolean getIsRecurring() { return isRecurring; }
    public String getRecurrencePattern() { return recurrencePattern; }
    public Task.TaskProof getActiveProof() { return activeProof; }
    public String getCompletedAt() { return completedAt; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}
