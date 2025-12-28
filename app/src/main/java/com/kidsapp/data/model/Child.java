package com.kidsapp.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model class for Child user
 */
public class Child {
    @SerializedName("id")
    private String id;
    
    @SerializedName("userId")
    private String userId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("avatarUrl")
    private String avatarUrl;
    
    @SerializedName("birthDate")
    private String birthDate;
    
    @SerializedName("grade")
    private Integer grade;
    
    @SerializedName("school")
    private String school;
    
    @SerializedName("nickname")
    private String nickname;
    
    @SerializedName("gender")
    private Boolean gender;  // true = Nam, false = Nữ
    
    @SerializedName("level")
    private int level;
    
    @SerializedName("isOnline")
    private boolean isOnline;
    
    // Tiến độ học tập
    @SerializedName("totalXp")
    private int totalXp;
    
    @SerializedName("coins")
    private int coins;
    
    @SerializedName("currentLevel")
    private int currentLevel;
    
    @SerializedName("xpToNextLevel")
    private int xpToNextLevel;
    
    @SerializedName("currentStreak")
    private int currentStreak;
    
    @SerializedName("longestStreak")
    private int longestStreak;
    
    @SerializedName("totalTasksCompleted")
    private int totalTasksCompleted;
    
    @SerializedName("totalStudyTimeMinutes")
    private int totalStudyTimeMinutes;
    
    // Mục tiêu
    @SerializedName("dailyGoalMinutes")
    private int dailyGoalMinutes;
    
    @SerializedName("dailyGoalExercises")
    private int dailyGoalExercises;
    
    // Tiến độ hoàn thành task trong ngày (0-100%)
    @SerializedName("dailyProgress")
    private float dailyProgress;

    public Child() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public Boolean getGender() {
        return gender;
    }
    
    public void setGender(Boolean gender) {
        this.gender = gender;
    }
    
    /**
     * Lấy giới tính dạng text
     */
    public String getGenderText() {
        if (gender == null) return "";
        return gender ? "Nam" : "Nữ";
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public int getTotalPoints() {
        return totalXp;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalXp = totalPoints;
    }
    
    public int getTotalXp() {
        return totalXp;
    }

    public void setTotalXp(int totalXp) {
        this.totalXp = totalXp;
    }
    
    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }
    
    public int getXpToNextLevel() {
        return xpToNextLevel;
    }

    public void setXpToNextLevel(int xpToNextLevel) {
        this.xpToNextLevel = xpToNextLevel;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public int getTotalTasksCompleted() {
        return totalTasksCompleted;
    }

    public void setTotalTasksCompleted(int totalTasksCompleted) {
        this.totalTasksCompleted = totalTasksCompleted;
    }
    
    public int getTotalExercisesCompleted() {
        return totalTasksCompleted;
    }

    public void setTotalExercisesCompleted(int totalExercisesCompleted) {
        this.totalTasksCompleted = totalExercisesCompleted;
    }

    public int getTotalStudyTimeMinutes() {
        return totalStudyTimeMinutes;
    }

    public void setTotalStudyTimeMinutes(int totalStudyTimeMinutes) {
        this.totalStudyTimeMinutes = totalStudyTimeMinutes;
    }

    public int getDailyGoalMinutes() {
        return dailyGoalMinutes;
    }

    public void setDailyGoalMinutes(int dailyGoalMinutes) {
        this.dailyGoalMinutes = dailyGoalMinutes;
    }

    public int getDailyGoalExercises() {
        return dailyGoalExercises;
    }

    public void setDailyGoalExercises(int dailyGoalExercises) {
        this.dailyGoalExercises = dailyGoalExercises;
    }
    
    public float getDailyProgress() {
        return dailyProgress;
    }
    
    public void setDailyProgress(float dailyProgress) {
        this.dailyProgress = dailyProgress;
    }
    
    /**
     * Tính tuổi từ birthDate (format: yyyy-MM-dd)
     */
    public int getAge() {
        if (birthDate == null || birthDate.isEmpty()) return 0;
        try {
            String[] parts = birthDate.split("-");
            int birthYear = Integer.parseInt(parts[0]);
            int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            return currentYear - birthYear;
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Lấy tên hiển thị (ưu tiên nickname)
     */
    public String getDisplayName() {
        return (nickname != null && !nickname.isEmpty()) ? nickname : name;
    }
}
