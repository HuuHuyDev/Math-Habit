package com.kidsapp.ui.child.task.history;

/**
 * Model cho lịch sử bài tập
 */
public class TaskHistory {
    private String title;
    private String date;
    private String result;
    private float rating;

    public TaskHistory(String title, String date, String result, float rating) {
        this.title = title;
        this.date = date;
        this.result = result;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
