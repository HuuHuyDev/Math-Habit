package com.example.mathhabit.model;

public class Habit {
    private String name;
    private boolean done;

    public Habit(String name, boolean done) {
        this.name = name;
        this.done = done;
    }

    public String getName() {
        return name;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
