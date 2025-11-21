package com.kidsapp.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model class for Activity Log
 */
public class ActivityLog {
    @SerializedName("id")
    private String id;
    
    @SerializedName("child_id")
    private String childId;
    
    @SerializedName("child_name")
    private String childName;
    
    @SerializedName("action")
    private String action;
    
    @SerializedName("xp_earned")
    private int xpEarned;
    
    @SerializedName("avatar")
    private String avatar;
    
    @SerializedName("icon")
    private String icon;
    
    @SerializedName("created_at")
    private String createdAt;

    public ActivityLog() {
    }

    public ActivityLog(String id, String childId, String childName, String action, 
                       int xpEarned, String avatar, String icon, String createdAt) {
        this.id = id;
        this.childId = childId;
        this.childName = childName;
        this.action = action;
        this.xpEarned = xpEarned;
        this.avatar = avatar;
        this.icon = icon;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getXpEarned() {
        return xpEarned;
    }

    public void setXpEarned(int xpEarned) {
        this.xpEarned = xpEarned;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

