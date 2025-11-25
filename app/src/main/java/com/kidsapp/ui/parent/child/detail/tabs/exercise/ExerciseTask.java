package com.kidsapp.ui.parent.child.detail.tabs.exercise;

/**
 * Model class cho bài tập
 */
public class ExerciseTask {
    private String id;
    private String title;
    private int correctAnswers;
    private int totalQuestions;
    private int xp;
    private int iconRes;

    public ExerciseTask(String id, String title, int correctAnswers, int totalQuestions, int xp, int iconRes) {
        this.id = id;
        this.title = title;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.xp = xp;
        this.iconRes = iconRes;
    }

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

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }
}
