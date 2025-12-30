package com.kidsapp.ui.parent.child.detail.tabs.badge;

/**
 * Model class cho huy hiệu
 */
public class BadgeItem {
    private String id;
    private String name;
    private String description;
    private boolean isUnlocked;
    private int iconRes;
    private String iconUrl;
    private int progressValue;
    private int requirementValue;
    private int progressPercent;
    private String rarity;
    private int xpReward;
    private int coinsReward;

    public BadgeItem(String id, String name, boolean isUnlocked, int iconRes) {
        this.id = id;
        this.name = name;
        this.isUnlocked = isUnlocked;
        this.iconRes = iconRes;
    }
    
    public BadgeItem(String id, String name, String description, boolean isUnlocked, 
                     String iconUrl, int progressValue, int requirementValue, 
                     int progressPercent, String rarity, int xpReward, int coinsReward) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isUnlocked = isUnlocked;
        this.iconUrl = iconUrl;
        this.progressValue = progressValue;
        this.requirementValue = requirementValue;
        this.progressPercent = progressPercent;
        this.rarity = rarity;
        this.xpReward = xpReward;
        this.coinsReward = coinsReward;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }

    public int getIconRes() { return iconRes; }
    public void setIconRes(int iconRes) { this.iconRes = iconRes; }
    
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    
    public int getProgressValue() { return progressValue; }
    public void setProgressValue(int progressValue) { this.progressValue = progressValue; }
    
    public int getRequirementValue() { return requirementValue; }
    public void setRequirementValue(int requirementValue) { this.requirementValue = requirementValue; }
    
    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }
    
    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }
    
    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }
    
    public int getCoinsReward() { return coinsReward; }
    public void setCoinsReward(int coinsReward) { this.coinsReward = coinsReward; }
    
    public String getProgressText() {
        // Giới hạn progress value không vượt quá requirement value
        int currentProgress = Math.min(progressValue, requirementValue);
        return currentProgress + "/" + requirementValue;
    }
}
