package com.kidsapp.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model class for Badge/Achievement
 */
public class Badge {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("iconUrl")
    private String iconUrl;
    
    @SerializedName("badgeType")
    private String badgeType;
    
    @SerializedName("rarity")
    private String rarity;
    
    @SerializedName("xpReward")
    private int xpReward;
    
    @SerializedName("coinsReward")
    private int coinsReward;
    
    @SerializedName("earned")
    private boolean earned;
    
    @SerializedName("earnedAt")
    private String earnedAt;
    
    @SerializedName("requirementValue")
    private int requirementValue;
    
    @SerializedName("progressValue")
    private int progressValue;
    
    @SerializedName("progressPercent")
    private int progressPercent;

    public Badge() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(String badgeType) {
        this.badgeType = badgeType;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public int getXpReward() {
        return xpReward;
    }

    public void setXpReward(int xpReward) {
        this.xpReward = xpReward;
    }

    public int getCoinsReward() {
        return coinsReward;
    }

    public void setCoinsReward(int coinsReward) {
        this.coinsReward = coinsReward;
    }

    public boolean isEarned() {
        return earned;
    }

    public void setEarned(boolean earned) {
        this.earned = earned;
    }

    public String getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(String earnedAt) {
        this.earnedAt = earnedAt;
    }

    public int getRequirementValue() {
        return requirementValue;
    }

    public void setRequirementValue(int requirementValue) {
        this.requirementValue = requirementValue;
    }

    public int getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(int progressValue) {
        this.progressValue = progressValue;
    }

    public int getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
    }
    
    /**
     * Lấy text hiển thị progress, giới hạn không vượt quá requirement
     */
    public String getProgressText() {
        // Giới hạn progress value không vượt quá requirement value
        int currentProgress = Math.min(progressValue, requirementValue);
        return currentProgress + "/" + requirementValue;
    }
}
