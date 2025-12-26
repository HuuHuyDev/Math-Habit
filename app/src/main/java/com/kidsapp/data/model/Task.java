package com.kidsapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Model class for Task
 */
public class Task implements Serializable {
    @SerializedName("id")
    private String id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("child_id")
    private String childId;
    
    @SerializedName("parent_id")
    private String parentId;
    
    @SerializedName("xp_reward")
    private int xpReward;
    
    @SerializedName("status")
    private String status; // pending, in_progress, completed
    
    @SerializedName("due_date")
    private String dueDate;
    
    @SerializedName("completed_at")
    private String completedAt;
    
    @SerializedName("created_at")
    private String createdAt;
    
    // Xác nhận hoàn thành
    @SerializedName("proof_image_url")
    private String proofImageUrl; // URL ảnh chứng minh
    
    @SerializedName("proof_video_url")
    private String proofVideoUrl; // URL video chứng minh
    
    @SerializedName("proof_note")
    private String proofNote; // Ghi chú khi hoàn thành
    
    @SerializedName("is_verified")
    private boolean isVerified; // Phụ huynh đã xác nhận chưa
    
    @SerializedName("verified_at")
    private String verifiedAt; // Thời gian phụ huynh xác nhận
    
    @SerializedName("verified_by")
    private String verifiedBy; // ID phụ huynh xác nhận

    public Task() {
    }

    public Task(String id, String title, String description, String childId, 
                String parentId, int xpReward, String status, String dueDate, 
                String completedAt, String createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.childId = childId;
        this.parentId = parentId;
        this.xpReward = xpReward;
        this.status = status;
        this.dueDate = dueDate;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
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

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getXpReward() {
        return xpReward;
    }

    public void setXpReward(int xpReward) {
        this.xpReward = xpReward;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getProofImageUrl() {
        return proofImageUrl;
    }

    public void setProofImageUrl(String proofImageUrl) {
        this.proofImageUrl = proofImageUrl;
    }

    public String getProofVideoUrl() {
        return proofVideoUrl;
    }

    public void setProofVideoUrl(String proofVideoUrl) {
        this.proofVideoUrl = proofVideoUrl;
    }

    public String getProofNote() {
        return proofNote;
    }

    public void setProofNote(String proofNote) {
        this.proofNote = proofNote;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(String verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }
}

