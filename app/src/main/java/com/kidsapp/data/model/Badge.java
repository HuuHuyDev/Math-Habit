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
    
    @SerializedName("icon")
    private String icon;
    
    @SerializedName("child_id")
    private String childId;
    
    @SerializedName("earned_at")
    private String earnedAt;
    
    @SerializedName("created_at")
    private String createdAt;

    public Badge() {
    }

    public Badge(String id, String name, String description, String icon, 
                 String childId, String earnedAt, String createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.childId = childId;
        this.earnedAt = earnedAt;
        this.createdAt = createdAt;
    }

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(String earnedAt) {
        this.earnedAt = earnedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

