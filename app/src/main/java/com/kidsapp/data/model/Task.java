package com.kidsapp.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model class for Task - Updated to match backend API
 */
public class Task {
    @SerializedName("id")
    private String id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("childId")
    private String childId;
    
    @SerializedName("assignedBy")
    private String assignedBy; // parent ID
    
    @SerializedName("taskType")
    private String taskType; // housework, habit, exercise, custom
    
    @SerializedName("exerciseId")
    private Integer exerciseId;
    
    @SerializedName("dueDate")
    private String dueDate;
    
    @SerializedName("dueTime")
    private String dueTime;
    
    @SerializedName("status")
    private String status; // pending, in_progress, completed, verified
    
    @SerializedName("pointsReward")
    private int pointsReward;
    
    @SerializedName("priority")
    private int priority; // 1=low, 2=medium, 3=high
    
    @SerializedName("isRecurring")
    private boolean isRecurring;
    
    @SerializedName("recurrencePattern")
    private String recurrencePattern; // daily, weekly, monthly
    
    @SerializedName("activeProof")
    private TaskProof activeProof; // Minh chứng hiện tại
    
    @SerializedName("completedAt")
    private String completedAt;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("updatedAt")
    private String updatedAt;
    
    /**
     * Inner class for TaskProof
     */
    public static class TaskProof {
        @SerializedName("id")
        private String id;
        
        @SerializedName("taskId")
        private String taskId;
        
        @SerializedName("proofUrl")
        private String proofUrl;
        
        @SerializedName("proofType")
        private String proofType; // IMAGE, VIDEO
        
        @SerializedName("note")
        private String note;
        
        @SerializedName("submittedAt")
        private String submittedAt;
        
        @SerializedName("status")
        private String status; // pending, approved, rejected
        
        @SerializedName("reviewedBy")
        private String reviewedBy;
        
        @SerializedName("reviewedAt")
        private String reviewedAt;
        
        @SerializedName("rejectionReason")
        private String rejectionReason;
        
        @SerializedName("isActive")
        private Boolean isActive;
        
        // Getters
        public String getId() { return id; }
        public String getTaskId() { return taskId; }
        public String getProofUrl() { return proofUrl; }
        public String getProofType() { return proofType; }
        public String getNote() { return note; }
        public String getSubmittedAt() { return submittedAt; }
        public String getStatus() { return status; }
        public String getReviewedBy() { return reviewedBy; }
        public String getReviewedAt() { return reviewedAt; }
        public String getRejectionReason() { return rejectionReason; }
        public Boolean getIsActive() { return isActive; }
        
        // Setters
        public void setId(String id) { this.id = id; }
        public void setTaskId(String taskId) { this.taskId = taskId; }
        public void setProofUrl(String proofUrl) { this.proofUrl = proofUrl; }
        public void setProofType(String proofType) { this.proofType = proofType; }
        public void setNote(String note) { this.note = note; }
        public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
        public void setStatus(String status) { this.status = status; }
        public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
        public void setReviewedAt(String reviewedAt) { this.reviewedAt = reviewedAt; }
        public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }

    public Task() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Integer getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Integer exerciseId) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPointsReward() {
        return pointsReward;
    }

    public void setPointsReward(int pointsReward) {
        this.pointsReward = pointsReward;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public String getRecurrencePattern() {
        return recurrencePattern;
    }

    public void setRecurrencePattern(String recurrencePattern) {
        this.recurrencePattern = recurrencePattern;
    }

    public TaskProof getActiveProof() {
        return activeProof;
    }

    public void setActiveProof(TaskProof activeProof) {
        this.activeProof = activeProof;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

