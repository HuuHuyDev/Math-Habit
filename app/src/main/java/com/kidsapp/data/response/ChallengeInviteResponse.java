package com.kidsapp.data.response;

public class ChallengeInviteResponse {
    private String id;
    private String challengeId;
    private String invitedChildId;
    private String invitedChildName;
    private String status;
    private String createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
