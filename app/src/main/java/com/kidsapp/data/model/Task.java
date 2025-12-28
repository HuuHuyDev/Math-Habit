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
    private String taskType; // EXERCISE hoặc HABIT
    
    @SerializedName("exerciseId")
    private String exerciseId; // ID của ExerciseContent
    
    @SerializedName("habitTemplateId")
    private String habitTemplateId; // ID của HabitTemplate
    
    @SerializedName("dueDate")
    private String dueDate;
    
    @SerializedName("dueTime")
    private String dueTime;
    
    @SerializedName("status")
    private String status; // PENDING, SUBMITTED, COMPLETED
    
    @SerializedName("pointsReward")
    private int pointsReward;
    
    @SerializedName("coinsReward")
    private int coinsReward;
    
    @SerializedName("priority")
    private int priority; // 1-5
    
    @SerializedName("isMandatory")
    private boolean isMandatory;
    
    @SerializedName("parentNote")
    private String parentNote;
    
    @SerializedName("isRecurring")
    private boolean isRecurring;
    
    @SerializedName("recurrencePattern")
    private String recurrencePattern; // daily, weekly, mon_wed_fri...
    
    @SerializedName("activeProof")
    private TaskProof activeProof; // Minh chứng hiện tại
    
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
        private String status; // PENDING, APPROVED, REJECTED
        
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
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getChildId() { return childId; }
    public void setChildId(String childId) { this.childId = childId; }

    public String getAssignedBy() { return assignedBy; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public String getExerciseId() { return exerciseId; }
    public void setExerciseId(String exerciseId) { this.exerciseId = exerciseId; }

    public String getHabitTemplateId() { return habitTemplateId; }
    public void setHabitTemplateId(String habitTemplateId) { this.habitTemplateId = habitTemplateId; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getDueTime() { return dueTime; }
    public void setDueTime(String dueTime) { this.dueTime = dueTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getPointsReward() { return pointsReward; }
    public void setPointsReward(int pointsReward) { this.pointsReward = pointsReward; }

    public int getCoinsReward() { return coinsReward; }
    public void setCoinsReward(int coinsReward) { this.coinsReward = coinsReward; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public boolean isMandatory() { return isMandatory; }
    public void setMandatory(boolean mandatory) { isMandatory = mandatory; }

    public String getParentNote() { return parentNote; }
    public void setParentNote(String parentNote) { this.parentNote = parentNote; }

    public boolean isRecurring() { return isRecurring; }
    public void setRecurring(boolean recurring) { isRecurring = recurring; }

    public String getRecurrencePattern() { return recurrencePattern; }
    public void setRecurrencePattern(String recurrencePattern) { this.recurrencePattern = recurrencePattern; }

    public TaskProof getActiveProof() { return activeProof; }
    public void setActiveProof(TaskProof activeProof) { this.activeProof = activeProof; }

    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }

    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }

    public Integer getXpEarned() { return xpEarned; }
    public void setXpEarned(Integer xpEarned) { this.xpEarned = xpEarned; }

    public Integer getCoinsEarned() { return coinsEarned; }
    public void setCoinsEarned(Integer coinsEarned) { this.coinsEarned = coinsEarned; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    
    /**
     * Check if task is EXERCISE type
     */
    public boolean isExercise() {
        return "EXERCISE".equalsIgnoreCase(taskType);
    }
    
    /**
     * Check if task is HABIT type
     */
    public boolean isHabit() {
        return "HABIT".equalsIgnoreCase(taskType);
    }
}

