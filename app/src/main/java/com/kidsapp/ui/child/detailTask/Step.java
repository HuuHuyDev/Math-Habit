package com.kidsapp.ui.child.detailTask;

public class Step {
    private int index;
    private String title;

    public Step(int index, String title) {
        this.index = index;
        this.title = title;
    }

    public int getIndex() {
        return index;
    }

    public String getTitle() {
        return title;
    }
}
