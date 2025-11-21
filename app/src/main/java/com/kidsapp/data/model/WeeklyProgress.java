package com.kidsapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Model class for Weekly Progress
 */
public class WeeklyProgress {
    @SerializedName("week_start")
    private String weekStart;
    
    @SerializedName("week_end")
    private String weekEnd;
    
    @SerializedName("total_tasks")
    private int totalTasks;
    
    @SerializedName("completed_tasks")
    private int completedTasks;
    
    @SerializedName("total_xp")
    private int totalXP;
    
    @SerializedName("daily_progress")
    private List<DailyProgress> dailyProgress;

    public WeeklyProgress() {
    }

    public WeeklyProgress(String weekStart, String weekEnd, int totalTasks, 
                         int completedTasks, int totalXP, List<DailyProgress> dailyProgress) {
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.totalXP = totalXP;
        this.dailyProgress = dailyProgress;
    }

    public String getWeekStart() {
        return weekStart;
    }

    public void setWeekStart(String weekStart) {
        this.weekStart = weekStart;
    }

    public String getWeekEnd() {
        return weekEnd;
    }

    public void setWeekEnd(String weekEnd) {
        this.weekEnd = weekEnd;
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

    public int getTotalXP() {
        return totalXP;
    }

    public void setTotalXP(int totalXP) {
        this.totalXP = totalXP;
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
        
        @SerializedName("tasks_completed")
        private int tasksCompleted;
        
        @SerializedName("xp_earned")
        private int xpEarned;

        public DailyProgress() {
        }

        public DailyProgress(String date, int tasksCompleted, int xpEarned) {
            this.date = date;
            this.tasksCompleted = tasksCompleted;
            this.xpEarned = xpEarned;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getTasksCompleted() {
            return tasksCompleted;
        }

        public void setTasksCompleted(int tasksCompleted) {
            this.tasksCompleted = tasksCompleted;
        }

        public int getXpEarned() {
            return xpEarned;
        }

        public void setXpEarned(int xpEarned) {
            this.xpEarned = xpEarned;
        }
    }
}

