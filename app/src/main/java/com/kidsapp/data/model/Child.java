package com.kidsapp.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model class for Child user
 */
public class Child {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("avatar")
    private String avatar;
    
    @SerializedName("age")
    private int age;
    
    @SerializedName("parent_id")
    private String parentId;
    
    @SerializedName("total_xp")
    private int totalXP;
    
    @SerializedName("level")
    private int level;
    
    @SerializedName("created_at")
    private String createdAt;

    public Child() {
    }

    public Child(String id, String name, String email, String avatar, int age, 
                 String parentId, int totalXP, int level, String createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.age = age;
        this.parentId = parentId;
        this.totalXP = totalXP;
        this.level = level;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getTotalXP() {
        return totalXP;
    }

    public void setTotalXP(int totalXP) {
        this.totalXP = totalXP;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

