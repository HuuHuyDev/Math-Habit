package com.kidsapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Model class for Weekly Progress
 */
public class WeeklyProgress {
    @SerializedName("dailyProgress")
    private List<DailyProgress> dailyProgress;

    public WeeklyProgress() {
    }

    public WeeklyProgress(List<DailyProgress> dailyProgress) {
        this.dailyProgress = dailyProgress;
    }

    public List<DailyProgress> getDailyProgress() {
        return dailyProgress;
    }

    public void setDailyProgress(List<DailyProgress> dailyProgress) {
        this.dailyProgress = dailyProgress;
    }

    /**
     * Inner class for daily progress
     */
    public static class DailyProgress {
        @SerializedName("date")
        private String date;
        
        @SerializedName("dayLabel")
        private String dayLabel;  // T2, T3, T4, T5, T6, T7, CN
        
        @SerializedName("totalTasks")
        private int totalTasks;
        
        @SerializedName("completedTasks")
        private int completedTasks;
        
        @SerializedName("progressPercent")
        private int progressPercent;

        public DailyProgress() {
        }

        public DailyProgress(String date, String dayLabel, int totalTasks, 
                            int completedTasks, int progressPercent) {
            this.date = date;
            this.dayLabel = dayLabel;
            this.totalTasks = totalTasks;
            this.completedTasks = completedTasks;
            this.progressPercent = progressPercent;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
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
        }

        public int getCompletedTasks() {
            return completedTasks;
        }

        public void setCompletedTasks(int completedTasks) {
            this.completedTasks = completedTasks;
        }

        public int getProgressPercent() {
            return progressPercent;
        }

        public void setProgressPercent(int progressPercent) {
            this.progressPercent = progressPercent;
        }
    }
}

