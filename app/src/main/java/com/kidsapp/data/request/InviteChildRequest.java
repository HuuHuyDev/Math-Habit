package com.kidsapp.data.request;

public class InviteChildRequest {
    private String invitedChildId;

    public InviteChildRequest() {
    }

    public InviteChildRequest(String invitedChildId) {
        this.invitedChildId = invitedChildId;
    }

    public String getInvitedChildId() {
        return invitedChildId;
    }

    public void setInvitedChildId(String invitedChildId) {
        this.invitedChildId = invitedChildId;
    }
}
