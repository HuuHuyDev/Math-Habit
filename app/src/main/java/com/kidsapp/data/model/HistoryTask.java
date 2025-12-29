package com.kidsapp.data.model;

/**
 * Model cho lịch sử nhiệm vụ đã hoàn thành
 */
public class HistoryTask {
    private String taskId; // Task ID để gọi API lấy chi tiết
    private String title;
    private String completionTime; // Format: "12/11/2025 - 15:30"
    private int coins;
    private int xp;
    private float rating; // 0 nếu không có rating
    private int iconRes; // Icon minh họa nhiệm vụ
    
    // Additional fields for detail view
    private String taskType; // EXERCISE or HABIT
    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private long durationSeconds; // Thời gian làm bài (giây)
    private int score; // Điểm số (0-100)

    public HistoryTask(String title, String completionTime, int coins, int xp, float rating, int iconRes) {
        this.title = title;
        this.completionTime = completionTime;
        this.coins = coins;
        this.xp = xp;
        this.rating = rating;
        this.iconRes = iconRes;
    }
    
    // Full constructor with all fields
    public HistoryTask(String taskId, String title, String completionTime, int coins, int xp, float rating, int iconRes,
                       String taskType, int totalQuestions, int correctAnswers, int wrongAnswers, 
                       long durationSeconds, int score) {
        this.taskId = taskId;
        this.title = title;
        this.completionTime = completionTime;
        this.coins = coins;
        this.xp = xp;
        this.rating = rating;
        this.iconRes = iconRes;
        this.taskType = taskType;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.durationSeconds = durationSeconds;
        this.score = score;
    }
    
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(String completionTime) {
        this.completionTime = completionTime;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean hasRating() {
        return rating > 0;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }
    
    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    
    public boolean isExercise() {
        return "EXERCISE".equalsIgnoreCase(taskType);
    }
    
    public boolean isHabit() {
        return "HABIT".equalsIgnoreCase(taskType);
    }
    
    /**
     * Format duration to readable string (e.g., "5 phút 30 giây")
     */
    public String getFormattedDuration() {
        if (durationSeconds <= 0) return "N/A";
        
        long minutes = durationSeconds / 60;
        long seconds = durationSeconds % 60;
        
        if (minutes > 0 && seconds > 0) {
            return minutes + " phút " + seconds + " giây";
        } else if (minutes > 0) {
            return minutes + " phút";
        } else {
            return seconds + " giây";
        }
    }
}

