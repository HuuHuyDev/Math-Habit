package com.kidsapp.data.model;

/**
 * Model cho lịch sử nhiệm vụ đã hoàn thành
 */
public class HistoryTask {
    private String title;
    private String completionTime; // Format: "12/11/2025 - 15:30"
    private int coins;
    private int xp;
    private float rating; // 0 nếu không có rating
    private int iconRes; // Icon minh họa nhiệm vụ

    public HistoryTask(String title, String completionTime, int coins, int xp, float rating, int iconRes) {
        this.title = title;
        this.completionTime = completionTime;
        this.coins = coins;
        this.xp = xp;
        this.rating = rating;
        this.iconRes = iconRes;
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
}

