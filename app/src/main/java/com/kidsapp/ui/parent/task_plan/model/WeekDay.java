package com.kidsapp.ui.parent.task_plan.model;

import java.util.Calendar;

/**
 * Model cho ngày trong tuần
 */
public class WeekDay {
    private int dayIndex; // 0 = Thứ 2, 6 = Chủ nhật
    private String dayLabel; // T2, T3, T4, T5, T6, T7, CN
    private String dateStr; // yyyy-MM-dd
    private int totalTasks;
    private int completedTasks;
    private int progress; // Phần trăm hoàn thành
    private boolean isPast; // Ngày đã qua
    private boolean isToday; // Ngày hôm nay

    public WeekDay(int dayIndex, String dayLabel) {
        this.dayIndex = dayIndex;
        this.dayLabel = dayLabel;
        this.totalTasks = 0;
        this.completedTasks = 0;
        this.progress = 0;
        this.isPast = false;
        this.isToday = false;
    }

    public WeekDay(int dayIndex, String dayLabel, String dateStr, boolean isPast, boolean isToday) {
        this.dayIndex = dayIndex;
        this.dayLabel = dayLabel;
        this.dateStr = dateStr;
        this.totalTasks = 0;
        this.completedTasks = 0;
        this.progress = 0;
        this.isPast = isPast;
        this.isToday = isToday;
    }

    public int getDayIndex() { return dayIndex; }
    public void setDayIndex(int dayIndex) { this.dayIndex = dayIndex; }

    public String getDayLabel() { return dayLabel; }
    public void setDayLabel(String dayLabel) { this.dayLabel = dayLabel; }

    public String getDateStr() { return dateStr; }
    public void setDateStr(String dateStr) { this.dateStr = dateStr; }

    public int getTotalTasks() { return totalTasks; }
    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
        calculateProgress();
    }

    public int getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
        calculateProgress();
    }

    public int getProgress() { return progress; }

    public boolean isPast() { return isPast; }
    public void setPast(boolean past) { isPast = past; }

    public boolean isToday() { return isToday; }
    public void setToday(boolean today) { isToday = today; }

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
