package com.kidsapp.data.request;

/**
 * Request để phụ huynh giao bài tập/thói quen cho con
 */
public class AssignTaskRequest {
    private String childId;
    private String taskType;        // exercise, habit, housework
    
    // Cho bài tập (exercise)
    private String subject;         // math, vietnamese, english
    private Integer difficultyLevel;
    private String exerciseId;
    
    // Cho thói quen (habit)
    private String habitCategory;   // health, study, housework, sport, creativity
    private String habitTemplateId;
    
    // Chung
    private String dueDate;         // yyyy-MM-dd
    private String dueTime;         // HH:mm
    private Boolean isRecurring;
    private String recurrencePattern;
    private Integer pointsReward;

    public AssignTaskRequest() {}

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private AssignTaskRequest request = new AssignTaskRequest();

        public Builder childId(String childId) {
            request.childId = childId;
            return this;
        }

        public Builder taskType(String taskType) {
            request.taskType = taskType;
            return this;
        }

        public Builder subject(String subject) {
            request.subject = subject;
            return this;
        }

        public Builder difficultyLevel(Integer difficultyLevel) {
            request.difficultyLevel = difficultyLevel;
            return this;
        }

        public Builder exerciseId(String exerciseId) {
            request.exerciseId = exerciseId;
            return this;
        }

        public Builder habitCategory(String habitCategory) {
            request.habitCategory = habitCategory;
            return this;
        }

        public Builder habitTemplateId(String habitTemplateId) {
            request.habitTemplateId = habitTemplateId;
            return this;
        }

        public Builder dueDate(String dueDate) {
            request.dueDate = dueDate;
            return this;
        }

        public Builder dueTime(String dueTime) {
            request.dueTime = dueTime;
            return this;
        }

        public Builder isRecurring(Boolean isRecurring) {
            request.isRecurring = isRecurring;
            return this;
        }

        public Builder recurrencePattern(String recurrencePattern) {
            request.recurrencePattern = recurrencePattern;
            return this;
        }

        public Builder pointsReward(Integer pointsReward) {
            request.pointsReward = pointsReward;
            return this;
        }

        public AssignTaskRequest build() {
            return request;
        }
    }

    // Getters and Setters
    public String getChildId() { return childId; }
    public void setChildId(String childId) { this.childId = childId; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public Integer getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(Integer difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public String getExerciseId() { return exerciseId; }
    public void setExerciseId(String exerciseId) { this.exerciseId = exerciseId; }

    public String getHabitCategory() { return habitCategory; }
    public void setHabitCategory(String habitCategory) { this.habitCategory = habitCategory; }

    public String getHabitTemplateId() { return habitTemplateId; }
    public void setHabitTemplateId(String habitTemplateId) { this.habitTemplateId = habitTemplateId; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getDueTime() { return dueTime; }
    public void setDueTime(String dueTime) { this.dueTime = dueTime; }

    public Boolean getIsRecurring() { return isRecurring; }
    public void setIsRecurring(Boolean isRecurring) { this.isRecurring = isRecurring; }

    public String getRecurrencePattern() { return recurrencePattern; }
    public void setRecurrencePattern(String recurrencePattern) { this.recurrencePattern = recurrencePattern; }

    public Integer getPointsReward() { return pointsReward; }
    public void setPointsReward(Integer pointsReward) { this.pointsReward = pointsReward; }
}
