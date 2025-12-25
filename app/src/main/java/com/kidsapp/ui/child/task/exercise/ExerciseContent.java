package com.kidsapp.ui.child.task.exercise;

/**
 * Model cho nội dung bài tập
 */
public class ExerciseContent {
    private String id;
    private String title;
    private String description;
    private int totalQuestions;
    private int duration; // phút
    private int iconRes;
    private boolean isUnlocked;
    private int completedQuestions;

    public ExerciseContent(String id, String title, String description, 
                          int totalQuestions, int duration, int iconRes, 
                          boolean isUnlocked, int completedQuestions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.totalQuestions = totalQuestions;
        this.duration = duration;
        this.iconRes = iconRes;
        this.isUnlocked = isUnlocked;
        this.completedQuestions = completedQuestions;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getDuration() {
        return duration;
    }

    public int getIconRes() {
        return iconRes;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public int getCompletedQuestions() {
        return completedQuestions;
    }

    public boolean isCompleted() {
        return completedQuestions >= totalQuestions;
    }

    public int getProgressPercent() {
        if (totalQuestions == 0) return 0;
        return (completedQuestions * 100) / totalQuestions;
    }
}
