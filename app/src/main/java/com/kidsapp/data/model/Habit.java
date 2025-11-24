package com.kidsapp.data.model;

public class Habit {

    private String title;
    private boolean isDone;

    public Habit(String title, boolean isDone) {
        this.title = title;
        this.isDone = isDone;
    }

    public String getTitle() {
        return title;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
