package com.kidsapp.ui.parent.task_plan.model;

/**
 * Model cho ngày trong tuần
 */
public class WeekDay {
    private int dayIndex; // 0 = Thứ 2, 6 = Chủ nhật
    private String dayLabel; // T2, T3, T4, T5, T6, T7, CN
    private int totalTasks;
    private int completedTasks;
    private int progress; // Phần trăm hoàn thành

    public WeekDay(int dayIndex, String dayLabel) {
        this.dayIndex = dayIndex;
        this.dayLabel = dayLabel;
        this.totalTasks = 0;
        this.completedTasks = 0;
        this.progress = 0;
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }

    public String getDayLabel() {
        return dayLabel;
    }

    public void setDayLabel(String dayLabel) {
        this.dayLabel = dayLabel;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
        calculateProgress();
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
        calculateProgress();
    }

    public int getProgress() {
        return progress;
    }

    private void calculateProgress() {
        if (totalTasks > 0) {
            progress = (int) ((completedTasks * 100.0) / totalTasks);
        } else {
            progress = 0;
        }
    }

    public String getTaskCountText() {
        return totalTasks + " task";
    }
}
