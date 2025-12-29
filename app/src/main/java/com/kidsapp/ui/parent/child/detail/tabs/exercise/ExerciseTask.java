package com.kidsapp.ui.parent.child.detail.tabs.exercise;

/**
 * Model class cho bài tập
 */
public class ExerciseTask {
    private String id;
    private String title;
    private String status;      // PENDING, COMPLETED
    private int coinReward;     // Coin thưởng
    private int xp;             // XP thưởng
    private int iconRes;

    public ExerciseTask(String id, String title, String status, int coinReward, int xp, int iconRes) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.coinReward = coinReward;
        this.xp = xp;
        this.iconRes = iconRes;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCoinReward() {
        return coinReward;
    }

    public void setCoinReward(int coinReward) {
        this.coinReward = coinReward;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }
    
    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(status);
    }
}
