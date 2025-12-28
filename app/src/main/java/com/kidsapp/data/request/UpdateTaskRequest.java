package com.kidsapp.data.request;

/**
 * Request DTO để cập nhật task
 * Chỉ cho phép sửa một số trường (không đổi được exerciseId/habitTemplateId)
 */
public class UpdateTaskRequest {
    private String dueDate;      // Format: yyyy-MM-dd
    private String dueTime;      // Format: HH:mm
    private String reminderTime; // Format: HH:mm
    private String parentNote;
    private Integer priority;
    private Boolean isMandatory;
    private Boolean isRecurring;
    private String recurrencePattern;

    public UpdateTaskRequest() {}

    // Getters and Setters
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getDueTime() { return dueTime; }
    public void setDueTime(String dueTime) { this.dueTime = dueTime; }

    public String getReminderTime() { return reminderTime; }
    public void setReminderTime(String reminderTime) { this.reminderTime = reminderTime; }

    public String getParentNote() { return parentNote; }
    public void setParentNote(String parentNote) { this.parentNote = parentNote; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public Boolean getIsMandatory() { return isMandatory; }
    public void setIsMandatory(Boolean isMandatory) { this.isMandatory = isMandatory; }

    public Boolean getIsRecurring() { return isRecurring; }
    public void setIsRecurring(Boolean isRecurring) { this.isRecurring = isRecurring; }

    public String getRecurrencePattern() { return recurrencePattern; }
    public void setRecurrencePattern(String recurrencePattern) { this.recurrencePattern = recurrencePattern; }
}
