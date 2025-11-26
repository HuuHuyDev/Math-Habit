package com.kidsapp.ui.parent.task_plan.model;

import java.io.Serializable;

/**
 * Model cho nhiệm vụ trong tuần
 */
public class WeekTask implements Serializable {
    private String id;
    private String title;
    private String description;
    private String type; // "habit" hoặc "quiz"
    private int coins;
    private int xp;
    private int level; // 1-5 (chỉ cho quiz)
    private int dayIndex; // Ngày trong tuần (0-6)
    private boolean isCompleted;

    public WeekTask(String id, String title, String description, String type, int coins, int xp, int dayIndex) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.coins = coins;
        this.xp = xp;
        this.dayIndex = dayIndex;
        this.level = 1;
        this.isCompleted = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isQuiz() {
        return "quiz".equalsIgnoreCase(type);
    }

    public boolean isHabit() {
        return "habit".equalsIgnoreCase(type);
    }

    public String getLevelStars() {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < level; i++) {
            stars.append("⭐");
        }
        return stars.toString();
    }

    public String getLevelText() {
        return "Level " + level;
    }
}
