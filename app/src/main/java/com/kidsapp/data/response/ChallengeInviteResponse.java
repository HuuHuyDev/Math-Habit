package com.kidsapp.data.response;

public class ChallengeInviteResponse {
    private String id;
    private String inviteId; // Backend sends inviteId
    private String challengeId;
    private String invitedChildId;
    private String invitedChildName;
    private String status;
    private String createdAt;

    public String getId() {
        // Return inviteId if id is null (backend compatibility)
        return id != null ? id : inviteId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInviteId() {
        return inviteId;
    }

    public void setInviteId(String inviteId) {
        this.inviteId = inviteId;
        // Also set id for backward compatibility
        if (this.id == null) {
            this.id = inviteId;
        }
    }

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public String getInvitedChildId() {
        return invitedChildId;
    }

    public void setInvitedChildId(String invitedChildId) {
        this.invitedChildId = invitedChildId;
    }

    public String getInvitedChildName() {
        return invitedChildName;
    }

    public void setInvitedChildName(String invitedChildName) {
        this.invitedChildName = invitedChildName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Additional methods for enhanced invite flow
    public String getCreatorName() {
        return invitedChildName; // For now, use invitedChildName as creator
    }

    public String getChallengeTitle() {
        return "Thách đấu"; // Default title
    }

    public String getCategoryName() {
        return "Câu đố mẹo"; // Default category
    }

    public String getDescription() {
        return "Thách đấu câu đố mẹo với anh chị em";
    }

    public String getTimeAgo() {
        return "Vừa xong"; // Default time ago
    }
}
