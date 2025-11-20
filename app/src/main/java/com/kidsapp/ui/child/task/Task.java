package com.kidsapp.ui.child.task;

public class Task {
    public static final int TYPE_HOMEWORK = 0; // Việc nhà
    public static final int TYPE_PERSONAL = 1; // Cá nhân
    public static final int TYPE_EXERCISE = 2; // Bài tập
    public static final int TYPE_HISTORY = 3; // Lịch sử

    private String title;
    private int questionCount;
    private int duration; // minutes
    private float rating;
    private int imageRes;
    private int type; // 0: Việc nhà, 1: Cá nhân, 2: Bài tập, 3: Lịch sử

    public Task(String title, int questionCount, int duration, float rating, int imageRes, int type) {
        this.title = title;
        this.questionCount = questionCount;
        this.duration = duration;
        this.rating = rating;
        this.imageRes = imageRes;
        this.type = type;
    }

    // Constructor cũ để backward compatible
    public Task(String title, int questionCount, int duration, float rating, int imageRes) {
        this(title, questionCount, duration, rating, imageRes, TYPE_EXERCISE);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

