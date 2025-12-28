package com.kidsapp.data.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request để parent giao bài cho con
 * Phụ huynh chọn từ template (ExerciseContent hoặc HabitTemplate)
 */
public class CreateTaskRequest {
    
    @SerializedName("childId")
    private String childId;
    
    @SerializedName("taskType")
    private String taskType; // EXERCISE hoặc HABIT
    
    @SerializedName("exerciseId")
    private String exerciseId; // ID của ExerciseContent (khi taskType = EXERCISE)
    
    @SerializedName("habitTemplateId")
    private String habitTemplateId; // ID của HabitTemplate (khi taskType = HABIT)
    
    @SerializedName("dueDate")
    private String dueDate; // yyyy-MM-dd
    
    @SerializedName("dueTime")
    private String dueTime; // HH:mm:ss
    
    @SerializedName("reminderTime")
    private String reminderTime; // HH:mm:ss
    
    @SerializedName("parentNote")
    private String parentNote; // Ghi chú riêng của phụ huynh
    
    @SerializedName("priority")
    private Integer priority; // 1-5
    
    @SerializedName("isMandatory")
    private Boolean isMandatory;
    
    @SerializedName("isRecurring")
    private Boolean isRecurring;
    
    @SerializedName("recurrencePattern")
    private String recurrencePattern; // daily, weekly, mon_wed_fri...

    public CreateTaskRequest() {
    }

    /**
     * Constructor cho EXERCISE task
     */
    public static CreateTaskRequest forExercise(String childId, String exerciseId) {
        CreateTaskRequest request = new CreateTaskRequest();
        request.childId = childId;
        request.taskType = "EXERCISE";
        request.exerciseId = exerciseId;
        return request;
    }
    
    /**
     * Constructor cho HABIT task
     */
    public static CreateTaskRequest forHabit(String childId, String habitTemplateId) {
        CreateTaskRequest request = new CreateTaskRequest();
        request.childId = childId;
        request.taskType = "HABIT";
        request.habitTemplateId = habitTemplateId;
        return request;
    }

    // Getters and Setters
    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
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

    public String getHabitTemplateId() {
        return habitTemplateId;
    }

    public void setHabitTemplateId(String habitTemplateId) {
        this.habitTemplateId = habitTemplateId;
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

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getParentNote() {
        return parentNote;
    }

    public void setParentNote(String parentNote) {
        this.parentNote = parentNote;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
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
