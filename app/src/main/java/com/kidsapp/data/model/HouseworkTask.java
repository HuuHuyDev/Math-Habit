package com.kidsapp.data.model;

/**
 * Model cho công việc nhà trong tab "Việc nhà".
 * Lưu trạng thái được chọn (isSelected) và đã hoàn thành (isCompleted).
 */
public class HouseworkTask {

    private String title;
    private String subtitle;
    private boolean isSelected;
    private boolean isCompleted;

    public HouseworkTask(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
        this.isSelected = false;
        this.isCompleted = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}


